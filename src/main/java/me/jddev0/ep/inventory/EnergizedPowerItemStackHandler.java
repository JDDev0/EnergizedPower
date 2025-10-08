package me.jddev0.ep.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class EnergizedPowerItemStackHandler extends ItemStacksResourceHandler implements IEnergizedPowerItemStackHandler {
    public EnergizedPowerItemStackHandler(int size) {
        super(size);
    }

    @Override
    protected final void onContentsChanged(int index, ItemStack previousContents) {
        onFinalCommit(index, previousContents);
    }

    protected void onFinalCommit(int index, ItemStack previousItemStack) {}

    @Override
    public void serialize(ValueOutput view) {
        NonNullList<ItemStack> items = NonNullList.withSize(size(), ItemStack.EMPTY);
        for(int i = 0;i < size();i++)
            items.set(i, getStackInSlot(i));

        ContainerHelper.saveAllItems(view, items);
    }

    @Override
    public void deserialize(ValueInput view) {
        NonNullList<ItemStack> items = NonNullList.withSize(size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(view, items);
        for(int i = 0;i < size();i++)
            stacks.set(i, items.get(i));
    }

    @Override
    public final void setStackInSlot(int slot, ItemStack itemStack) {
        ItemStack previousItemStack = stacks.set(slot, itemStack.copy());
        onFinalCommit(slot, previousItemStack);
    }

    @Override
    public final ItemStack extractItem(int slot, int amount) {
        if(getResource(slot).isEmpty())
            return ItemStack.EMPTY;

        ItemStack previousItemStack = stacks.get(slot);

        int currentAmount = previousItemStack.getCount();
        int extractedAmount = Math.min(currentAmount, amount);

        ItemStack extracted = previousItemStack.copyWithCount(extractedAmount);
        if(currentAmount == extractedAmount) {
            stacks.set(slot, ItemStack.EMPTY);
        }else {
            stacks.set(slot, extracted.copyWithCount(currentAmount - extractedAmount));
        }

        onFinalCommit(slot, previousItemStack);
        return extracted;
    }
}
