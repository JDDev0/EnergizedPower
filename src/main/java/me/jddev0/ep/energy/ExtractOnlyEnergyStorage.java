package me.jddev0.ep.energy;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.IEnergyStorage;

public class ExtractOnlyEnergyStorage implements IEnergyStorage {
    protected int energy;
    protected int capacity;
    protected int maxExtract;

    public ExtractOnlyEnergyStorage() {}

    public ExtractOnlyEnergyStorage(int energy, int capacity, int maxExtract) {
        this.energy = energy;
        this.capacity = capacity;
        this.maxExtract = maxExtract;
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

    public int getMaxExtract() {
        return maxExtract;
    }

    public void setMaxExtract(int maxExtract) {
        onChange();
        this.maxExtract = maxExtract;
    }

    public void setMaxExtractWithoutUpdate(int maxExtract) {
        this.maxExtract = maxExtract;
    }

    protected void onChange() {}

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if(!canExtract())
            return 0;

        int extracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

        if(!simulate) {
            onChange();
            energy -= extracted;
        }

        return extracted;
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
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
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
