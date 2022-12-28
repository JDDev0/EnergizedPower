package me.jddev0.ep.energy;

import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.IEnergyStorage;

public interface IEnergizedPowerEnergyStorage extends IEnergyStorage {
    int getEnergy();
    void setEnergy(int energy);
    void setEnergyWithoutUpdate(int energy);

    int getCapacity();
    void setCapacity(int capacity);
    void setCapacityWithoutUpdate(int capacity);

    Tag saveNBT();

    void loadNBT(Tag tag);
}
