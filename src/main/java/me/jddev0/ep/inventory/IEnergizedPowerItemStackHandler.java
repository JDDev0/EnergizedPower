package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface IEnergizedPowerItemStackHandler extends SlottedStorage<ItemVariant> {
    long getCapacity(int index, ItemVariant resource);
    boolean isValid(int index, ItemVariant resource);

    void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider);
    void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider);

    default ItemStack getStackInSlot(int slot) {
        SingleSlotStorage<ItemVariant> slotStorage = getSlot(slot);
        return slotStorage.getResource().toStack((int)slotStorage.getAmount());
    }
    void setStackInSlot(int slot, ItemStack itemStack);

    void set(int index, ItemVariant resource, int amount);

    ItemStack extractItem(int slot, int amount);

    default int size() {
        return getSlotCount();
    }
}
