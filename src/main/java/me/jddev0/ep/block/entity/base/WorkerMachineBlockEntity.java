package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class WorkerMachineBlockEntity<W>
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage, ItemStackHandler> {
    protected final int baseEnergyConsumptionPerTick;
    protected final int baseWorkDuration;

    protected int progress;
    protected int maxProgress;
    protected int energyConsumptionLeft = -1;
    protected boolean hasEnoughEnergy;

    protected int timeoutOffState;

    public WorkerMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                    String machineName,
                                    int slotCount, int baseWorkDuration,
                                    int baseEnergyCapacity, int baseEnergyTransferRate, int baseEnergyConsumptionPerTick,
                                    UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate, slotCount, upgradeModifierSlots);

        this.baseEnergyConsumptionPerTick = baseEnergyConsumptionPerTick;
        this.baseWorkDuration = baseWorkDuration;
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            public int getMaxReceive() {
                return Math.max(1, (int)Math.ceil(maxReceive * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }

            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.put("recipe.progress", IntTag.valueOf(progress));
        nbt.put("recipe.max_progress", IntTag.valueOf(maxProgress));
        nbt.put("recipe.energy_consumption_left", IntTag.valueOf(energyConsumptionLeft));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");
    }

    public static <W> void tick(Level level, BlockPos blockPos, BlockState state, WorkerMachineBlockEntity<W> blockEntity) {
        if(level.isClientSide)
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

            int energyConsumptionPerTick = blockEntity.getEnergyConsumptionFor(workData.get());

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = energyConsumptionPerTick * blockEntity.maxProgress;

            if(energyConsumptionPerTick <= blockEntity.energyStorage.getEnergy()) {
                blockEntity.hasEnoughEnergy = true;
                blockEntity.timeoutOffState = 0;
                blockEntity.onHasEnoughEnergy();

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress();
                    setChanged(level, blockPos, state);

                    blockEntity.onTickEnd();

                    return;
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);
                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    blockEntity.onWorkCompleted(workData.get());

                setChanged(level, blockPos, state);
            }else {
                blockEntity.hasEnoughEnergy = false;
                if(blockEntity.timeoutOffState == 0) {
                    blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
                }
                blockEntity.onHasNotEnoughEnergy();
                setChanged(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress();
            if(blockEntity.timeoutOffState == 0) {
                blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
            }
            blockEntity.onHasNotEnoughEnergy();
            setChanged(level, blockPos, state);
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

    protected final int getEnergyConsumptionFor(W workData) {
        return Math.max(1, (int)Math.ceil(baseEnergyConsumptionPerTick *
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