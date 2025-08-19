package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

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
    public void writeData(WriteView view) {
        view.putNullable("Item", ItemStack.UNCOUNTED_CODEC, isEmpty()?null:this.variant.toStack());
    }

    @Override
    public void readData(ReadView input) {
        this.variant = ItemVariant.of(input.read("Item", ItemStack.UNCOUNTED_CODEC).orElse(ItemStack.EMPTY));
        this.amount = isEmpty()?0:1;
    }
}
