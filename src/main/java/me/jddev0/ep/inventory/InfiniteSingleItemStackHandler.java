package me.jddev0.ep.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

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
        return 64;
    }

    protected final int getStackLimit(int slot, ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        if(!this.stack.isEmpty()) {
            nbt.put("Item", this.stack.save(new CompoundTag()));
            nbt.getCompound("Item").remove("Count");
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("Item")) {
            CompoundTag itemNbt = nbt.getCompound("Item");
            itemNbt.putInt("Count", 1);
            this.stack = ItemStack.of(itemNbt).copyWithCount(1);
        }else {
            this.stack = ItemStack.EMPTY;
        }
    }

    @Override
    protected void validateSlotIndex(int slot) {
        if(slot != 0)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
    }
}
