package me.jddev0.ep.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public class PatternSlot extends Slot {
    private final BooleanSupplier isEnabled;

    public PatternSlot(Container container, int slot, int x, int y, BooleanSupplier isEnabled) {
        super(container, slot, x, y);

        this.isEnabled = isEnabled;
    }

    @Override
    protected void onQuickCraft(ItemStack itemStack, int amount) {}

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return true;
    }

    @Override
    public ItemStack safeInsert(ItemStack itemStack, int amount) {
        if(!isEnabled.getAsBoolean())
            return itemStack;

        if(!itemStack.isEmpty()) {
            ItemStack selfItem = getItem();
            if(selfItem.isEmpty() || !ItemStack.isSameItem(itemStack, selfItem) || !ItemStack.isSameItemSameTags(itemStack, selfItem)) {
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
        if(!isEnabled.getAsBoolean())
            return Optional.empty();

        ItemStack selfItem = getItem();
        if(!selfItem.isEmpty()) {
            selfItem.shrink(1);
            set(selfItem);
        }

        return Optional.empty();
    }
}
