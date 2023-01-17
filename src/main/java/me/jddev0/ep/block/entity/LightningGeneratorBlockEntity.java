package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.LightningGeneratorBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ExtractOnlyEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LightningGeneratorBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    private final ExtractOnlyEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    public LightningGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.LIGHTING_GENERATOR_ENTITY.get(), blockPos, blockState);

        energyStorage = new ExtractOnlyEnergyStorage(0, LightningGeneratorBlock.ENERGY_PER_LIGHTNING_STRIKE, 65536) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
            }
        };
    }

    public void onLightningStrike() {
        energyStorage.setEnergy(energyStorage.getCapacity());
    }

    @Override
    public @Nonnull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
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
    public CompoundTag save(CompoundTag nbt) {
        nbt.put("energy", energyStorage.saveNBT());

        return super.save(nbt);
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);

        energyStorage.loadNBT(nbt.get("energy"));
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