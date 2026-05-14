package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMethods;
import me.jddev0.ep.block.entity.base.MenuFluidStorageBlockEntity;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractFluidTankBlockEntity<F extends Storage<FluidVariant>>
        extends MenuFluidStorageBlockEntity<F> {
    public AbstractFluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        String machineName,
                                        FluidStorageMethods<F> fluidStorageMethods, long baseTankCapacity) {
        super(type, blockPos, blockState, machineName, fluidStorageMethods, baseTankCapacity);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AbstractFluidTankBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        //Sync item stacks to client every 5 seconds
        if(level.getGameTime() % 100 == 0) { //TODO improve
            blockEntity.syncFluidToPlayers(64);
        }
    }

    public int getRedstoneOutput() {
        return FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
    }
}