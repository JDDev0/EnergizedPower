package me.jddev0.ep.inventory;

import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.BooleanSupplier;

public class UpgradeModuleSlot extends Slot {
    private final BooleanSupplier active;

    public UpgradeModuleSlot(Inventory container, int slot, int x, int y, BooleanSupplier active) {
        super(container, slot, x, y);

        this.active = active;
    }

    @Override
    public boolean isEnabled() {
        return active.getAsBoolean();
    }

    @Override
    public boolean canInsert(ItemStack itemStack) {
        return itemStack.getItem() instanceof UpgradeModuleItem upgradeModuleItem &&
                (!(inventory instanceof UpgradeModuleInventory upgradeModuleInventory) ||
                        upgradeModuleItem.getMainUpgradeModuleModifier() == upgradeModuleInventory.
                                getUpgradeModifierSlots()[getIndex()]);
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }
}
