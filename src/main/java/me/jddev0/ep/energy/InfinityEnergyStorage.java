package me.jddev0.ep.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class InfinityEnergyStorage implements IEnergizedPowerEnergyStorage {
    public InfinityEnergyStorage() {}

    @Override
    public int getEnergy() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setEnergy(int energy) {}

    @Override
    public void setEnergyWithoutUpdate(int energy) {}

    @Override
    public int getCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setCapacity(int capacity) {}

    @Override
    public void setCapacityWithoutUpdate(int capacity) {}

    public int getMaxTransfer() {
        return Integer.MAX_VALUE;
    }

    public void setMaxTransfer(int maxTransfer) {}

    public void setMaxTransferWithoutUpdate(int maxTransfer) {}

    protected void onChange() {}

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return maxReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return maxExtract;
    }

    @Override
    public int getEnergyStored() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxEnergyStored() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public Tag saveNBT() {
        return new CompoundTag();
    }

    @Override
    public void loadNBT(Tag tag) {
        if(!(tag instanceof CompoundTag))
            throw new IllegalArgumentException("Tag must be of type IntTag!");
    }
}
