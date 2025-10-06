package me.jddev0.ep.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class CreativeItemSiloSlot extends Slot {
    private static final Container EMPTY_INVENTORY = new SimpleContainer(0);
    private final InfiniteSingleItemStackHandler itemHandler;
    protected final int index;

    public CreativeItemSiloSlot(InfiniteSingleItemStackHandler itemHandler, int index, int x, int y) {
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
        return this.getItemHandler().stack.toStack();
    }

    @Override
    public void set(ItemStack stack) {
        itemHandler.setStackInSlot(index, stack);
        this.setChanged();
    }

    @Override
    public boolean mayPickup(Player player) {
        return true;
    }

    @Override
    public ItemStack safeInsert(ItemStack itemStack, int amount) {
        if(!itemStack.isEmpty()) {
            ItemStack selfItem = getItem();
            if(selfItem.isEmpty() || !ItemStack.isSameItem(itemStack, selfItem) || !ItemStack.isSameItemSameComponents(itemStack, selfItem)) {
                set(itemStack.copyWithCount(1));
            }else {
                selfItem.grow(1);
                set(selfItem);
            }
        }

        return itemStack;
    }

    @Override
    public Optional<ItemStack> tryRemove(int count, int limit, Player player) {
        ItemStack selfItem = getItem();
        if(!selfItem.isEmpty()) {
            selfItem.shrink(1);
            set(selfItem);
        }

        return Optional.empty();
    }

    @Override
    public boolean isFake() {
        return true;
    }

    public InfiniteSingleItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return other instanceof CreativeItemSiloSlot that && that.itemHandler == this.itemHandler;
    }
}
