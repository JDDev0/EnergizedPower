package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.PoweredLampBlock;
import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class PoweredLampBlockEntity extends EnergyStorageBlockEntity<ReceiveOnlyEnergyStorage> {
    public static final int MAX_RECEIVE = ModConfigs.COMMON_POWERED_LAMP_TRANSFER_RATE.getValue();

    public PoweredLampBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.POWERED_LAMP_ENTITY.get(), blockPos, blockState,

                MAX_RECEIVE,
                MAX_RECEIVE
        );
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, PoweredLampBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        boolean isEmptyFlag = blockEntity.energyStorage.getEnergy() == 0;
        int levelValue = Math.min(Mth.floor((float)blockEntity.energyStorage.getEnergy() /
                blockEntity.energyStorage.getCapacity() * 14.f) + (isEmptyFlag?0:1), 15);

        if(state.getValue(PoweredLampBlock.LEVEL) != levelValue)
            level.setBlock(blockPos, state.setValue(PoweredLampBlock.LEVEL, levelValue), 3);

        blockEntity.energyStorage.setEnergy(0);
    }
}