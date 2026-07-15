package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.fluid.IEnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.IEnergizedPowerItemStackHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class WorkerFluidMachineBlockEntity<W>
        extends ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <EnergizedPowerEnergyStorage, IEnergizedPowerItemStackHandler, IEnergizedPowerFluidStorage> {
    protected final long baseEnergyConsumptionPerTickPerRecipe;
    protected final int workerThreadCount;
    protected final int baseWorkDuration;

    protected final int[] progress;
    protected final int[] maxProgress;
    protected final long[] energyConsumptionLeft;

    protected final boolean[] hasEnoughEnergy;

    protected int timeoutOffState;

    public WorkerFluidMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                         String machineName,
                                         int slotCount, int baseWorkDuration,
                                         long baseEnergyCapacity, long baseEnergyTransferRate, long baseEnergyConsumptionPerTickPerRecipe,
                                         long baseTankCapacity,
                                         UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate, slotCount, baseTankCapacity,
                upgradeModifierSlots);

        this.baseEnergyConsumptionPerTickPerRecipe = baseEnergyConsumptionPerTickPerRecipe;
        this.workerThreadCount = initWorkerThreadCount();
        this.baseWorkDuration = baseWorkDuration;

        if(workerThreadCount <= 0)
            throw new IllegalArgumentException("Worker Thread Count must be >= 0");

        progress = new int[workerThreadCount];
        maxProgress = new int[workerThreadCount];
        energyConsumptionLeft = new long[workerThreadCount];
        for(int i = 0;i < workerThreadCount;i++)
            energyConsumptionLeft[i] = -1;

        hasEnoughEnergy = new boolean[workerThreadCount];
    }

    protected int initWorkerThreadCount() {
        return 1;
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
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
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        for(int i = 0;i < workerThreadCount;i++) {
            nbt.putInt("recipe.progress." + i, progress[i]);
            nbt.putInt("recipe.max_progress." + i, maxProgress[i]);
            nbt.putLong("recipe.energy_consumption_left." + i, energyConsumptionLeft[i]);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        for(int i = 0;i < workerThreadCount;i++) {
            progress[i] = nbt.getInt("recipe.progress." + i);
            maxProgress[i] = nbt.getInt("recipe.max_progress." + i);
            energyConsumptionLeft[i] = nbt.getLong("recipe.energy_consumption_left." + i);
        }

        //Ensure compatibility with older versions: Override recipe progress data for thread 0
        if(nbt.contains("recipe.progress")) {
            progress[0] = nbt.getInt("recipe.progress");
            maxProgress[0] = nbt.getInt("recipe.max_progress");
            energyConsumptionLeft[0] = nbt.getLong("recipe.energy_consumption_left");
        }
    }

    public static <W> void tick(
            Level level, BlockPos blockPos, BlockState state, WorkerFluidMachineBlockEntity<W> blockEntity) {
        if(level.isClientSide())
            return;

        blockEntity.onTickStart();

        if(blockEntity.timeoutOffState > 0) {
            blockEntity.timeoutOffState--;

            if(blockEntity.timeoutOffState == 0) {
                blockEntity.onHasNotEnoughEnergyWithOffTimeout();
            }
        }

        if(!blockEntity.redstoneMode.isActive(state.getValue(BlockStateProperties.POWERED)))
            return;

        blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));

        tickRecipe(level, blockPos, state, blockEntity);

        blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));

        blockEntity.onTickEnd();
    }

    private static <W> void tickRecipe(Level level, BlockPos blockPos, BlockState state, WorkerFluidMachineBlockEntity<W> blockEntity) {
        if(level.isClientSide())
            return;

        boolean hasNoWork = true;
        int hasNotEnoughEnergyCount = 0;
        for(int i = 0;i < blockEntity.workerThreadCount;i++) {
            if(blockEntity.hasWork(i)) {
                hasNoWork = false;

                Optional<W> workData = blockEntity.getCurrentWorkData(i);
                if(workData.isEmpty()) {
                    continue;
                }

                //Increment progress before starting recipe: prevents flickering (The initial recipe will complete one tick later, but the following will be correct)
                long energyConsumptionPerTick = blockEntity.getEnergyConsumptionFor(i, workData.get());
                if(blockEntity.maxProgress[i] > 0 && energyConsumptionPerTick <= blockEntity.energyStorage.getAmount()) {
                    blockEntity.hasEnoughEnergy[i] = true;
                    blockEntity.timeoutOffState = 0;
                    blockEntity.onHasEnoughEnergy();

                    if(blockEntity.progress[i] < 0 || blockEntity.energyConsumptionLeft[i] < 0) {
                        //Reset progress for invalid values

                        blockEntity.resetProgress(i);
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    try(Transaction transaction = Transaction.openOuter()) {
                        blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);
                        transaction.commit();
                    }
                    blockEntity.energyConsumptionLeft[i] -= energyConsumptionPerTick;

                    blockEntity.onWorkTicked(i, workData.get());

                    blockEntity.progress[i]++;
                    if(blockEntity.progress[i] >= blockEntity.maxProgress[i])
                        blockEntity.onWorkCompleted(i, workData.get());

                    setChanged(level, blockPos, state);
                }else {
                    blockEntity.hasEnoughEnergy[i] = false;
                    hasNotEnoughEnergyCount++;
                    setChanged(level, blockPos, state);
                }

                if(blockEntity.maxProgress[i] <= 0) {
                    blockEntity.onWorkStarted(i, workData.get());

                    blockEntity.maxProgress[i] = blockEntity.getWorkDurationFor(i, workData.get());

                    energyConsumptionPerTick = blockEntity.getEnergyConsumptionFor(i, workData.get());
                    blockEntity.energyConsumptionLeft[i] = energyConsumptionPerTick * blockEntity.maxProgress[i];

                    if(energyConsumptionPerTick <= blockEntity.energyStorage.getAmount()) {
                        blockEntity.hasEnoughEnergy[i] = true;
                        blockEntity.timeoutOffState = 0;
                        blockEntity.onHasEnoughEnergy();
                    }
                }
            }else {
                blockEntity.resetProgress(i);
                hasNotEnoughEnergyCount++;
                setChanged(level, blockPos, state);
            }
        }

        //Unlit if nothing is being worked on
        if(hasNoWork || hasNotEnoughEnergyCount == blockEntity.workerThreadCount) {
            if(blockEntity.timeoutOffState == 0) {
                blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
            }
            blockEntity.onHasNotEnoughEnergy();
        }
    }

    protected void onTickStart() {}

    protected void onTickEnd() {}

    protected void onHasEnoughEnergy() {
        if(level.getBlockState(getBlockPos()).hasProperty(EPBlockStateProperties.WORKING) &&
                !level.getBlockState(getBlockPos()).getValue(EPBlockStateProperties.WORKING)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(EPBlockStateProperties.WORKING, true), 3);
        }
    }

    protected void onHasNotEnoughEnergy() {}

    protected void onHasNotEnoughEnergyWithOffTimeout() {
        if(level.getBlockState(getBlockPos()).hasProperty(EPBlockStateProperties.WORKING) &&
                level.getBlockState(getBlockPos()).getValue(EPBlockStateProperties.WORKING)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(EPBlockStateProperties.WORKING, false), 3);
        }
    }

    protected final int getWorkDurationFor(int thread, W workData) {
        return Math.max(1, (int)Math.ceil(baseWorkDuration * getWorkDataDependentWorkDuration(thread, workData) /
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));
    }

    protected final long getEnergyConsumptionFor(int thread, W workData) {
        return Math.max(1, (long)Math.ceil(baseEnergyConsumptionPerTickPerRecipe *
                getWorkDataDependentEnergyConsumption(thread, workData) *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));
    }

    protected double getWorkDataDependentWorkDuration(int thread, W workData) {
        return 1;
    }

    protected double getWorkDataDependentEnergyConsumption(int thread, W workData) {
        return 1;
    }

    protected final long getEnergyConsumptionPerTickSum() {
        long energyConsumptionSum = -1;

        for(int i = 0;i < workerThreadCount;i++) {
            if(!hasWork(i))
                continue;

            Optional<W> workData = getCurrentWorkData(i);
            if(workData.isEmpty())
                continue;

            long energyConsumption = getEnergyConsumptionFor(i, workData.get());

            if(energyConsumptionSum == -1)
                energyConsumptionSum = energyConsumption;
            else
                energyConsumptionSum += energyConsumption;

            if(energyConsumptionSum < 0)
                energyConsumptionSum = Long.MAX_VALUE;
        }

        return energyConsumptionSum;
    }

    protected abstract boolean hasWork(int thread);

    protected abstract Optional<W> getCurrentWorkData(int thread);

    protected abstract void onWorkStarted(int thread, W workData);

    protected void onWorkTicked(int thread, W workData) {}

    protected abstract void onWorkCompleted(int thread, W workData);

    protected void resetProgress(int thread) {
        progress[thread] = 0;
        maxProgress[thread] = 0;
        energyConsumptionLeft[thread] = -1;
        hasEnoughEnergy[thread] = false;
    }

    protected void recalculateProgress(int thread) {
        if(!hasWork(thread) || this.maxProgress[thread] <= 0)
            return;

        Optional<W> workData = getCurrentWorkData(thread);
        if(workData.isEmpty()) {
            return;
        }

        int currentMaxProgress = this.maxProgress[thread];

        this.maxProgress[thread] = getWorkDurationFor(thread, workData.get());
        if(this.maxProgress[thread] != currentMaxProgress) {
            this.progress[thread] = this.progress[thread] * this.maxProgress[thread] / currentMaxProgress;
        }

        long energyConsumptionPerTick = getEnergyConsumptionFor(thread, workData.get());
        this.energyConsumptionLeft[thread] = energyConsumptionPerTick * (this.maxProgress[thread] - this.progress[thread]);
    }

    @Override
    protected void updateUpgradeModules() {
        for(int i = 0;i < workerThreadCount;i++)
            recalculateProgress(i);

        super.updateUpgradeModules();
    }
}