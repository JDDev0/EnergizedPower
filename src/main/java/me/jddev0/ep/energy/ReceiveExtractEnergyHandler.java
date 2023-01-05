package me.jddev0.ep.energy;

import net.minecraftforge.energy.IEnergyStorage;

import java.util.function.BiPredicate;

public class ReceiveExtractEnergyHandler implements IEnergyStorage {
    private final IEnergyStorage energyStorage;

    private final BiPredicate<Integer, Boolean> canReceive;
    private final BiPredicate<Integer, Boolean> canExtract;

    public ReceiveExtractEnergyHandler(IEnergyStorage energyStorage, BiPredicate<Integer, Boolean> canReceive, BiPredicate<Integer, Boolean> canExtract) {
        this.energyStorage = energyStorage;
        this.canReceive = canReceive;
        this.canExtract = canExtract;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if(canReceive.test(maxReceive, simulate))
            return energyStorage.receiveEnergy(maxReceive, simulate);

        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if(canExtract.test(maxExtract, simulate))
            return energyStorage.extractEnergy(maxExtract, simulate);

        return 0;
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
        return canExtract.test(-1, false) && energyStorage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return canReceive.test(-1, false) && energyStorage.canReceive();
    }
}
