package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class WorkerMachineBlockEntity<W>
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleInventory> {
    protected final long baseEnergyConsumptionPerTick;
    protected final int baseWorkDuration;

    protected int progress;
    protected int maxProgress;
    protected long energyConsumptionLeft = -1;
    protected boolean hasEnoughEnergy;

    protected int timeoutOffState;

    public WorkerMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                    String machineName,
                                    int slotCount, int baseWorkDuration,
                                    long baseEnergyCapacity, long baseEnergyTransferRate, long baseEnergyConsumptionPerTick,
                                    UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate, slotCount, upgradeModifierSlots);

        this.baseEnergyConsumptionPerTick = baseEnergyConsumptionPerTick;
        this.baseWorkDuration = baseWorkDuration;
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
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.max_progress", NbtInt.of(maxProgress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");
    }

    public static <W> void tick(World level, BlockPos blockPos, BlockState state, WorkerMachineBlockEntity<W> blockEntity) {
        if(level.isClient)
            return;

        blockEntity.onTickStart();

        if(blockEntity.timeoutOffState > 0) {
            blockEntity.timeoutOffState--;

            if(blockEntity.timeoutOffState == 0) {
                blockEntity.onHasNotEnoughEnergyWithOffTimeout();
            }
        }

        if(!blockEntity.redstoneMode.isActive(state.get(Properties.POWERED)))
            return;

        if(blockEntity.hasWork()) {
            Optional<W> workData = blockEntity.getCurrentWorkData();
            if(workData.isEmpty()) {
                blockEntity.onTickEnd();

                return;
            }

            if(blockEntity.maxProgress == 0) {
                blockEntity.onWorkStarted(workData.get());

                blockEntity.maxProgress = blockEntity.getWorkDurationFor(workData.get());
            }

            long energyConsumptionPerTick = blockEntity.getEnergyConsumptionFor(workData.get());

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = energyConsumptionPerTick * blockEntity.maxProgress;

            if(energyConsumptionPerTick <= blockEntity.energyStorage.getAmount()) {
                blockEntity.hasEnoughEnergy = true;
                blockEntity.timeoutOffState = 0;
                blockEntity.onHasEnoughEnergy();

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress();
                    markDirty(level, blockPos, state);

                    blockEntity.onTickEnd();

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);
                    transaction.commit();
                }
                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    blockEntity.onWorkCompleted(workData.get());

                markDirty(level, blockPos, state);
            }else {
                blockEntity.hasEnoughEnergy = false;
                if(blockEntity.timeoutOffState == 0) {
                    blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
                }
                blockEntity.onHasNotEnoughEnergy();
                markDirty(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress();
            if(blockEntity.timeoutOffState == 0) {
                blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
            }
            blockEntity.onHasNotEnoughEnergy();
            markDirty(level, blockPos, state);
        }

        blockEntity.onTickEnd();
    }

    protected void onTickStart() {}

    protected void onTickEnd() {}

    protected void onHasEnoughEnergy() {}

    protected void onHasNotEnoughEnergy() {}

    protected void onHasNotEnoughEnergyWithOffTimeout() {}

    protected final int getWorkDurationFor(W workData) {
        return Math.max(1, (int)Math.ceil(baseWorkDuration * getWorkDataDependentWorkDuration(workData) /
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));
    }

    protected final long getEnergyConsumptionFor(W workData) {
        return Math.max(1, (long)Math.ceil(baseEnergyConsumptionPerTick *
                getWorkDataDependentEnergyConsumption(workData) *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));
    }

    protected double getWorkDataDependentWorkDuration(W workData) {
        return 1;
    }

    protected double getWorkDataDependentEnergyConsumption(W workData) {
        return 1;
    }

    protected abstract boolean hasWork();

    protected abstract Optional<W> getCurrentWorkData();

    protected abstract void onWorkStarted(W workData);

    protected abstract void onWorkCompleted(W workData);

    protected void resetProgress() {
        progress = 0;
        maxProgress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = false;
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress();

        super.updateUpgradeModules();
    }
}