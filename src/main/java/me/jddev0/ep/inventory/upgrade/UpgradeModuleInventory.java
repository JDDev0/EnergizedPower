package me.jddev0.ep.inventory.upgrade;

import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class UpgradeModuleInventory extends SimpleContainer {
    private final UpgradeModuleModifier[] upgradeModifierSlots;

    public UpgradeModuleInventory(UpgradeModuleModifier... upgradeModifierSlots) {
        super(upgradeModifierSlots.length);

        this.upgradeModifierSlots = upgradeModifierSlots;
    }

    public UpgradeModuleModifier[] getUpgradeModifierSlots() {
        return upgradeModifierSlots;
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if(slot >= 0 && slot < getContainerSize()) {
            return stack.getItem() instanceof UpgradeModuleItem upgradeModuleItem &&
                    upgradeModuleItem.getMainUpgradeModuleModifier() == upgradeModifierSlots[slot];
        }

        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public int getUpgradeModuleTier(int slot) {
        ItemStack itemStack = getItem(slot);
        if(!(itemStack.getItem() instanceof UpgradeModuleItem upgradeModuleItem))
            return -1;

        return upgradeModuleItem.getUpgradeModuleTier();
    }

    public UpgradeModuleModifier[] getUpgradeModuleModifiers(int slot) {
        ItemStack itemStack = getItem(slot);
        if(!(itemStack.getItem() instanceof UpgradeModuleItem upgradeModuleItem))
            return new UpgradeModuleModifier[0];

        return upgradeModuleItem.getUpgradeModuleModifiers();
    }

    public double getUpgradeModuleModifierEffect(int slot, UpgradeModuleModifier modifier) {
        ItemStack itemStack = getItem(slot);
        if(!(itemStack.getItem() instanceof UpgradeModuleItem upgradeModuleItem))
            return -1;

        return upgradeModuleItem.getUpgradeModuleModifierValue(modifier);
    }

    public double getModifierEffectProduct(UpgradeModuleModifier modifier) {
        double prod = 1;

        for(int i = 0;i < getContainerSize();i++) {
            double value = getUpgradeModuleModifierEffect(i, modifier);
            if(value != -1)
                prod *= value;
        }

        return prod;
    }

    public double getModifierEffectSum(UpgradeModuleModifier modifier) {
        double sum = 0;

        for(int i = 0;i < getContainerSize();i++) {
            double value = getUpgradeModuleModifierEffect(i, modifier);
            if(value != -1)
                sum += value;
        }

        return sum;
    }

    public void saveData(ValueOutput view) {
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        for(int i = 0;i < getContainerSize();i++)
            items.set(i, getItem(i));

        ContainerHelper.saveAllItems(view, items);
    }

    public void readData(ValueInput view) {
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(view, items);
        for(int i = 0;i < getContainerSize();i++)
            setItem(i, items.get(i));
    }
}
