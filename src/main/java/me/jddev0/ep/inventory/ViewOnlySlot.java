package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;

public class ViewOnlySlot extends Slot {
    private static final Inventory EMPTY_INVENTORY = new SimpleInventory(0);
    private final SingleItemStackHandler itemHandler;
    protected final int index;

    public ViewOnlySlot(SingleItemStackHandler itemHandler, int index, int x, int y) {
        super(EMPTY_INVENTORY, index, x, y);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    @Override
    protected void onCrafted(ItemStack itemStack, int amount) {}

    @Override
    public boolean canInsert(ItemStack itemStack) {
        return false;
    }

    @Override
    public ItemStack getStack() {
        return this.getItemHandler().variant.toStack();
    }

    @Override
    public void setStackNoCallbacks(ItemStack stack) {
        try(Transaction transaction = Transaction.openOuter()) {
            itemHandler.setItemStack(stack, transaction);
            transaction.commit();
        }

        this.markDirty();
    }

    @Override
    public boolean canTakeItems(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack insertStack(ItemStack itemStack, int amount) {
        return itemStack;
    }

    @Override
    public Optional<ItemStack> tryTakeStackRange(int count, int limit, PlayerEntity player) {
        return Optional.empty();
    }

    @Override
    public boolean disablesDynamicDisplay() {
        return true;
    }

    public SingleItemStackHandler getItemHandler() {
        return itemHandler;
    }
}
