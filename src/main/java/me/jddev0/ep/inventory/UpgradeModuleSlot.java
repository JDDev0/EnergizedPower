package me.jddev0.ep.inventory;

import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;

public class UpgradeModuleSlot extends Slot {
    private final BooleanSupplier active;

    public UpgradeModuleSlot(Container container, int slot, int x, int y, BooleanSupplier active) {
        super(container, slot, x, y);

        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active.getAsBoolean();
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return itemStack.getItem() instanceof UpgradeModuleItem upgradeModuleItem &&
                (!(container instanceof UpgradeModuleInventory upgradeModuleInventory) ||
                        upgradeModuleItem.getMainUpgradeModuleModifier() == upgradeModuleInventory.
                                getUpgradeModifierSlots()[getSlotIndex()]);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
