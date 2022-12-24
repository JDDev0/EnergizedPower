package me.jddev0.ep.energy;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.IEnergyStorage;

public class ReceiveOnlyEnergyStorage implements IEnergyStorage {
    protected int energy;
    protected int capacity;
    protected int maxReceive;

    public ReceiveOnlyEnergyStorage() {}

    public ReceiveOnlyEnergyStorage(int energy, int capacity, int maxReceive) {
        this.energy = energy;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        onChange();
        this.energy = energy;
    }

    public void setEnergyWithoutUpdate(int energy) {
        this.energy = energy;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        onChange();
        this.capacity = capacity;
    }

    public void setCapacityWithoutUpdate(int capacity) {
        this.capacity = capacity;
    }

    public int getMaxReceive() {
        return maxReceive;
    }

    public void setMaxReceive(int maxReceive) {
        onChange();
        this.maxReceive = maxReceive;
    }

    public void setMaxReceiveWithoutUpdate(int maxReceive) {
        this.maxReceive = maxReceive;
    }

    protected void onChange() {}

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if(!canReceive())
            return 0;

        int received = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

        if(!simulate) {
            onChange();
            energy += received;
        }

        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    public Tag saveNBT() {
        return IntTag.valueOf(energy);
    }

    public void loadNBT(Tag tag) {
        if(!(tag instanceof IntTag))
            throw new IllegalArgumentException("Tag must be of type IntTag!");

        energy = ((IntTag)tag).getAsInt();
    }
}
