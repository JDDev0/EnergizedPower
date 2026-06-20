package me.jddev0.ep.inventory;

import me.jddev0.ep.codec.CodecFix;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    public void serialize(ValueOutput output) {
        output.putLong("Count", this.amount);
        output.storeNullable("Item", CodecFix.SINGLE_ITEM_ITEM_STACK_CODEC, isEmpty()?null:this.variant.toStack());
    }

    @Override
    public final void writeValue(ValueOutput output) {
        serialize(output);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.amount = input.getLongOr("Count", 0);
        if(this.amount == 0) {
            this.variant = ItemVariant.blank();
        }else {
            this.variant = ItemVariant.of(input.read("Item", CodecFix.SINGLE_ITEM_ITEM_STACK_CODEC).orElse(ItemStack.EMPTY));
        }
    }

    @Override
    public final void readValue(ValueInput input) {
        deserialize(input);
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
