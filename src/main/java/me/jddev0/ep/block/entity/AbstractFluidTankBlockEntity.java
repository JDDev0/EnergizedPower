package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuFluidStorageBlockEntity;
import me.jddev0.ep.fluid.IEnergizedPowerFluidStorage;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFluidTankBlockEntity<F extends IEnergizedPowerFluidStorage>
        extends MenuFluidStorageBlockEntity<F> {
    public AbstractFluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        String machineName,
                                        long baseTankCapacity) {
        super(type, blockPos, blockState, machineName, baseTankCapacity);
    }

    public int getRedstoneOutput() {
        return FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
    }

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag output = new CompoundTag();
        fluidStorage.serialize(output, registries);
        return output;
    }
}