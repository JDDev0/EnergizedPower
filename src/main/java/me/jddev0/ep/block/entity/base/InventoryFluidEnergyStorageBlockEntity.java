package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.IEnergizedPowerFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class InventoryFluidEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends ItemStackHandler, F extends IEnergizedPowerFluidStorage>
        extends InventoryEnergyStorageBlockEntity<E, I>
        implements FluidStoragePacketUpdate {
    protected final F fluidStorage;

    protected final int baseTankCapacity;

    public InventoryFluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                  int baseEnergyCapacity, int baseEnergyTransferRate,
                                                  int slotCount,
                                                  int baseTankCapacity) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate, slotCount);

        this.baseTankCapacity = baseTankCapacity;

        fluidStorage = initFluidStorage();
    }

    protected abstract F initFluidStorage();

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        fluidStorage.serialize(nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        fluidStorage.deserialize(nbt, registries);
    }

    protected final void syncFluidToPlayer(Player player) {
        for(int i = 0;i < fluidStorage.size();i++)
            ModMessages.sendToPlayer(new FluidSyncS2CPacket(i, fluidStorage.getFluid(i),
                    fluidStorage.getTankCapacity(i), getBlockPos()), (ServerPlayer)player);
    }

    protected final void syncFluidToPlayers(int distance) {
        if(level != null && !level.isClientSide())
            for(int i = 0;i < fluidStorage.size();i++)
                ModMessages.sendToPlayersWithinXBlocks(
                        new FluidSyncS2CPacket(i, fluidStorage.getFluid(i), fluidStorage.getTankCapacity(i), getBlockPos()),
                        getBlockPos(), (ServerLevel)level, distance
                );
    }

    public FluidStack getFluid(int tank) {
        return fluidStorage.getFluid(tank);
    }

    public int getTankCapacity(int tank) {
        return fluidStorage.getTankCapacity(tank);
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(tank, fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, int capacity) {
        fluidStorage.setTankCapacity(tank, capacity);
    }
}
