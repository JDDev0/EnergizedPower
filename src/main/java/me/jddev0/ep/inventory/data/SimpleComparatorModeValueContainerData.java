package me.jddev0.ep.inventory.data;

import me.jddev0.ep.machine.configuration.ComparatorMode;
import net.minecraft.screen.PropertyDelegate;

public class SimpleComparatorModeValueContainerData implements PropertyDelegate {
    private ComparatorMode value = ComparatorMode.ITEM;

    public ComparatorMode getValue() {
        return value;
    }

    @Override
    public int get(int index) {
        if(index == 0)
            return value.ordinal();

        return 0;
    }

    @Override
    public void set(int index, int value) {
        if(index == 0)
            this.value = ComparatorMode.fromIndex(value);
    }

    @Override
    public int size() {
        return 1;
    }
}
