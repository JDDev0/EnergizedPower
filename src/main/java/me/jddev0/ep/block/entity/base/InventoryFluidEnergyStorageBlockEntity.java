package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.IEnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.IEnergizedPowerItemStackHandler;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class InventoryFluidEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends IEnergizedPowerItemStackHandler, F extends IEnergizedPowerFluidStorage>
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
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        fluidStorage.serialize(view);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        fluidStorage.deserialize(view);
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
