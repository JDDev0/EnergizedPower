package me.jddev0.ep.block.entity.handler;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class InputOutputItemHandler implements IItemHandlerModifiable {
    private final IItemHandlerModifiable handler;
    private final BiPredicate<Integer, ItemStack> canInput;
    private final Predicate<Integer> canOutput;

    public InputOutputItemHandler(IItemHandlerModifiable handler, BiPredicate<Integer, ItemStack> canInput, Predicate<Integer> canOutput) {
        this.handler = handler;
        this.canInput = canInput;
        this.canOutput = canOutput;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        handler.setStackInSlot(slot, stack);
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return canInput.test(slot, stack)?handler.insertItem(slot, stack, simulate):stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return canOutput.test(slot)?handler.extractItem(slot, amount, simulate):ItemStack.EMPTY;
   }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return canInput.test(slot, stack) && handler.isItemValid(slot, stack);
    }
}
