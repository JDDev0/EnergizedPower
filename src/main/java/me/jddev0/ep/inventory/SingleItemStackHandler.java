package me.jddev0.ep.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class SingleItemStackHandler extends EnergizedPowerItemStackHandler {
    protected final int slotCount;
    protected int count;
    protected ItemStack stack = ItemStack.EMPTY;

    public SingleItemStackHandler(int slotCount) {
        this.slotCount = slotCount;
    }

    @Override
    public void setSize(int size) {}

    @Override
    public final void internalSetStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);

        this.count = stack.getCount();
        this.stack = stack.copyWithCount(1);

        onFinalCommit();
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
    public @NotNull ItemStack internalInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if(stack.isEmpty())
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        if(!isValid(slot, stack))
            return stack;

        int limit = Math.min(stack.getMaxStackSize(), slotCount * stack.getMaxStackSize() - count);
        if(limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if(!simulate) {
            if(this.stack.isEmpty()) {
                this.stack = stack.copyWithCount(1);
            }

            count += reachedLimit?limit:stack.getCount();
            onFinalCommit();
        }

        return reachedLimit?stack.copyWithCount(stack.getCount() - limit):ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack internalExtractItem(int slot, int amount, boolean simulate) {
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
                onFinalCommit();
            }

            return existing.copyWithCount(existingCount);
        }else {
            if(!simulate) {
                this.count -= toExtract;
                onFinalCommit();
            }

            return existing.copyWithCount(toExtract);
        }
    }

    @Override
    public final int getCapacity(int slot) {
        return Item.ABSOLUTE_MAX_STACK_SIZE;
    }

    @Override
    public boolean isValid(int slot, @NotNull ItemStack stack) {
        return this.stack.isEmpty() || ItemStack.isSameItemSameComponents(this.stack, stack);
    }

    /**
     * Method not used in InfiniteSingleItemStackHandler, use onFinalCommit without parameters instead
     */
    @Override
    @ApiStatus.Internal
    protected final void onFinalCommit(int index, ItemStack previousItemStack) {
        throw new IllegalStateException("This method should not be called in the EP implementation of the ItemStackHandler, use onFinalCommit instead");
    }

    protected void onFinalCommit() {}

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = new CompoundTag();

        nbt.putInt("Count", this.count);
        if(!this.stack.isEmpty()) {
            nbt.put("Item", this.stack.save(lookupProvider, new CompoundTag()));
            nbt.getCompound("Item").remove("count");
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        this.count = nbt.getInt("Count");
        if(this.count == 0) {
            this.stack = ItemStack.EMPTY;
        }else {
            CompoundTag itemNbt = nbt.getCompound("Item");
            itemNbt.putInt("count", 1);
            this.stack = ItemStack.parse(lookupProvider, itemNbt).orElse(ItemStack.EMPTY).copyWithCount(1);
        }
    }

    @Override
    protected void validateSlotIndex(int slot) {
        if(slot < 0 || slot >= slotCount)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + slotCount + ")");
    }
}
