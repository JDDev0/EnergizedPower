package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.ContainerUser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class InputOutputItemHandler implements SidedInventory, Function<Direction, @Nullable Storage<ItemVariant>> {
    private final InventoryStorage[] cachedInventoryStorages = new InventoryStorage[Direction.values().length];

    private final SidedInventory handler;
    private final BiPredicate<Integer, ItemStack> canInput;
    private final Predicate<Integer> canOutput;

    public InputOutputItemHandler(Inventory handler, BiPredicate<Integer, ItemStack> canInput, Predicate<Integer> canOutput) {
        this(new SidedInventoryWrapper(handler), canInput, canOutput);
    }

    public InputOutputItemHandler(SidedInventory handler, BiPredicate<Integer, ItemStack> canInput, Predicate<Integer> canOutput) {
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
            cachedInventoryStorages[index] = InventoryStorage.of(this, side);

        return cachedInventoryStorages[index];
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return handler.getAvailableSlots(side);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return canInput.test(slot, stack) && handler.isValid(slot, stack) && handler.canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return canOutput.test(slot) && handler.canExtract(slot, stack, dir);
    }

    @Override
    public int size() {
        return handler.size();
    }

    @Override
    public boolean isEmpty() {
        return handler.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return handler.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return handler.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return handler.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        handler.setStack(slot, stack);
    }

    @Override
    public void markDirty() {
        handler.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return handler.canPlayerUse(player);
    }

    @Override
    public void clear() {
        handler.clear();
    }

    @Override
    public int getMaxCountPerStack() {
        return handler.getMaxCountPerStack();
    }

    @Override
    public void onOpen(ContainerUser player) {
        handler.onOpen(player);
    }

    @Override
    public void onClose(ContainerUser player) {
        handler.onClose(player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return handler.isValid(slot, stack);
    }

    @Override
    public int count(Item item) {
        return handler.count(item);
    }

    @Override
    public boolean containsAny(Set<Item> items) {
        return handler.containsAny(items);
    }

    @Override
    public boolean containsAny(Predicate<ItemStack> predicate) {
        return handler.containsAny(predicate);
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return handler.canTransferTo(hopperInventory, slot, stack);
    }
}
