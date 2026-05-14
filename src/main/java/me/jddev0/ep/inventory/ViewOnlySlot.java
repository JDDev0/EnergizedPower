package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;

public class ViewOnlySlot extends Slot {
    private static final Container EMPTY_INVENTORY = new SimpleContainer(0);
    private final SingleItemStackHandler itemHandler;
    protected final int index;

    public ViewOnlySlot(SingleItemStackHandler itemHandler, int index, int x, int y) {
        super(EMPTY_INVENTORY, index, x, y);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    @Override
    protected void onQuickCraft(ItemStack itemStack, int amount) {}

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return this.getItemHandler().variant.toStack();
    }

    @Override
    public void set(ItemStack stack) {
        try(Transaction transaction = Transaction.openOuter()) {
            itemHandler.setItemStack(stack, transaction);
            transaction.commit();
        }

        this.setChanged();
    }

    @Override
    public boolean mayPickup(Player player) {
        return true;
    }

    @Override
    public ItemStack safeInsert(ItemStack itemStack, int amount) {
        return itemStack;
    }

    @Override
    public Optional<ItemStack> tryRemove(int count, int limit, Player player) {
        return Optional.empty();
    }

    @Override
    public boolean isFake() {
        return true;
    }

    public SingleItemStackHandler getItemHandler() {
        return itemHandler;
    }
}
