package me.jddev0.ep.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public class PatternSlot extends Slot {
    private final BooleanSupplier isEnabled;

    public PatternSlot(Inventory container, int slot, int x, int y, BooleanSupplier isEnabled) {
        super(container, slot, x, y);

        this.isEnabled = isEnabled;
    }

    @Override
    protected void onCrafted(ItemStack itemStack, int amount) {}

    @Override
    public boolean canInsert(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canTakeItems(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack insertStack(ItemStack itemStack, int amount) {
        if(!isEnabled.getAsBoolean())
            return itemStack;

        if(!itemStack.isEmpty()) {
            ItemStack selfItem = getStack();
            if(selfItem.isEmpty() || !ItemStack.areItemsEqual(itemStack, selfItem) || !ItemStack.areNbtEqual(itemStack, selfItem)) {
                ItemStack itemStackCopy = itemStack.copy();
                itemStackCopy.setCount(1);
                setStack(itemStackCopy);
            }else {
                selfItem.increment(1);
                setStack(selfItem);
            }
        }

        return itemStack;
    }

    @Override
    public Optional<ItemStack> tryTakeStackRange(int count, int limit, PlayerEntity player) {
        if(!isEnabled.getAsBoolean())
            return Optional.empty();

        ItemStack selfItem = getStack();
        if(!selfItem.isEmpty()) {
            selfItem.decrement(1);
            setStack(selfItem);
        }

        return Optional.empty();
    }
}
