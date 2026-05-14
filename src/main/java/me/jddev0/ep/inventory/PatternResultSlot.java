package me.jddev0.ep.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public class PatternResultSlot extends PatternSlot {
    public PatternResultSlot(Container container, int slot, int x, int y, BooleanSupplier isEnabled) {
        super(container, slot, x, y, isEnabled);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public ItemStack safeInsert(ItemStack itemStack, int amount) {
        return itemStack;
    }

    @Override
    public Optional<ItemStack> tryRemove(int count, int limit, Player player) {
        return Optional.empty();
    }
}
