package me.jddev0.ep.inventory;

import me.jddev0.ep.codec.CodecFix;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
                extractedVariant.componentsMatch(variant.getComponentsPatch())?maxAmount:0;
    }

    @Override
    public void serialize(ValueOutput view) {
        view.storeNullable("Item", CodecFix.SINGLE_ITEM_ITEM_STACK_CODEC, isEmpty()?null:this.variant.toStack());
    }

    @Override
    public final void writeValue(ValueOutput output) {
        serialize(output);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.variant = ItemVariant.of(input.read("Item", CodecFix.SINGLE_ITEM_ITEM_STACK_CODEC).orElse(ItemStack.EMPTY));
        this.amount = isEmpty()?0:1;
    }

    @Override
    public final void readValue(ValueInput input) {
        deserialize(input);
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
