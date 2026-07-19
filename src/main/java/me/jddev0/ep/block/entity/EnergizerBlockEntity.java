package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.data.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.EnergizerRecipe;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import me.jddev0.ep.screen.EnergizerMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnergizerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler>
        implements IngredientPacketUpdate {
    public static final float ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_ENERGIZER_ENERGY_CONSUMPTION_MULTIPLIER.getValue();
    public static final int RECIPE_DURATION = ModConfigs.COMMON_ENERGIZER_RECIPE_DURATION.getValue();

    private static final int ITEM_SLOT_INPUT = 0;
    private static final int ITEM_SLOT_OUTPUT = 1;

    private int progress;
    private int maxProgress;
    private int energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    private int timeoutOffState;

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    public EnergizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ENERGIZER_ENTITY.get(), blockPos, blockState,

                "energizer",

                ModConfigs.COMMON_ENERGIZER_CAPACITY.getValue(),
                ModConfigs.COMMON_ENERGIZER_TRANSFER_RATE.getValue(),

                2,

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING,
                UpgradeModuleModifier.ENERGIZING_SPEED
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
            @Override
            public long getCapacityAsLong() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0) {
            @Override
            public int getMaxInsert() {
                return Math.max(1, (int)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemResource resource) {
                ItemStack stack = resource.toStack();

                return switch (slot) {
                    case 0 -> (level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, EPRecipes.ENERGIZER_TYPE.get(), stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack);
                    case 1 -> false;
                    default -> super.isValid(slot, resource);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(worldPosition, level.getBlockState(worldPosition));
                }

                setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(() -> hasRecipe(this)?getEnergyConsumptionPerTick():-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft, value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncIngredientListToPlayer(player);
        syncIOConfigurationToPlayer(player);

        return new EnergizerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        if(slotType == SlotType.ITEM) {
            return List.of(
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT)),

                    //Output only
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Input & Output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT))
            );
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.ITEM) {
            for(RelativeDirection direction:RelativeDirection.values())
                conf.setSlotGroupId(direction, 2);
        }

        return conf;
    }

    public @Nullable ResourceHandler<ItemResource> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandler, facing, side);
    }

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("recipe.progress", progress);
        view.putInt("recipe.energy_consumption_left", energyConsumptionLeft);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        progress = view.getIntOr("recipe.progress", 0);
        energyConsumptionLeft = view.getIntOr("recipe.energy_consumption_left", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, EnergizerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.timeoutOffState > 0) {
            blockEntity.timeoutOffState--;

            if(blockEntity.timeoutOffState == 0 && level.getBlockState(blockPos).hasProperty(EPBlockStateProperties.WORKING) &&
                    level.getBlockState(blockPos).getValue(EPBlockStateProperties.WORKING)) {
                level.setBlock(blockPos, state.setValue(EPBlockStateProperties.WORKING, false), 3);
            }
        }

        if(!blockEntity.redstoneMode.isActive(state.getValue(BlockStateProperties.POWERED)))
            return;

        blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));

        tickRecipe(level, blockPos, state, blockEntity);

        blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, EnergizerBlockEntity blockEntity) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        if(hasRecipe(blockEntity)) {
            SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.size());
            for(int i = 0;i < blockEntity.itemHandler.size();i++)
                inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

            Optional<RecipeHolder<EnergizerRecipe>> recipe = serverLevel.recipeAccess().
                    getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);
            if(recipe.isEmpty())
                return;

            //Increment progress before starting recipe: prevents flickering (The initial recipe will complete one tick later, but the following will be correct)
            int energyConsumptionPerTick = blockEntity.getEnergyConsumptionPerTick();
            if(blockEntity.maxProgress > 0 && energyConsumptionPerTick <= blockEntity.energyStorage.getAmountAsInt()) {
                blockEntity.hasEnoughEnergy = true;
                blockEntity.timeoutOffState = 0;
                if(level.getBlockState(blockPos).hasProperty(EPBlockStateProperties.WORKING) &&
                        !level.getBlockState(blockPos).getValue(EPBlockStateProperties.WORKING)) {
                    level.setBlock(blockPos, state.setValue(EPBlockStateProperties.WORKING, true), 3);
                }

                if(blockEntity.progress < 0 || blockEntity.energyConsumptionLeft < 0 || energyConsumptionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    setChanged(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.open(null)) {
                    blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);
                    transaction.commit();
                }
                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    craftItem(blockPos, state, blockEntity);

                setChanged(level, blockPos, state);
            }else {
                blockEntity.hasEnoughEnergy = false;
                if(blockEntity.timeoutOffState == 0) {
                    blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
                }
                setChanged(level, blockPos, state);
            }

            if(blockEntity.maxProgress <= 0) {
                blockEntity.maxProgress = Math.max(1, (int)Math.ceil(RECIPE_DURATION /
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGIZING_SPEED)));

                int energyConsumption = recipe.get().value().getEnergyConsumption();
                energyConsumption = (int)(energyConsumption * ENERGY_CONSUMPTION_MULTIPLIER);
                blockEntity.energyConsumptionLeft = energyConsumption;

                if(blockEntity.getEnergyConsumptionPerTick() <= blockEntity.energyStorage.getAmountAsInt()) {
                    blockEntity.hasEnoughEnergy = true;
                    blockEntity.timeoutOffState = 0;
                    if(level.getBlockState(blockPos).hasProperty(EPBlockStateProperties.WORKING) &&
                            !level.getBlockState(blockPos).getValue(EPBlockStateProperties.WORKING)) {
                        level.setBlock(blockPos, state.setValue(EPBlockStateProperties.WORKING, true), 3);
                    }
                }
            }

            setChanged(level, blockPos, state);
        }else {
            blockEntity.resetProgress(blockPos, state);
            if(blockEntity.timeoutOffState == 0) {
                blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
            }
            setChanged(level, blockPos, state);
        }
    }
    
    protected final int getEnergyConsumptionPerTick() {
        if(!(level instanceof ServerLevel serverLevel))
            return -1;

        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for(int i = 0;i < itemHandler.size();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<EnergizerRecipe>> recipe = serverLevel.recipeAccess().
                getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);
        if(recipe.isEmpty())
            return -1;

        //TODO improve (alternate values +/- 1 per x recipes instead of changing last energy consumption tick)
        int energyConsumptionPerTick = (int)Math.ceil((float)energyConsumptionLeft / (maxProgress - progress));
        if(progress == maxProgress - 1)
            energyConsumptionPerTick = energyConsumptionLeft;

        return energyConsumptionPerTick;
    }

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
        maxProgress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = false;
    }

    private static void craftItem(BlockPos blockPos, BlockState state, EnergizerBlockEntity blockEntity) {
        Level level = blockEntity.level;

        if(!(level instanceof ServerLevel serverLevel))
            return;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.size());
        for(int i = 0;i < blockEntity.itemHandler.size();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<EnergizerRecipe>> recipe = serverLevel.recipeAccess().
                getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.extractItem(0, 1);
        blockEntity.itemHandler.setStackInSlot(1, recipe.get().value().assemble(null).copyWithCount(
                blockEntity.itemHandler.getStackInSlot(1).getCount() + recipe.get().value().assemble(null).getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(EnergizerBlockEntity blockEntity) {
        Level level = blockEntity.level;

        if(!(level instanceof ServerLevel serverLevel))
            return false;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.size());
        for(int i = 0;i < blockEntity.itemHandler.size();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<EnergizerRecipe>> recipe = serverLevel.recipeAccess().
                getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.get().value().assemble(null));
    }

    protected void recalculateProgress() {
        if(!hasRecipe(this) || this.maxProgress <= 0)
            return;

        int currentMaxProgress = this.maxProgress;

        this.maxProgress = Math.max(1, (int)Math.ceil(RECIPE_DURATION /
                this.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGIZING_SPEED)));
        if(this.maxProgress != currentMaxProgress) {
            this.progress = Math.min(this.progress * this.maxProgress / currentMaxProgress, currentMaxProgress - 1);
        }

        //Energy consumption left stays the same
    }

    @Override
    protected void updateUpgradeModules() {
        recalculateProgress();

        super.updateUpgradeModules();
    }

    protected void syncIngredientListToPlayer(Player player) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        ModMessages.sendToPlayer(new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf(serverLevel, EPRecipes.ENERGIZER_TYPE.get())), (ServerPlayer)player);
    }

    public List<Ingredient> getIngredientsOfRecipes() {
        return new ArrayList<>(ingredientsOfRecipes);
    }

    @Override
    public void setIngredients(int index, List<Ingredient> ingredients) {
        if(index == 0)
            this.ingredientsOfRecipes = ingredients;
    }
}