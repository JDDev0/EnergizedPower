package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMethods;
import me.jddev0.ep.block.entity.base.MenuFluidStorageBlockEntity;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractFluidTankBlockEntity<F extends Storage<FluidVariant>>
        extends MenuFluidStorageBlockEntity<F> {
    public AbstractFluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        String machineName,
                                        FluidStorageMethods<F> fluidStorageMethods, long baseTankCapacity) {
        super(type, blockPos, blockState, machineName, fluidStorageMethods, baseTankCapacity);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AbstractFluidTankBlockEntity blockEntity) {
        if(level.isClient())
            return;

        //Sync item stacks to client every 5 seconds
        if(level.getTime() % 100 == 0) { //TODO improve
            blockEntity.syncFluidToPlayers(64);
        }
    }

    public int getRedstoneOutput() {
        return FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
    }
}