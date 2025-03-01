package me.jddev0.ep.inventory.data;

import net.minecraft.world.inventory.ContainerData;

public class SimpleBooleanValueContainerData implements ContainerData {
    private boolean value;

    public boolean getValue() {
        return value;
    }

    @Override
    public int get(int index) {
        if(index == 0)
            return value?1:0;

        return 0;
    }

    @Override
    public void set(int index, int value) {
        if(index == 0)
            this.value = value != 0;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
