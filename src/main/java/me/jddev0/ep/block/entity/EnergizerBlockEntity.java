package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.EnergizerRecipe;
import me.jddev0.ep.screen.EnergizerMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.Optional;

public class EnergizerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleInventory> {
    public static final double ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_ENERGIZER_ENERGY_CONSUMPTION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    private int progress;
    private int maxProgress = ModConfigs.COMMON_ENERGIZER_RECIPE_DURATION.getValue();
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    private int timeoutOffState;

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
                markDirty();
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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> world == null || RecipeUtils.isIngredientOfAny(world, EnergizerRecipe.Type.INSTANCE, stack);
                    case 1 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.canCombine(stack, itemStack))
                        resetProgress(pos, world.getBlockState(pos));
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                EnergizerBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected PropertyDelegate initContainerData() {
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
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        
        return new EnergizerMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, EnergizerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(blockEntity.timeoutOffState > 0) {
            blockEntity.timeoutOffState--;

            if(blockEntity.timeoutOffState == 0 && level.getBlockState(blockPos).contains(Properties.LIT) &&
                    level.getBlockState(blockPos).get(Properties.LIT)) {
                level.setBlockState(blockPos, state.with(Properties.LIT, false), 3);
            }
        }

        if(!blockEntity.redstoneMode.isActive(state.get(Properties.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            Optional<EnergizerRecipe> recipe = level.getRecipeManager().getFirstMatch(EnergizerRecipe.Type.INSTANCE, blockEntity.itemHandler, level);
            if(recipe.isEmpty())
                return;

            long energyConsumption = recipe.get().getEnergyConsumption();
            energyConsumption = (long)(energyConsumption * ENERGY_CONSUMPTION_MULTIPLIER);
            if(blockEntity.progress == 0)
                blockEntity.energyConsumptionLeft = energyConsumption;

            long energyConsumptionPerTick = blockEntity.getEnergyConsumptionPerTick();
            if(energyConsumptionPerTick <= blockEntity.energyStorage.getAmount()) {
                blockEntity.hasEnoughEnergy = true;
                blockEntity.timeoutOffState = 0;
                if(level.getBlockState(blockPos).contains(Properties.LIT) &&
                        !level.getBlockState(blockPos).get(Properties.LIT)) {
                    level.setBlockState(blockPos, state.with(Properties.LIT, true), 3);                }

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    markDirty(level, blockPos, state);

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

                markDirty(level, blockPos, state);
            }else {
                blockEntity.hasEnoughEnergy = false;
                if(blockEntity.timeoutOffState == 0) {
                    blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
                }
                markDirty(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress(blockPos, state);
            if(blockEntity.timeoutOffState == 0) {
                blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
            }
            markDirty(level, blockPos, state);
        }
    }
    
    protected final long getEnergyConsumptionPerTick() {
        if(world == null)
            return -1;

        Optional<EnergizerRecipe> recipe = world.getRecipeManager().
                getFirstMatch(EnergizerRecipe.Type.INSTANCE, this.itemHandler, world);
        if(recipe.isEmpty())
            return -1;

        long energyConsumption = recipe.get().getEnergyConsumption();
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
        World level = blockEntity.world;

        Optional<EnergizerRecipe> recipe = level.getRecipeManager().getFirstMatch(EnergizerRecipe.Type.INSTANCE, blockEntity.itemHandler, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.removeStack(0, 1);
        blockEntity.itemHandler.setStack(1, recipe.get().getOutput(level.getRegistryManager()).copyWithCount(
                blockEntity.itemHandler.getStack(1).getCount() + recipe.get().getOutput(level.getRegistryManager()).getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(EnergizerBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<EnergizerRecipe> recipe = level.getRecipeManager().getFirstMatch(EnergizerRecipe.Type.INSTANCE, blockEntity.itemHandler, level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(blockEntity.itemHandler, 1, recipe.get().getOutput(level.getRegistryManager()));
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress(getPos(), getCachedState());

        super.updateUpgradeModules();
    }
}