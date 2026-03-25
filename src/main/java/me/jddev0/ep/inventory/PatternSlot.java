package me.jddev0.ep.inventory;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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
            if(selfItem.isEmpty() || !ItemStack.isSameItem(itemStack, selfItem) || !ItemStack.isSameItemSameComponents(itemStack, selfItem)) {
                setByPlayer(itemStack.copyWithCount(1));
            }else {
                selfItem.grow(1);
                setByPlayer(selfItem);
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
            setByPlayer(selfItem);
        }

        return Optional.empty();
    }

    @Override
    public boolean isFake() {
        return true;
    }
}
