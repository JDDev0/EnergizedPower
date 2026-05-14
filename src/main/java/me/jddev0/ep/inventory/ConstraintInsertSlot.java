package me.jddev0.ep.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ConstraintInsertSlot extends Slot {
    public ConstraintInsertSlot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return container.canPlaceItem(getContainerSlot(), stack);
    }
}
