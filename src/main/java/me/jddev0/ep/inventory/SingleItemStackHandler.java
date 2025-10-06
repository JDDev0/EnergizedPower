package me.jddev0.ep.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SingleItemStackHandler extends SnapshotJournal<SingleItemStackHandler.ItemResourceWithAmount>
        implements IEnergizedPowerItemStackHandler {
    protected final int slotCount;
    protected int count;
    protected ItemResource stack = ItemResource.EMPTY;

    public SingleItemStackHandler(int slotCount) {
        this.slotCount = slotCount;
    }

    public int getCount() {
        return count;
    }

    public ItemResource getResource() {
        return stack;
    }

    @Override
    public ItemResource getResource(int slot) {
        validateSlotIndex(slot);

        if(count == 0)
            return ItemResource.EMPTY;

        int maxStackSize = stack.getMaxStackSize();
        if(count <= slot * maxStackSize)
            return ItemResource.EMPTY;

        return stack;
    }

    @Override
    public long getAmountAsLong(int slot) {
        validateSlotIndex(slot);

        if(count == 0)
            return 0;

        int maxStackSize = stack.getMaxStackSize();
        if(count <= slot * maxStackSize)
            return 0;

        return Math.min(count - slot * maxStackSize, maxStackSize);
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return (long)slotCount * resource.toStack().getMaxStackSize();
    }

    @Override
    public boolean isValid(int index, ItemResource stack) {
        return this.stack.isEmpty() || this.stack.equals(stack);
    }

    @Override
    protected ItemResourceWithAmount createSnapshot() {
        return new ItemResourceWithAmount(stack, count);
    }

    @Override
    protected void revertToSnapshot(ItemResourceWithAmount snapshot) {
        stack = snapshot.item;
        count = snapshot.amount;
    }

    protected void onFinalCommit() {}

    @Override
    protected void onRootCommit(ItemResourceWithAmount originalState) {
        onFinalCommit();
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("Count", this.count);
        output.storeNullable("Item", ItemStack.SINGLE_ITEM_CODEC, this.stack.isEmpty()?null:this.stack.toStack());
    }

    @Override
    public void deserialize(ValueInput input) {
        this.count = input.getIntOr("Count", 0);
        if(this.count == 0) {
            this.stack = ItemResource.EMPTY;
        }else {
            this.stack = ItemResource.of(input.read("Item", ItemStack.SINGLE_ITEM_CODEC).orElse(ItemStack.EMPTY));
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);

        this.count = stack.getCount();
        this.stack = ItemResource.of(stack);

        onFinalCommit();
    }

    @Override
    public void set(int index, ItemResource resource, int amount) {
        validateSlotIndex(index);

        this.count = amount;
        this.stack = resource;

        onFinalCommit();
    }

    @Override
    public int size() {
        return slotCount;
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
        return stack.toStack(itemCount);
    }

    @Override
    public int insert(int slot, ItemResource stack, int amount, TransactionContext transaction) {
        if(stack.isEmpty())
            return 0;

        validateSlotIndex(slot);

        if(!isValid(slot, stack))
            return 0;

        int limit = Math.min(stack.getMaxStackSize(), slotCount * stack.getMaxStackSize() - count);
        if(limit <= 0)
            return 0;

        boolean reachedLimit = amount > limit;

        updateSnapshots(transaction);
        if(this.stack.isEmpty()) {
            this.stack = stack;
        }

        count += reachedLimit?limit:amount;

        return reachedLimit?limit:amount;
    }

    @Override
    public int extract(int slot, ItemResource stack, int amount, TransactionContext transaction) {
        if(amount == 0)
            return 0;

        validateSlotIndex(slot);

        if(this.stack.isEmpty())
            return 0;

        ItemStack existing = this.stack.toStack();
        int existingCount = this.count;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        updateSnapshots(transaction);
        if(this.count <= toExtract) {
            this.count = 0;
            this.stack = ItemResource.EMPTY;

            return existingCount;
        }else {
            this.count -= toExtract;

            return toExtract;
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount) {
        if(amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        if(this.stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack existing = this.stack.toStack();
        int existingCount = this.count;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if(this.count <= toExtract) {
            this.count = 0;
            this.stack = ItemResource.EMPTY;
            onFinalCommit();

            return existing.copyWithCount(existingCount);
        }else {
            this.count -= toExtract;
            onFinalCommit();

            return existing.copyWithCount(toExtract);
        }
    }

    private void validateSlotIndex(int slot) {
        if(slot < 0 || slot >= slotCount)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + slotCount + ")");
    }

    public record ItemResourceWithAmount(ItemResource item, int amount) {}
}
