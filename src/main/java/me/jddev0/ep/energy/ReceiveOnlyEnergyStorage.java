package me.jddev0.ep.energy;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

public class ReceiveOnlyEnergyStorage implements IEnergizedPowerEnergyStorage {
    protected int energy;
    protected int capacity;
    protected int maxReceive;

    public ReceiveOnlyEnergyStorage() {}

    public ReceiveOnlyEnergyStorage(int energy, int capacity, int maxReceive) {
        this.energy = energy;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = energy;
        onChange();
    }

    @Override
    public void setEnergyWithoutUpdate(int energy) {
        this.energy = energy;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        onChange();
    }

    @Override
    public void setCapacityWithoutUpdate(int capacity) {
        this.capacity = capacity;
    }

    public int getMaxReceive() {
        return maxReceive;
    }

    public void setMaxReceive(int maxReceive) {
        this.maxReceive = maxReceive;
        onChange();
    }

    public void setMaxReceiveWithoutUpdate(int maxReceive) {
        this.maxReceive = maxReceive;
    }

    protected void onChange() {}

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if(!canReceive())
            return 0;

        int received = Math.max(0, Math.min(getMaxEnergyStored() - energy, Math.min(getMaxReceive(), maxReceive)));

        if(!simulate) {
            energy += received;
            onChange();
        }

        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public final int getEnergyStored() {
        return getEnergy();
    }

    @Override
    public final int getMaxEnergyStored() {
        return getCapacity();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public Tag saveNBT() {
        return IntTag.valueOf(energy);
    }

    @Override
    public void loadNBT(Tag tag) {
        if(!(tag instanceof IntTag)) {
            energy = 0;

            return;
        }

        energy = ((IntTag)tag).getAsInt();
    }
}
