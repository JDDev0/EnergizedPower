package me.jddev0.ep.inventory;


import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class EnergizedPowerItemStackHandler extends ItemStackHandler {
    public EnergizedPowerItemStackHandler() {
        super();
    }

    public EnergizedPowerItemStackHandler(int size) {
        super(size);
    }

    /**
     * Method "renamed" to "isValid()" to match 26.1.x
     */
    @Override
    public final boolean isItemValid(int slot, ItemStack stack) {
        return isValid(slot, stack);
    }

    public boolean isValid(int slot, ItemStack stack) {
        return super.isItemValid(slot, stack);
    }
}
