package me.jddev0.ep.inventory;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;

public class SingleItemStackHandler extends ItemStackHandler {
    protected final int slotCount;
    protected int count;
    protected ItemStack stack = ItemStack.EMPTY;

    public SingleItemStackHandler(int slotCount) {
        this.slotCount = slotCount;
    }

    @Override
    public void setSize(int size) {}

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);

        this.count = stack.getCount();
        this.stack = stack.copyWithCount(1);

        onContentsChanged(slot);
    }

    @Override
    public int getSlots() {
        return slotCount;
    }

    public int getCount() {
        return count;
    }

    public ItemStack getStack() {
        return stack.copy();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);

        if(count == 0)
            return ItemStack.EMPTY;

        int maxStackSize = stack.getMaxStackSize();
        if(count <= slot * maxStackSize)
            return ItemStack.EMPTY;

        int itemCount = Math.min(count - slot * maxStackSize, maxStackSize);
        return stack.copyWithCount(itemCount);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if(stack.isEmpty())
            return ItemStack.EMPTY;

        if(!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        int limit = Math.min(stack.getMaxStackSize(), slotCount * stack.getMaxStackSize() - count);
        if(limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if(!simulate) {
            if(this.stack.isEmpty()) {
                this.stack = stack.copyWithCount(1);
            }

            count += reachedLimit?limit:stack.getCount();
            onContentsChanged(slot);
        }

        return reachedLimit?stack.copyWithCount(stack.getCount() - limit):ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        if(this.stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack existing = this.stack.copy();
        int existingCount = this.count;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if(this.count <= toExtract) {
            if(!simulate) {
                this.count = 0;
                this.stack = ItemStack.EMPTY;
                onContentsChanged(slot);
            }

            return existing.copyWithCount(existingCount);
        }else {
            if(!simulate) {
                this.count -= toExtract;
                onContentsChanged(slot);
            }

            return existing.copyWithCount(toExtract);
        }
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
        return this.stack.isEmpty() || ItemStack.isSameItemSameComponents(this.stack, stack);
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("Count", this.count);
        output.storeNullable("Item", ItemStack.SINGLE_ITEM_CODEC, this.stack.isEmpty()?null:this.stack);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.count = input.getIntOr("Count", 0);
        if(this.count == 0) {
            this.stack = ItemStack.EMPTY;
        }else {
            this.stack = input.read("Item", ItemStack.SINGLE_ITEM_CODEC).orElse(ItemStack.EMPTY).copyWithCount(1);
        }
    }

    @Override
    protected void validateSlotIndex(int slot) {
        if(slot < 0 || slot >= slotCount)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + slotCount + ")");
    }
}
