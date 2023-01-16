package me.jddev0.ep.item.energy;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemCapabilityEnergy implements ICapabilityProvider, IEnergyStorage {
    private final ItemStack itemStack;
    private final IEnergizedPowerEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    public ItemCapabilityEnergy(ItemStack itemStack, @Nullable CompoundTag nbt, IEnergizedPowerEnergyStorage energyStorage) {
        this.itemStack = itemStack;
        this.energyStorage = energyStorage;

        if(nbt != null && nbt.contains("energy"))
            this.energyStorage.loadNBT(nbt.get("energy"));

        lazyEnergyStorage = LazyOptional.of(() -> this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityEnergy.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int ret = energyStorage.receiveEnergy(maxReceive, simulate);

        if(!simulate)
            itemStack.getOrCreateTag().put("energy", energyStorage.saveNBT());

        return ret;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int ret = energyStorage.extractEnergy(maxExtract, simulate);

        if(!simulate)
            itemStack.getOrCreateTag().put("energy", energyStorage.saveNBT());

        return ret;
    }

    @Override
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return energyStorage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return energyStorage.canReceive();
    }

    public void setEnergy(int energy) {
        energyStorage.setEnergy(energy);

        itemStack.getOrCreateTag().put("energy", energyStorage.saveNBT());
    }

    public void setCapacity(int capacity) {
        energyStorage.setCapacity(capacity);
    }
}
