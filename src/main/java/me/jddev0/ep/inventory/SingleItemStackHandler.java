package me.jddev0.ep.inventory;

import me.jddev0.ep.codec.CodecFix;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
        return (long)slotCount * variant.toStack().getMaxStackSize();
    }

    @Override
    public void writeValue(ValueOutput view) {
        view.putLong("Count", this.amount);
        view.storeNullable("Item", CodecFix.SINGLE_ITEM_ITEM_STACK_CODEC, isEmpty()?null:this.variant.toStack());
    }

    @Override
    public void readValue(ValueInput input) {
        this.amount = input.getLongOr("Count", 0);
        if(this.amount == 0) {
            this.variant = ItemVariant.blank();
        }else {
            this.variant = ItemVariant.of(input.read("Item", CodecFix.SINGLE_ITEM_ITEM_STACK_CODEC).orElse(ItemStack.EMPTY));
        }
    }
}
