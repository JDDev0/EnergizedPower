package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMethods;
import me.jddev0.ep.block.entity.base.MenuFluidStorageBlockEntity;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFluidTankBlockEntity<F extends IFluidHandler>
        extends MenuFluidStorageBlockEntity<F> {
    private final LazyOptional<IFluidHandler> lazyFluidStorage;

    public AbstractFluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        String machineName,
                                        FluidStorageMethods<F> fluidStorageMethods, int baseTankCapacity) {
        super(type, blockPos, blockState, machineName, fluidStorageMethods, baseTankCapacity);

        lazyFluidStorage = LazyOptional.of(() -> fluidStorage);
    }

    public static <F extends IFluidHandler> void tick(Level level, BlockPos blockPos, BlockState state,
                                                      AbstractFluidTankBlockEntity<F> blockEntity) {
        if(level.isClientSide)
            return;

        //Sync item stacks to client every 5 seconds
        if(level.getGameTime() % 100 == 0) //TODO improve
            blockEntity.syncFluidToPlayers(64);
    }

    public int getRedstoneOutput() {
        return FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.FLUID_HANDLER) {
            return lazyFluidStorage.cast();
        }

        return super.getCapability(cap, side);
    }
}