package me.jddev0.ep.item.energy;

import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ItemCapabilityEnergy implements IEnergyStorage {
    private final ItemStack itemStack;
    private final IEnergizedPowerEnergyStorage energyStorage;

    public ItemCapabilityEnergy(ItemStack itemStack, IEnergizedPowerEnergyStorage energyStorage) {
        this.itemStack = itemStack;
        this.energyStorage = energyStorage;

        if(itemStack.has(EPDataComponentTypes.ENERGY))
            this.energyStorage.setEnergyWithoutUpdate(itemStack.getOrDefault(EPDataComponentTypes.ENERGY, 0));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int ret = energyStorage.receiveEnergy(maxReceive, simulate);

        if(!simulate) {
            itemStack.set(EPDataComponentTypes.ENERGY, energyStorage.getEnergy());
        }

        return ret;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int ret = energyStorage.extractEnergy(maxExtract, simulate);

        if(!simulate) {
            itemStack.set(EPDataComponentTypes.ENERGY, energyStorage.getEnergy());
        }

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

        itemStack.set(EPDataComponentTypes.ENERGY, energyStorage.getEnergy());
    }

    public void setCapacity(int capacity) {
        energyStorage.setCapacity(capacity);
    }

    public IEnergizedPowerEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}
