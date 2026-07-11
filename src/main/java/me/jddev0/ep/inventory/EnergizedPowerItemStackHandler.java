package me.jddev0.ep.inventory;


import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class EnergizedPowerItemStackHandler extends ItemStackHandler {
    public EnergizedPowerItemStackHandler() {
        super();
    }

    public EnergizedPowerItemStackHandler(int size) {
        super(size);
    }

    @Override
    protected final int getStackLimit(int slot, @NotNull ItemStack stack) {
        return super.getStackLimit(slot, stack);
    }

    /**
     * Method redirected to "getCapacity()" to match 26.1.x
     */
    @Override
    public final int getSlotLimit(int slot) {
        return getCapacity(slot);
    }

    public int getCapacity(int slot) {
        return super.getSlotLimit(slot);
    }

    /**
     * Method redirected to "isValid()" to match 26.1.x
     */
    @Override
    public final boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return isValid(slot, stack);
    }

    public boolean isValid(int slot, @NotNull ItemStack stack) {
        return super.isItemValid(slot, stack);
    }

    /**
     * Method redirected to "internalSetStackInSlot()" to allow override in (Infinite)SingleItemStackHandler
     */
    @Override
    public final void setStackInSlot(int slot, @NotNull ItemStack stack) {
        internalSetStackInSlot(slot, stack);
    }

    @ApiStatus.NonExtendable
    @ApiStatus.Internal
    protected void internalSetStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        ItemStack previousItemStack = this.stacks.set(slot, stack);
        onFinalCommit(slot, previousItemStack);
    }

    /**
     * Method redirected to "internalInsertItem()" to allow override in (Infinite)SingleItemStackHandler
     */
    @Override
    public final @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return internalInsertItem(slot, stack, simulate);
    }

    /**
     * Re-implemented to use onFinalCommit with previous stack method
     */
    @ApiStatus.NonExtendable
    @ApiStatus.Internal
    public @NotNull ItemStack internalInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if(stack.isEmpty())
            return ItemStack.EMPTY;

        if(!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if(!existing.isEmpty()) {
            if(!ItemStack.isSameItemSameComponents(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if(limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if(!simulate) {
            ItemStack previousItemStack;
            if(existing.isEmpty()) {
                previousItemStack = this.stacks.set(slot, reachedLimit?stack.copyWithCount(limit):stack);
            }else {
                previousItemStack = existing.copy();
                existing.grow(reachedLimit?limit:stack.getCount());
            }
            onFinalCommit(slot, previousItemStack);
        }

        return reachedLimit?stack.copyWithCount(stack.getCount() - limit):ItemStack.EMPTY;
    }

    /**
     * Method redirected to "internalExtractItem()" to allow override in (Infinite)SingleItemStackHandler
     */
    @Override
    public final @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return internalExtractItem(slot, amount, simulate);
    }

    /**
     * Re-implemented to use onFinalCommit with previous stack method
     */
    @ApiStatus.NonExtendable
    @ApiStatus.Internal
    public @NotNull ItemStack internalExtractItem(int slot, int amount, boolean simulate) {
        if(amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if(existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if(existing.getCount() <= toExtract) {
            if(!simulate) {
                ItemStack previousItemStack = this.stacks.set(slot, ItemStack.EMPTY);
                onFinalCommit(slot, previousItemStack);
                return existing;
            }else {
                return existing.copy();
            }
        }else {
            if(!simulate) {
                ItemStack previousItemStack = this.stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                onFinalCommit(slot, previousItemStack);
            }

            return existing.copyWithCount(toExtract);
        }
    }

    /**
     * Method not used in EP Item Stack Handler, use onFinalCommit instead
     */
    @Override
    @ApiStatus.Internal
    protected final void onContentsChanged(int slot) {
        throw new IllegalStateException("This method should not be called in the EP implementation of the ItemStackHandler, use onFinalCommit instead");
    }

    protected void onFinalCommit(int index, ItemStack previousItemStack) {}
}
