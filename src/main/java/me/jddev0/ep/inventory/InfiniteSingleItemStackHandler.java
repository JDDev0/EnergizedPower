package me.jddev0.ep.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class InfiniteSingleItemStackHandler extends SnapshotJournal<ItemResource>
        implements IEnergizedPowerItemStackHandler {
    protected ItemResource stack = ItemResource.EMPTY;

    @Override
    public ItemResource getResource(int slot) {
        return stack;
    }

    @Override
    public long getAmountAsLong(int index) {
        return stack.isEmpty()?0:Integer.MAX_VALUE;
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return true;
    }

    @Override
    protected ItemResource createSnapshot() {
        return stack;
    }

    @Override
    protected void revertToSnapshot(ItemResource snapshot) {
        stack = snapshot;
    }

    protected void onFinalCommit() {}

    @Override
    protected void onRootCommit(ItemResource originalState) {
        onFinalCommit();
    }

    @Override
    public void serialize(ValueOutput output) {
        output.storeNullable("Item", ItemStack.SINGLE_ITEM_CODEC, this.stack.isEmpty()?null:this.stack.toStack());
    }

    @Override
    public void deserialize(ValueInput input) {
        this.stack = ItemResource.of(input.read("Item", ItemStack.SINGLE_ITEM_CODEC).orElse(ItemStack.EMPTY));
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);

        this.stack = ItemResource.of(stack);

        onFinalCommit();
    }

    @Override
    public void set(int index, ItemResource resource, int amount) {
        validateSlotIndex(index);

        this.stack = resource;

        onFinalCommit();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);

        if(stack.isEmpty())
            return ItemStack.EMPTY;

        return stack.toStack(stack.getMaxStackSize());
    }

    @Override
    public int insert(int slot, ItemResource stack, int amount, TransactionContext transaction) {
        if(stack.isEmpty())
            return 0;

        validateSlotIndex(slot);

        return amount;
    }

    @Override
    public int extract(int slot, ItemResource stack, int amount, TransactionContext transaction) {
        if(amount == 0)
            return 0;

        validateSlotIndex(slot);

        if(this.stack.isEmpty())
            return 0;

        if(stack.equals(this.stack)) {
            return amount;
        }

        return 0;
    }

    @Override
    public ItemStack extractItem(int slot, int amount) {
        if(amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        if(this.stack.isEmpty())
            return ItemStack.EMPTY;

        return this.stack.toStack(Math.min(amount, this.stack.getMaxStackSize()));
    }

    private void validateSlotIndex(int slot) {
        if(slot != 0)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
    }
}
