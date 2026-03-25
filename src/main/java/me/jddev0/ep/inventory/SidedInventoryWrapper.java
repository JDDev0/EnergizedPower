package me.jddev0.ep.inventory;

import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SidedInventoryWrapper implements WorldlyContainer {
    private final Container handler;

    public SidedInventoryWrapper(Container handler) {
        this.handler = handler;
    }

    public Container getHandler() {
        return handler;
    }

    @Override
    public int getContainerSize() {
        return handler.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return handler.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return handler.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return handler.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return handler.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        handler.setItem(slot, stack);
    }

    @Override
    public void setChanged() {
        handler.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return handler.stillValid(player);
    }

    @Override
    public void clearContent() {
        handler.clearContent();
    }

    @Override
    public int getMaxStackSize() {
        return handler.getMaxStackSize();
    }

    @Override
    public void startOpen(ContainerUser player) {
        handler.startOpen(player);
    }

    @Override
    public void stopOpen(ContainerUser player) {
        handler.stopOpen(player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return handler.canPlaceItem(slot, stack);
    }

    @Override
    public int countItem(Item item) {
        return handler.countItem(item);
    }

    @Override
    public boolean hasAnyOf(Set<Item> items) {
        return handler.hasAnyOf(items);
    }

    @Override
    public boolean hasAnyMatching(Predicate<ItemStack> predicate) {
        return handler.hasAnyMatching(predicate);
    }

    @Override
    public boolean canTakeItem(Container hopperInventory, int slot, ItemStack stack) {
        return handler.canTakeItem(hopperInventory, slot, stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return IntStream.range(0, handler.getContainerSize()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return true;
    }
}
