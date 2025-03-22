package me.jddev0.ep.inventory.data;

import me.jddev0.ep.machine.configuration.RedstoneMode;
import net.minecraft.screen.PropertyDelegate;

public class SimpleRedstoneModeValueContainerData implements PropertyDelegate {
    private RedstoneMode value = RedstoneMode.IGNORE;

    public RedstoneMode getValue() {
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
            this.value = RedstoneMode.fromIndex(value);
    }

    @Override
    public int size() {
        return 1;
    }
}
