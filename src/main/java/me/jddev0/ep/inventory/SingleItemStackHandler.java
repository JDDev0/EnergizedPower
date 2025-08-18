package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public class SingleItemStackHandler extends SingleItemStorage {
    public final int slotCount;

    public SingleItemStackHandler(int slotCount) {
        this.slotCount = slotCount;
    }

    public boolean isEmpty() {
        return isResourceBlank();
    }

    public void setItemStack(ItemStack itemStack, TransactionContext transaction) {
        ItemVariant itemVariant = ItemVariant.of(itemStack);

        updateSnapshots(transaction);

        variant = itemVariant;
        amount = itemStack.getCount();
    }

    @Override
    protected long getCapacity(ItemVariant variant) {
        return (long)slotCount * variant.toStack().getMaxCount();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookupProvider) {
        nbt.putLong("Count", this.amount);
        if(!isEmpty()) {
            nbt.put("Item", this.variant.toStack().encode(lookupProvider, new NbtCompound()));
            nbt.getCompound("Item").remove("count");
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookupProvider) {
        this.amount = nbt.getLong("Count");
        if(this.amount == 0) {
            this.variant = ItemVariant.blank();
        }else {
            NbtCompound itemNbt = nbt.getCompound("Item");
            itemNbt.putInt("count", 1);

            this.variant = ItemVariant.of(ItemStack.fromNbt(lookupProvider, itemNbt).orElse(ItemStack.EMPTY));
        }
    }
}
