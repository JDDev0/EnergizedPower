package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class InputOutputItemHandler implements WorldlyContainer, Function<Direction, @Nullable Storage<ItemVariant>> {
    private final ContainerStorage[] cachedInventoryStorages = new ContainerStorage[Direction.values().length];

    private final WorldlyContainer handler;
    private final BiPredicate<Integer, ItemStack> canInput;
    private final Predicate<Integer> canOutput;

    public InputOutputItemHandler(Container handler, BiPredicate<Integer, ItemStack> canInput, Predicate<Integer> canOutput) {
        this(new SidedInventoryWrapper(handler), canInput, canOutput);
    }

    public InputOutputItemHandler(WorldlyContainer handler, BiPredicate<Integer, ItemStack> canInput, Predicate<Integer> canOutput) {
        this.handler = handler;
        this.canInput = canInput;
        this.canOutput = canOutput;
    }

    @Override
    @Nullable
    public Storage<ItemVariant> apply(Direction side) {
        if(side == null)
            return null;

        int index = side.ordinal();

        if(cachedInventoryStorages[index] == null)
            cachedInventoryStorages[index] = ContainerStorage.of(this, side);

        return cachedInventoryStorages[index];
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return handler.getSlotsForFace(side);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return canInput.test(slot, stack) && handler.canPlaceItem(slot, stack) && handler.canPlaceItemThroughFace(slot, stack, dir);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return canOutput.test(slot) && handler.canTakeItemThroughFace(slot, stack, dir);
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
}
