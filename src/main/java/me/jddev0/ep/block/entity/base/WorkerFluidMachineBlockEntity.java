package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.fluid.IEnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.IEnergizedPowerItemStackHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Optional;

public abstract class WorkerFluidMachineBlockEntity<W>
        extends ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <EnergizedPowerEnergyStorage, IEnergizedPowerItemStackHandler, IEnergizedPowerFluidStorage> {
    protected final int baseEnergyConsumptionPerTickPerRecipe;
    protected final int workerThreadCount;
    protected final int baseWorkDuration;

    protected final int[] progress;
    protected final int[] maxProgress;
    protected final int[] energyConsumptionLeft;

    protected final boolean[] hasEnoughEnergy;

    protected int timeoutOffState;

    public WorkerFluidMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                         String machineName,
                                         int slotCount, int baseWorkDuration,
                                         int baseEnergyCapacity, int baseEnergyTransferRate, int baseEnergyConsumptionPerTickPerRecipe,
                                         int baseTankCapacity,
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
        energyConsumptionLeft = new int[workerThreadCount];
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
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        for(int i = 0;i < workerThreadCount;i++) {
            view.putInt("recipe.progress." + i, progress[i]);
            view.putInt("recipe.max_progress." + i, maxProgress[i]);
            view.putInt("recipe.energy_consumption_left." + i, energyConsumptionLeft[i]);
        }
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        for(int i = 0;i < workerThreadCount;i++) {
            progress[i] = view.getIntOr("recipe.progress." + i, 0);
            maxProgress[i] = view.getIntOr("recipe.max_progress." + i, 0);
            energyConsumptionLeft[i] = view.getIntOr("recipe.energy_consumption_left." + i, 0);
        }

        //Ensure compatibility with older versions: Override recipe progress data for thread 0
        if(view.getInt("recipe.progress").isPresent()) {
            progress[0] = view.getIntOr("recipe.progress", 0);
            maxProgress[0] = view.getIntOr("recipe.max_progress", 0);
            energyConsumptionLeft[0] = view.getIntOr("recipe.energy_consumption_left", 0);
        }
    }

    public static <W> void tick(Level level, BlockPos blockPos, BlockState state, WorkerFluidMachineBlockEntity<W> blockEntity) {
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

                if(blockEntity.maxProgress[i] == 0) {
                    blockEntity.onWorkStarted(i, workData.get());

                    blockEntity.maxProgress[i] = blockEntity.getWorkDurationFor(i, workData.get());
                }

                int energyConsumptionPerTick = blockEntity.getEnergyConsumptionFor(i, workData.get());

                if(blockEntity.energyConsumptionLeft[i] < 0)
                    blockEntity.energyConsumptionLeft[i] = energyConsumptionPerTick * blockEntity.maxProgress[i];

                if(energyConsumptionPerTick <= blockEntity.energyStorage.getAmountAsInt()) {
                    blockEntity.hasEnoughEnergy[i] = true;
                    blockEntity.timeoutOffState = 0;
                    blockEntity.onHasEnoughEnergy();

                    if(blockEntity.progress[i] < 0 || blockEntity.maxProgress[i] < 0 || blockEntity.energyConsumptionLeft[i] < 0) {
                        //Reset progress for invalid values

                        blockEntity.resetProgress(i);
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    try(Transaction transaction = Transaction.open(null)) {
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

    protected final int getEnergyConsumptionFor(int thread, W workData) {
        return Math.max(1, (int)Math.ceil(baseEnergyConsumptionPerTickPerRecipe *
                getWorkDataDependentEnergyConsumption(thread, workData) *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));
    }

    protected double getWorkDataDependentWorkDuration(int thread, W workData) {
        return 1;
    }

    protected double getWorkDataDependentEnergyConsumption(int thread, W workData) {
        return 1;
    }

    protected final int getEnergyConsumptionPerTickSum() {
        int energyConsumptionSum = -1;

        for(int i = 0;i < workerThreadCount;i++) {
            if(!hasWork(i))
                continue;

            Optional<W> workData = getCurrentWorkData(i);
            if(workData.isEmpty())
                continue;

            int energyConsumption = getEnergyConsumptionFor(i, workData.get());

            if(energyConsumptionSum == -1)
                energyConsumptionSum = energyConsumption;
            else
                energyConsumptionSum += energyConsumption;

            if(energyConsumptionSum < 0)
                energyConsumptionSum = Integer.MAX_VALUE;
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

        int energyConsumptionPerTick = getEnergyConsumptionFor(thread, workData.get());
        this.energyConsumptionLeft[thread] = energyConsumptionPerTick * (this.maxProgress[thread] - this.progress[thread]);
    }

    @Override
    protected void updateUpgradeModules() {
        for(int i = 0;i < workerThreadCount;i++)
            recalculateProgress(i);

        super.updateUpgradeModules();
    }
}