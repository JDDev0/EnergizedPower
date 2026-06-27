package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EnergizedPowerItemStackHandler extends CombinedSlottedStorage<ItemVariant, SingleItemStorage>
        implements IEnergizedPowerItemStackHandler {
    public EnergizedPowerItemStackHandler(int size) {
        super(new ArrayList<>(size));

        for(int i = 0;i < size;i++) {
            final int index = i;
            this.parts.add(new SingleItemStorage() {
                private ItemStack previousContents = null;

                @Override
                protected long getCapacity(ItemVariant resource) {
                    return EnergizedPowerItemStackHandler.this.getCapacity(index, resource);
                }

                @Override
                protected boolean canInsert(ItemVariant resource) {
                    return EnergizedPowerItemStackHandler.this.isValid(index, resource);
                }

                @Override
                public void updateSnapshots(TransactionContext transaction) {
                    if(previousContents == null) {
                        previousContents = variant.toStack((int)amount);
                    }

                    super.updateSnapshots(transaction);
                }

                @Override
                protected void onFinalCommit() {
                    //previousContents is never null here, because updateSnapshots would have been called at least once
                    ItemStack previousContents = this.previousContents;
                    //Clear previousContents immediately: Currently Fabric API does not allow opening transactions within onFinalCommit,
                    // but this might be possible in the future
                    this.previousContents = null;

                    EnergizedPowerItemStackHandler.this.onFinalCommit(index, previousContents);
                }
            });
        }
    }

    @Override
    public long getCapacity(int index, ItemVariant resource) {
        return resource.isBlank()?Item.ABSOLUTE_MAX_STACK_SIZE:Math.min(resource.toStack().getMaxStackSize(), Item.ABSOLUTE_MAX_STACK_SIZE);
    }

    @Override
    public boolean isValid(int index, ItemVariant resource) {
        return true;
    }

    protected void onFinalCommit(int index, @NotNull ItemStack previousItemStack) {}

    @Override
    public void serialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        NonNullList<ItemStack> items = NonNullList.withSize(parts.size(), ItemStack.EMPTY);
        for(int i = 0;i < parts.size();i++)
            items.set(i, getStackInSlot(i));

        ContainerHelper.saveAllItems(nbt, items, lookupProvider);
    }

    @Override
    public void deserialize(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        NonNullList<ItemStack> items = NonNullList.withSize(parts.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, items, lookupProvider);
        for(int i = 0;i < parts.size();i++) {
            parts.get(i).variant = ItemVariant.of(items.get(i));
            parts.get(i).amount = items.get(i).getCount();
        }
    }

    @Override
    public final void setStackInSlot(int slot, ItemStack itemStack) {
        ItemStack previousItemStack = getStackInSlot(slot);
        parts.get(slot).variant = ItemVariant.of(itemStack.copy());
        parts.get(slot).amount = itemStack.getCount();
        onFinalCommit(slot, previousItemStack);
    }

    @Override
    public void set(int index, ItemVariant resource, int amount) {
        StoragePreconditions.notNegative(amount);
        if(resource.isBlank() && amount > 0) {
            throw new IllegalArgumentException("ItemVariant is empty but the amount is positive: " + amount);
        }

        setStackInSlot(index, resource.toStack(amount));
    }

    @Override
    public final ItemStack extractItem(int slot, int amount) {
        ItemStack previousItemStack = getStackInSlot(slot);
        if(previousItemStack.isEmpty())
            return ItemStack.EMPTY;

        int currentAmount = previousItemStack.getCount();
        int extractedAmount = Math.min(currentAmount, amount);

        ItemStack extracted = previousItemStack.copyWithCount(extractedAmount);
        if(currentAmount == extractedAmount) {
            parts.get(slot).variant = ItemVariant.blank();
            parts.get(slot).amount = 0;
        }else {
            parts.get(slot).variant = ItemVariant.of(extracted.copy());
            parts.get(slot).amount = currentAmount - extractedAmount;
        }

        onFinalCommit(slot, previousItemStack);
        return extracted;
    }

    @Override
    public int getSlotCount() {
        return parts.size();
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return parts.get(slot);
    }
}
