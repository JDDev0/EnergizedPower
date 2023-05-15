package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.MinecartChargerBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.entity.MinecartBatteryBox;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MinecartChargerBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    public static final int MAX_TRANSFER = 512;

    private final ReceiveOnlyEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    public MinecartChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.MINECART_CHARGER_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, 16384, MAX_TRANSFER) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityEnergy.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyEnergyStorage.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("energy", energyStorage.saveNBT());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        energyStorage.loadNBT(nbt.get("energy"));
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, MinecartChargerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        BlockPos blockPosFacing = blockEntity.getBlockPos().relative(blockEntity.getBlockState().getValue(MinecartChargerBlock.FACING));
        List<MinecartBatteryBox> minecarts = level.getEntities(EntityTypeTest.forClass(MinecartBatteryBox.class),
                new AABB(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntitySelector.ENTITY_STILL_ALIVE);
        if(minecarts.isEmpty())
            return;

        MinecartBatteryBox minecart = minecarts.get(0);
        int transferred = Math.min(Math.min(blockEntity.energyStorage.getEnergy(), MAX_TRANSFER),
                Math.min(MinecartBatteryBox.MAX_TRANSFER, MinecartBatteryBox.CAPACITY - minecart.getEnergy()));
        minecart.setEnergy(minecart.getEnergy() + transferred);

        blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - transferred);
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }
}