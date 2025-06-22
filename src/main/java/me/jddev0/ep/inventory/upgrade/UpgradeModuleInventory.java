package me.jddev0.ep.inventory.upgrade;

import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.NotNull;

public class UpgradeModuleInventory extends SimpleInventory {
    private final UpgradeModuleModifier[] upgradeModifierSlots;

    public UpgradeModuleInventory(UpgradeModuleModifier... upgradeModifierSlots) {
        super(upgradeModifierSlots.length);

        this.upgradeModifierSlots = upgradeModifierSlots;
    }

    public UpgradeModuleModifier[] getUpgradeModifierSlots() {
        return upgradeModifierSlots;
    }

    @Override
    public boolean isValid(int slot, @NotNull ItemStack stack) {
        if(slot >= 0 && slot < size()) {
            return stack.getItem() instanceof UpgradeModuleItem upgradeModuleItem &&
                    upgradeModuleItem.getMainUpgradeModuleModifier() == upgradeModifierSlots[slot];
        }

        return false;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    public int getUpgradeModuleTier(int slot) {
        ItemStack itemStack = getStack(slot);
        if(!(itemStack.getItem() instanceof UpgradeModuleItem upgradeModuleItem))
            return -1;

        return upgradeModuleItem.getUpgradeModuleTier();
    }

    public UpgradeModuleModifier[] getUpgradeModuleModifiers(int slot) {
        ItemStack itemStack = getStack(slot);
        if(!(itemStack.getItem() instanceof UpgradeModuleItem upgradeModuleItem))
            return new UpgradeModuleModifier[0];

        return upgradeModuleItem.getUpgradeModuleModifiers();
    }

    public double getUpgradeModuleModifierEffect(int slot, UpgradeModuleModifier modifier) {
        ItemStack itemStack = getStack(slot);
        if(!(itemStack.getItem() instanceof UpgradeModuleItem upgradeModuleItem))
            return -1;

        return upgradeModuleItem.getUpgradeModuleModifierValue(modifier);
    }

    public double getModifierEffectProduct(UpgradeModuleModifier modifier) {
        double prod = 1;

        for(int i = 0;i < size();i++) {
            double value = getUpgradeModuleModifierEffect(i, modifier);
            if(value != -1)
                prod *= value;
        }

        return prod;
    }

    public double getModifierEffectSum(UpgradeModuleModifier modifier) {
        double sum = 0;

        for(int i = 0;i < size();i++) {
            double value = getUpgradeModuleModifierEffect(i, modifier);
            if(value != -1)
                sum += value;
        }

        return sum;
    }

    public void saveData(WriteView view) {
        Inventories.writeData(view, heldStacks);
    }

    public void readData(ReadView view) {
        Inventories.readData(view, heldStacks);
    }
}
