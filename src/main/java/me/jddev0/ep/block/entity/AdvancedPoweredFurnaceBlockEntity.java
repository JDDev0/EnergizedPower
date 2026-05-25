package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncFurnaceRecipeTypeS2CPacket;
import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import me.jddev0.ep.screen.AdvancedPoweredFurnaceMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AdvancedPoweredFurnaceBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleContainer>
        implements FurnaceRecipeTypePacketUpdate {
    private static final List<@NotNull ResourceLocation> RECIPE_BLACKLIST = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_BLACKLIST.getValue();

    private static final long ENERGY_USAGE_PER_INPUT_PER_TICK = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_INPUT_PER_TICK.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 3, i -> i >= 3 && i < 6);

    private int[] progress = new int[] {
            0, 0, 0
    };
    private int[] maxProgress = new int[] {
            0, 0, 0
    };
    private long[] energyConsumptionLeft = new long[] {
            -1, -1, -1
    };
    private boolean[] hasEnoughEnergy = new boolean[] {
            false, false, false
    };

    private @NotNull RecipeType<? extends AbstractCookingRecipe> recipeType = RecipeType.SMELTING;

    private int timeoutOffState;

    public AdvancedPoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_POWERED_FURNACE_ENTITY, blockPos, blockState,

                "advanced_powered_furnace",

                ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_TRANSFER_RATE.getValue(),

                6,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE,
                UpgradeModuleModifier.ITEM_EJECTOR
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
                    case 0, 1, 2 -> level == null || RecipeUtils.isIngredientOfAny(level, getRecipeForFurnaceModeUpgrade(), stack);
                    case 3, 4, 5 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getItem(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress(slot, worldPosition, level.getBlockState(worldPosition));
                }

                super.setItem(slot, stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                AdvancedPoweredFurnaceBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress[0], value -> progress[0] = value),
                new ProgressValueContainerData(() -> progress[1], value -> progress[1] = value),
                new ProgressValueContainerData(() -> progress[2], value -> progress[2] = value),
                new ProgressValueContainerData(() -> maxProgress[0], value -> maxProgress[0] = value),
                new ProgressValueContainerData(() -> maxProgress[1], value -> maxProgress[1] = value),
                new ProgressValueContainerData(() -> maxProgress[2], value -> maxProgress[2] = value),
                new EnergyValueContainerData(this::getEnergyConsumptionPerTickSum, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[0], value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[1], value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[2], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[0], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[1], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[2], value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        ModMessages.sendServerPacketToPlayer((ServerPlayer)player,
                new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getBlockPos()));

        return new AdvancedPoweredFurnaceMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(nbt, registries);

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.progress." + i, IntTag.valueOf(progress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.max_progress." + i, IntTag.valueOf(maxProgress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_consumption_left." + i, LongTag.valueOf(energyConsumptionLeft[i]));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(nbt, registries);

        for(int i = 0;i < 3;i++)
            progress[i] = nbt.getInt("recipe.progress." + i);
        for(int i = 0;i < 3;i++)
            maxProgress[i] = nbt.getInt("recipe.max_progress." + i);
        for(int i = 0;i < 3;i++)
            energyConsumptionLeft[i] = nbt.getLong("recipe.energy_consumption_left." + i);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
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

        tickRecipe(level, blockPos, state, blockEntity);

        blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        boolean hasNoRecipe = true;
        int hasNotEnoughEnergyCount = 0;
        for(int i = 0;i < 3;i++) {
            if(hasRecipe(i, blockEntity)) {
                hasNoRecipe = false;
                SimpleContainer inventory = new SimpleContainer(2);
                inventory.setItem(0, blockEntity.itemHandler.getItem(i));
                inventory.setItem(1, blockEntity.itemHandler.getItem(3 + i));

                Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = blockEntity.getRecipeFor(inventory, level);
                if(recipe.isEmpty())
                    continue;

                int cookingTime = recipe.get().value().getCookingTime();
                if(blockEntity.maxProgress[i] == 0)
                    //Default Cooking Time = 200 -> maxProgress = 34 (= 200 / 6)
                    blockEntity.maxProgress[i] = Math.max(1, (int)Math.ceil(cookingTime * RECIPE_DURATION_MULTIPLIER / 6.f /
                            blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));

                long energyUsagePerInputPerTick = Math.max(1, (long)Math.ceil(ENERGY_USAGE_PER_INPUT_PER_TICK *
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

                if(blockEntity.energyConsumptionLeft[i] < 0)
                    blockEntity.energyConsumptionLeft[i] = energyUsagePerInputPerTick * blockEntity.maxProgress[i];

                if(energyUsagePerInputPerTick <= blockEntity.energyStorage.getAmount()) {
                    blockEntity.hasEnoughEnergy[i] = true;
                    blockEntity.timeoutOffState = 0;
                    if(level.getBlockState(blockPos).hasProperty(EPBlockStateProperties.WORKING) &&
                            !level.getBlockState(blockPos).getValue(EPBlockStateProperties.WORKING)) {
                        level.setBlock(blockPos, state.setValue(EPBlockStateProperties.WORKING, true), 3);
                    }

                    if(blockEntity.progress[i] < 0 || blockEntity.maxProgress[i] < 0 || blockEntity.energyConsumptionLeft[i] < 0) {
                        //Reset progress for invalid values

                        blockEntity.resetProgress(i, blockPos, state);
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    try(Transaction transaction = Transaction.openOuter()) {
                        blockEntity.energyStorage.extract(energyUsagePerInputPerTick, transaction);
                        transaction.commit();
                    }
                    blockEntity.energyConsumptionLeft[i] -= energyUsagePerInputPerTick;

                    blockEntity.progress[i]++;
                    if(blockEntity.progress[i] >= blockEntity.maxProgress[i])
                        craftItem(i, blockPos, state, blockEntity);

                    setChanged(level, blockPos, state);
                }else {
                    blockEntity.hasEnoughEnergy[i] = false;
                    hasNotEnoughEnergyCount++;
                    setChanged(level, blockPos, state);
                }
            }else {
                blockEntity.resetProgress(i, blockPos, state);
                hasNotEnoughEnergyCount++;
                setChanged(level, blockPos, state);
            }
        }

        //Unlit if nothing is being smelted
        if(hasNoRecipe || hasNotEnoughEnergyCount == 3) {
            if(blockEntity.timeoutOffState == 0) {
                blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
            }
        }
    }
    
    protected final long getEnergyConsumptionPerTickSum() {
        long energyConsumptionSum = -1;

        for(int i = 0;i < 3;i++) {
            if(!hasRecipe(i, this))
                continue;

            SimpleContainer inventory = new SimpleContainer(2);
            inventory.setItem(0, this.itemHandler.getItem(i));
            inventory.setItem(1, this.itemHandler.getItem(3 + i));

            Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = getRecipeFor(inventory, level);
            if(recipe.isEmpty())
                continue;

            long energyConsumption = Math.max(1, (long)Math.ceil(ENERGY_USAGE_PER_INPUT_PER_TICK *
                    upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(energyConsumptionSum == -1)
                energyConsumptionSum = energyConsumption;
            else
                energyConsumptionSum += energyConsumption;

            if(energyConsumptionSum < 0)
                energyConsumptionSum = Long.MAX_VALUE;
        }

        return energyConsumptionSum;
    }

    private void resetProgress(int index, BlockPos blockPos, BlockState state) {
        progress[index] = 0;
        maxProgress[index] = 0;
        energyConsumptionLeft[index] = -1;
        hasEnoughEnergy[index] = false;
    }

    private static void craftItem(int index, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, blockEntity.itemHandler.getItem(index));
        inventory.setItem(1, blockEntity.itemHandler.getItem(3 + index));

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = blockEntity.getRecipeFor(inventory, level);

        if(!hasRecipe(index, blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.removeItem(index, 1);
        blockEntity.itemHandler.setItem(3 + index, recipe.get().value().getResultItem(level.registryAccess()).copyWithCount(
                blockEntity.itemHandler.getItem(3 + index).getCount() + recipe.get().value().getResultItem(level.registryAccess()).getCount()));

        blockEntity.resetProgress(index, blockPos, state);
    }

    private static boolean hasRecipe(int index, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, blockEntity.itemHandler.getItem(index));
        inventory.setItem(1, blockEntity.itemHandler.getItem(3 + index));

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = blockEntity.getRecipeFor(inventory, level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(blockEntity.itemHandler, 3 + index, recipe.get().value().getResultItem(level.registryAccess()));
    }

    private Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> getRecipeFor(Container container, Level level) {
        return level.getRecipeManager().getAllRecipesFor(getRecipeForFurnaceModeUpgrade()).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.id())).
                filter(recipe -> recipe.value().matches(new SingleRecipeInput(container.getItem(0)), level)).
                findFirst();
    }

    public RecipeType<? extends AbstractCookingRecipe> getRecipeForFurnaceModeUpgrade() {
        if(level != null && level.isClientSide())
            return recipeType;

        double value = upgradeModuleInventory.getUpgradeModuleModifierEffect(3, UpgradeModuleModifier.FURNACE_MODE);
        if(value == 1)
            return RecipeType.BLASTING;
        else if(value == 2)
            return RecipeType.SMOKING;

        return RecipeType.SMELTING;
    }

    protected void recalculateProgress(int index) {
        if(!hasRecipe(index, this) || this.maxProgress[index] <= 0)
            return;

        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, this.itemHandler.getItem(index));
        inventory.setItem(1, this.itemHandler.getItem(3 + index));

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = getRecipeFor(inventory, level);
        if(recipe.isEmpty()) {
            return;
        }

        int currentMaxProgress = this.maxProgress[index];

        int cookingTime = recipe.get().value().getCookingTime();
        //Default Cooking Time = 200 -> maxProgress = 34 (= 200 / 6)
        this.maxProgress[index] = Math.max(1, (int)Math.ceil(cookingTime * RECIPE_DURATION_MULTIPLIER / 6.f /
                this.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));

        if(this.maxProgress[index] != currentMaxProgress) {
            this.progress[index] = this.progress[index] * this.maxProgress[index] / currentMaxProgress;
        }

        int energyUsagePerInputPerTick = Math.max(1, (int)Math.ceil(ENERGY_USAGE_PER_INPUT_PER_TICK *
                this.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));
        this.energyConsumptionLeft[index] = energyUsagePerInputPerTick * (this.maxProgress[index] - this.progress[index]);
    }

    @Override
    public void setRecipeType(RecipeType<? extends AbstractCookingRecipe> recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    protected void updateUpgradeModules() {
        for(int i = 0;i < 3;i++)
            recalculateProgress(i);

        super.updateUpgradeModules();

        if(level != null && !level.isClientSide()) {
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getBlockPos(), (ServerLevel)level, 32,
                    new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getBlockPos())
            );
        }
    }
}