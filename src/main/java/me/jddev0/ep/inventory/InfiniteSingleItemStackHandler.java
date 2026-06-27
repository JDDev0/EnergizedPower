package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class InfiniteSingleItemStackHandler extends SingleItemStorage
        implements IEnergizedPowerItemStackHandler {
    public boolean isEmpty() {
        return isResourceBlank();
    }

    @Override
    protected long getCapacity(ItemVariant variant) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getCapacity(int index, ItemVariant resource) {
        return getCapacity(variant);
    }

    @Override
    public boolean isValid(int index, ItemVariant resource) {
        return true;
    }

    @Override
    public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        return maxAmount;
    }

    @Override
    public long extract(ItemVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        return !extractedVariant.isBlank() && extractedVariant.isOf(variant.getItem()) &&
                extractedVariant.componentsMatch(variant.getComponents())?maxAmount:0;
    }

    @Override
    public void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        if(!isEmpty()) {
            nbt.put("Item", this.variant.toStack().save(lookupProvider, new CompoundTag()));
            nbt.getCompound("Item").remove("count");
        }
    }

    @Override
    public final void writeNbt(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        serialize(nbt, lookupProvider);
    }

    @Override
    public void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        if(nbt.contains("Item")) {
            CompoundTag itemNbt = nbt.getCompound("Item");
            itemNbt.putInt("count", 1);

            this.variant = ItemVariant.of(ItemStack.parse(lookupProvider, itemNbt).orElse(ItemStack.EMPTY));
            this.amount = 1;
        }else {
            this.variant = ItemVariant.blank();
            this.amount = 0;
        }
    }

    @Override
    public final void readNbt(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        deserialize(nbt, lookupProvider);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.variant = ItemVariant.of(stack);

        onFinalCommit();
    }

    @Override
    public void set(int index, ItemVariant resource, int amount) {
        this.variant = resource;

        onFinalCommit();
    }

    @Override
    public ItemStack extractItem(int slot, int amount) {
        if(amount == 0)
            return ItemStack.EMPTY;

        if(this.variant.isBlank())
            return ItemStack.EMPTY;

        return this.variant.toStack(Math.min(amount, this.variant.toStack().getMaxStackSize()));
    }
}
