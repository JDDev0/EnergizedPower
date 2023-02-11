package me.jddev0.ep.block.entity.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;

public interface SidedInventoryBlockEntityWrapper extends SidedInventory {
    SidedInventory getHandler();

    @Override
    default int[] getAvailableSlots(Direction side) {
        return getHandler().getAvailableSlots(side);
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return getHandler().isValid(slot, stack) && getHandler().canInsert(slot, stack, dir);
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return getHandler().canExtract(slot, stack, dir);
    }

    @Override
    default int size() {
        return getHandler().size();
    }

    @Override
    default boolean isEmpty() {
        return getHandler().isEmpty();
    }

    @Override
    default ItemStack getStack(int slot) {
        return getHandler().getStack(slot);
    }

    @Override
    default ItemStack removeStack(int slot, int amount) {
        return getHandler().removeStack(slot, amount);
    }

    @Override
    default ItemStack removeStack(int slot) {
        return getHandler().removeStack(slot);
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getHandler().setStack(slot, stack);
    }

    @Override
    default void markDirty() {
        getHandler().markDirty();
    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return getHandler().canPlayerUse(player);
    }

    @Override
    default void clear() {
        getHandler().clear();
    }

    @Override
    default int getMaxCountPerStack() {
        return getHandler().getMaxCountPerStack();
    }

    @Override
    default void onOpen(PlayerEntity player) {
        getHandler().onOpen(player);
    }

    @Override
    default void onClose(PlayerEntity player) {
        getHandler().onClose(player);
    }

    @Override
    default boolean isValid(int slot, ItemStack stack) {
        return getHandler().isValid(slot, stack);
    }

    @Override
    default int count(Item item) {
        return getHandler().count(item);
    }

    @Override
    default boolean containsAny(Set<Item> items) {
        return getHandler().containsAny(items);
    }
}
