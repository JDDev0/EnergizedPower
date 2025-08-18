package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public class InfiniteSingleItemStackHandler extends SingleItemStorage {
    public boolean isEmpty() {
        return isResourceBlank();
    }

    public void setItemStack(ItemStack itemStack, TransactionContext transaction) {
        ItemVariant itemVariant = ItemVariant.of(itemStack);

        updateSnapshots(transaction);

        variant = itemVariant;
        amount = itemVariant.isBlank()?0:1;
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
    protected long getCapacity(ItemVariant variant) {
        return Long.MAX_VALUE;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookupProvider) {
        if(!isEmpty()) {
            nbt.put("Item", this.variant.toStack().encode(lookupProvider, new NbtCompound()));
            nbt.getCompound("Item").remove("count");
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookupProvider) {
        if(nbt.contains("Item")) {
            NbtCompound itemNbt = nbt.getCompound("Item");
            itemNbt.putInt("count", 1);

            this.variant = ItemVariant.of(ItemStack.fromNbt(lookupProvider, itemNbt).orElse(ItemStack.EMPTY));
            this.amount = 1;
        }else {
            this.variant = ItemVariant.blank();
            this.amount = 0;
        }
    }
}
