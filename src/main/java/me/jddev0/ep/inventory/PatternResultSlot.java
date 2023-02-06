package me.jddev0.ep.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public class PatternResultSlot extends PatternSlot {
    public PatternResultSlot(Inventory container, int slot, int x, int y, BooleanSupplier isEnabled) {
        super(container, slot, x, y, isEnabled);
    }

    @Override
    public boolean canInsert(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canTakeItems(PlayerEntity player) {
        return false;
    }

    @Override
    public ItemStack insertStack(ItemStack itemStack, int amount) {
        return itemStack;
    }

    @Override
    public Optional<ItemStack> tryTakeStackRange(int count, int limit, PlayerEntity player) {
        return Optional.empty();
    }
}
