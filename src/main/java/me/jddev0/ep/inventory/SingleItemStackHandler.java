package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

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
    public void writeData(WriteView view) {
        view.putLong("Count", this.amount);
        view.putNullable("Item", ItemStack.UNCOUNTED_CODEC, isEmpty()?null:this.variant.toStack());
    }

    @Override
    public void readData(ReadView input) {
        this.amount = input.getLong("Count", 0);
        if(this.amount == 0) {
            this.variant = ItemVariant.blank();
        }else {
            this.variant = ItemVariant.of(input.read("Item", ItemStack.UNCOUNTED_CODEC).orElse(ItemStack.EMPTY));
        }
    }
}
