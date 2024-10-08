package me.jddev0.ep.item.energy;

import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ItemCapabilityEnergy implements IEnergyStorage {
    private final ItemStack itemStack;
    private final IEnergizedPowerEnergyStorage energyStorage;

    public ItemCapabilityEnergy(ItemStack itemStack, IEnergizedPowerEnergyStorage energyStorage) {
        this.itemStack = itemStack;
        this.energyStorage = energyStorage;

        if(itemStack.has(EPDataComponentTypes.ENERGY))
            this.energyStorage.loadNBT(IntTag.valueOf(itemStack.getOrDefault(EPDataComponentTypes.ENERGY, 0)));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int ret = energyStorage.receiveEnergy(maxReceive, simulate);

        if(!simulate) {
            Tag nbt = energyStorage.saveNBT();
            if(nbt instanceof IntTag nbtInt)
                itemStack.set(EPDataComponentTypes.ENERGY, nbtInt.getAsInt());
        }

        return ret;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int ret = energyStorage.extractEnergy(maxExtract, simulate);

        if(!simulate) {
            Tag nbt = energyStorage.saveNBT();
            if(nbt instanceof IntTag nbtInt)
                itemStack.set(EPDataComponentTypes.ENERGY, nbtInt.getAsInt());
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

        Tag nbt = energyStorage.saveNBT();
        if(nbt instanceof IntTag nbtInt)
            itemStack.set(EPDataComponentTypes.ENERGY, nbtInt.getAsInt());
    }

    public void setCapacity(int capacity) {
        energyStorage.setCapacity(capacity);
    }

    public IEnergizedPowerEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}
