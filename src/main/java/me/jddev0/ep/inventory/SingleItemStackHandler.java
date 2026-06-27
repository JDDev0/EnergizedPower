package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class SingleItemStackHandler extends SingleItemStorage
        implements IEnergizedPowerItemStackHandler {
    public final int slotCount;

    public SingleItemStackHandler(int slotCount) {
        this.slotCount = slotCount;
    }

    public boolean isEmpty() {
        return isResourceBlank();
    }

    @Override
    protected long getCapacity(ItemVariant variant) {
        return (long)slotCount * variant.toStack().getMaxStackSize();
    }

    @Override
    public long getCapacity(int index, ItemVariant resource) {
        return getCapacity(variant);
    }

    @Override
    public boolean isValid(int index, ItemVariant stack) {
        return this.variant.isBlank() || ItemStack.isSameItemSameComponents(this.variant.toStack(), stack.toStack());
    }

    @Override
    public void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        nbt.putLong("Count", this.amount);
        if(!isEmpty()) {
            nbt.put("Item", this.variant.toStack().save(lookupProvider, new CompoundTag()));
            nbt.getCompound("Item").remove("count");
        }
    }

    public final void writeNbt(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        serialize(nbt, lookupProvider);
    }

    @Override
    public void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        this.amount = nbt.getLong("Count");
        if(this.amount == 0) {
            this.variant = ItemVariant.blank();
        }else {
            CompoundTag itemNbt = nbt.getCompound("Item");
            itemNbt.putInt("count", 1);

            this.variant = ItemVariant.of(ItemStack.parse(lookupProvider, itemNbt).orElse(ItemStack.EMPTY));
        }
    }

    @Override
    public final void readNbt(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        deserialize(nbt, lookupProvider);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.amount = stack.getCount();
        this.variant = ItemVariant.of(stack);

        onFinalCommit();
    }

    @Override
    public void set(int index, ItemVariant resource, int amount) {
        this.amount = amount;
        this.variant = resource;

        onFinalCommit();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if(amount == 0)
            return ItemStack.EMPTY;

        int maxStackSize = variant.toStack().getMaxStackSize();
        if(amount <= slot * (long)maxStackSize)
            return ItemStack.EMPTY;

        int itemCount = (int)Math.min(amount - slot * (long)maxStackSize, maxStackSize);
        return variant.toStack(itemCount);
    }

    @Override
    public ItemStack extractItem(int slot, int amount) {
        if(amount == 0)
            return ItemStack.EMPTY;

        if(this.variant.isBlank())
            return ItemStack.EMPTY;

        ItemStack existing = this.variant.toStack();
        long existingCount = this.amount;

        long toExtract = Math.min(amount, existing.getMaxStackSize());

        if(this.amount <= toExtract) {
            this.amount = 0;
            this.variant = ItemVariant.blank();
            onFinalCommit();

            return existing.copyWithCount((int)existingCount);
        }else {
            this.amount -= toExtract;
            onFinalCommit();

            return existing.copyWithCount((int)toExtract);
        }
    }
}
