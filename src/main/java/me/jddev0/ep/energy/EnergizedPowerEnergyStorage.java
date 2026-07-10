package me.jddev0.ep.energy;

public class EnergizedPowerEnergyStorage implements IEnergizedPowerEnergyStorage {
    protected int energy;
    protected int capacity;
    protected int maxInsert;
    protected int maxExtract;

    public EnergizedPowerEnergyStorage(int capacity, int maxInsert, int maxExtract) {
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    /**
     * Creates an energy storage with unlimited transfer rate
     */
    public EnergizedPowerEnergyStorage(int capacity) {
        this(capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    protected void onFinalCommit() {}

    @Override
    public boolean canReceive() {
        return maxInsert > 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if(!canReceive())
            return 0;

        int received = Math.max(0, Math.min(getMaxEnergyStored() - energy, Math.min(this.maxInsert, maxReceive)));

        if(!simulate) {
            energy += received;
            onFinalCommit();
        }

        return received;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if(!canExtract())
            return 0;

        int extracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

        if(!simulate) {
            energy -= extracted;
            onFinalCommit();
        }

        return extracted;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public final int getEnergyStored() {
        return getEnergy();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public final int getMaxEnergyStored() {
        return getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = energy;
        onFinalCommit();
    }

    @Override
    public void setEnergyWithoutUpdate(int energy) {
        this.energy = energy;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        onFinalCommit();
    }

    @Override
    public void setCapacityWithoutUpdate(int capacity) {
        this.capacity = capacity;
    }
}
