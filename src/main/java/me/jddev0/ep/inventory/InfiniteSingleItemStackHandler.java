package me.jddev0.ep.inventory;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;

public class InfiniteSingleItemStackHandler extends ItemStackHandler {
    protected ItemStack stack = ItemStack.EMPTY;

    @Override
    public void setSize(int size) {}

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);

        this.stack = stack.copyWithCount(1);

        onContentsChanged(slot);
    }

    @Override
    public int getSlots() {
        return 1;
    }

    public ItemStack getStack() {
        return stack.copy();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);

        if(stack.isEmpty())
            return ItemStack.EMPTY;

        return stack.copyWithCount(stack.getMaxStackSize());
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if(stack.isEmpty())
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        if(this.stack.isEmpty())
            return ItemStack.EMPTY;

        return this.stack.copyWithCount(Math.min(amount, this.stack.getMaxStackSize()));
    }

    @Override
    public final int getSlotLimit(int slot) {
        return Item.ABSOLUTE_MAX_STACK_SIZE;
    }

    protected final int getStackLimit(int slot, ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.storeNullable("Item", ItemStack.SINGLE_ITEM_CODEC, this.stack.isEmpty()?null:this.stack);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.stack = input.read("Item", ItemStack.SINGLE_ITEM_CODEC).orElse(ItemStack.EMPTY).copyWithCount(1);
    }

    @Override
    protected void validateSlotIndex(int slot) {
        if(slot != 0)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
    }
}
