package me.jddev0.ep.block.entity.handler;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class InputOutputItemHandler implements IItemHandlerModifiable {
    private final IItemHandlerModifiable handler;
    private final Predicate<Integer> isInput;
    private final Predicate<Integer> isOutput;

    public InputOutputItemHandler(IItemHandlerModifiable handler, Predicate<Integer> isInput, Predicate<Integer> isOutput) {
        this.handler = handler;
        this.isInput = isInput;
        this.isOutput = isOutput;
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
        return isInput.test(slot)?handler.insertItem(slot, stack, simulate):stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return isOutput.test(slot)?handler.extractItem(slot, amount, simulate):ItemStack.EMPTY;
   }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return isInput.test(slot) && handler.isItemValid(slot, stack);
    }
}
