package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
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
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnergizerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleContainer>
        implements IngredientPacketUpdate {
    public static final double ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_ENERGIZER_ENERGY_CONSUMPTION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    private int progress;
    private int maxProgress = ModConfigs.COMMON_ENERGIZER_RECIPE_DURATION.getValue();
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    private int timeoutOffState;

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    public EnergizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ENERGIZER_ENTITY, blockPos, blockState,

                "energizer",

                ModConfigs.COMMON_ENERGIZER_CAPACITY.getValue(),
                ModConfigs.COMMON_ENERGIZER_TRANSFER_RATE.getValue(),

                2,

                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
            @Override
            public long getCapacity() {
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
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> ((level instanceof ServerLevel serverWorld)?
                            RecipeUtils.isIngredientOfAny(serverWorld, EPRecipes.ENERGIZER_TYPE, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
                    case 1 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getItem(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress(worldPosition, level.getBlockState(worldPosition));
                }

                super.setItem(slot, stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                EnergizerBlockEntity.this.setChanged();
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
        
        return new EnergizerMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("recipe.progress", progress);
        view.putLong("recipe.energy_consumption_left", energyConsumptionLeft);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        progress = view.getIntOr("recipe.progress", 0);
        energyConsumptionLeft = view.getLongOr("recipe.energy_consumption_left", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, EnergizerBlockEntity blockEntity) {
        if(level.isClientSide() || !(level instanceof ServerLevel serverWorld))
            return;

        if(blockEntity.timeoutOffState > 0) {
            blockEntity.timeoutOffState--;

            if(blockEntity.timeoutOffState == 0 && level.getBlockState(blockPos).hasProperty(BlockStateProperties.LIT) &&
                    level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, state.setValue(BlockStateProperties.LIT, false), 3);
            }
        }

        if(!blockEntity.redstoneMode.isActive(state.getValue(BlockStateProperties.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            Optional<RecipeHolder<EnergizerRecipe>> recipe = serverWorld.recipeAccess().
                    getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(blockEntity.itemHandler), level);
            if(recipe.isEmpty())
                return;

            long energyConsumption = recipe.get().value().getEnergyConsumption();
            energyConsumption = (long)(energyConsumption * ENERGY_CONSUMPTION_MULTIPLIER);
            if(blockEntity.progress == 0)
                blockEntity.energyConsumptionLeft = energyConsumption;

            long energyConsumptionPerTick = blockEntity.getEnergyConsumptionPerTick();
            if(energyConsumptionPerTick <= blockEntity.energyStorage.getAmount()) {
                blockEntity.hasEnoughEnergy = true;
                blockEntity.timeoutOffState = 0;
                if(level.getBlockState(blockPos).hasProperty(BlockStateProperties.LIT) &&
                        !level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                    level.setBlock(blockPos, state.setValue(BlockStateProperties.LIT, true), 3);
                }

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    setChanged(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
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
        }else {
            blockEntity.resetProgress(blockPos, state);
            if(blockEntity.timeoutOffState == 0) {
                blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
            }
            setChanged(level, blockPos, state);
        }
    }
    
    protected final long getEnergyConsumptionPerTick() {
        if(!(level instanceof ServerLevel serverWorld))
            return -1;

        Optional<RecipeHolder<EnergizerRecipe>> recipe = serverWorld.recipeAccess().
                getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(this.itemHandler), level);
        if(recipe.isEmpty())
            return -1;

        long energyConsumption = recipe.get().value().getEnergyConsumption();
        energyConsumption = (long)(energyConsumption * ENERGY_CONSUMPTION_MULTIPLIER);

        //TODO improve (alternate values +/- 1 per x recipes instead of changing last energy consumption tick)
        long energyConsumptionPerTick = (long)Math.ceil((double)energyConsumption / this.maxProgress);
        if(progress == maxProgress - 1)
            energyConsumptionPerTick = energyConsumptionLeft;

        return energyConsumptionPerTick;
    }

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = false;
    }

    private static void craftItem(BlockPos blockPos, BlockState state, EnergizerBlockEntity blockEntity) {
        Level level = blockEntity.level;

        if(!(level instanceof ServerLevel serverWorld))
            return;

        Optional<RecipeHolder<EnergizerRecipe>> recipe = serverWorld.recipeAccess().
                getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(blockEntity.itemHandler), level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.removeItem(0, 1);
        blockEntity.itemHandler.setItem(1, recipe.get().value().assemble(null).copyWithCount(
                blockEntity.itemHandler.getItem(1).getCount() + recipe.get().value().assemble(null).getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(EnergizerBlockEntity blockEntity) {
        Level level = blockEntity.level;

        if(!(level instanceof ServerLevel serverWorld))
            return false;

        Optional<RecipeHolder<EnergizerRecipe>> recipe = serverWorld.recipeAccess().
                getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(blockEntity.itemHandler), level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(blockEntity.itemHandler, 1, recipe.get().value().assemble(null));
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress(getBlockPos(), getBlockState());

        super.updateUpgradeModules();
    }

    protected void syncIngredientListToPlayer(Player player) {
        if(!(level instanceof ServerLevel serverWorld))
            return;

        ModMessages.sendServerPacketToPlayer((ServerPlayer)player,
                new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf(serverWorld, EPRecipes.ENERGIZER_TYPE)));
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