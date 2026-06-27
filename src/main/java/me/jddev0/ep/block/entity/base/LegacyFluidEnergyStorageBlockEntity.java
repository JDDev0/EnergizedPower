package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class LegacyFluidEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, F extends Storage<FluidVariant>>
        extends EnergyStorageBlockEntity<E>
        implements FluidStoragePacketUpdate {
    protected final FluidStorageMethods<F> fluidStorageMethods;

    public final F fluidStorage;

    protected final long baseTankCapacity;

    public LegacyFluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                               long baseEnergyCapacity, long baseEnergyTransferRate,
                                               FluidStorageMethods<F> fluidStorageMethods, long baseTankCapacity) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate);

        this.fluidStorageMethods = fluidStorageMethods;
        this.baseTankCapacity = baseTankCapacity;

        fluidStorage = initFluidStorage();
    }

    protected abstract F initFluidStorage();

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        fluidStorageMethods.saveFluidStorage(fluidStorage, nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        fluidStorageMethods.loadFluidStorage(fluidStorage, nbt, registries);
    }

    protected final void syncFluidToPlayer(Player player) {
        fluidStorageMethods.syncFluidToPlayer(fluidStorage, player, worldPosition);
    }

    protected final void syncFluidToPlayers(int distance) {
        if(level != null && !level.isClientSide())
            fluidStorageMethods.syncFluidToPlayers(fluidStorage, level, worldPosition, distance);
    }

    public FluidStack getFluid(int tank) {
        return fluidStorageMethods.getFluid(fluidStorage, tank);
    }

    public long getTankCapacity(int tank) {
        return fluidStorageMethods.getTankCapacity(fluidStorage, tank);
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorageMethods.setFluid(fluidStorage, tank, fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        fluidStorageMethods.setTankCapacity(fluidStorage, tank, capacity);
    }
}
