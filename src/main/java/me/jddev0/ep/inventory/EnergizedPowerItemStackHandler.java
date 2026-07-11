package me.jddev0.ep.inventory;


import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class EnergizedPowerItemStackHandler extends ItemStackHandler {
    public EnergizedPowerItemStackHandler() {
        super();
    }

    public EnergizedPowerItemStackHandler(int size) {
        super(size);
    }

    @Override
    protected final int getStackLimit(int slot, ItemStack stack) {
        return super.getStackLimit(slot, stack);
    }

    /**
     * Method "renamed" to "getCapacity()" to match 26.1.x
     */
    @Override
    public int getSlotLimit(int slot) {
        return getCapacity(slot);
    }

    public int getCapacity(int slot) {
        return super.getSlotLimit(slot);
    }

    /**
     * Method "renamed" to "isValid()" to match 26.1.x
     */
    @Override
    public final boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return isValid(slot, stack);
    }

    public boolean isValid(int slot, @NotNull ItemStack stack) {
        return super.isItemValid(slot, stack);
    }
}
