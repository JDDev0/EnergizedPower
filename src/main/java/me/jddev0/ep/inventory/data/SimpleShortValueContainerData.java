package me.jddev0.ep.inventory.data;

import net.minecraft.world.inventory.ContainerData;

public class SimpleShortValueContainerData implements ContainerData {
    private short value;

    public short getValue() {
        return value;
    }

    @Override
    public int get(int index) {
        if(index == 0)
            return value;

        return 0;
    }

    @Override
    public void set(int index, int value) {
        if(index == 0)
            this.value = (short)value;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
