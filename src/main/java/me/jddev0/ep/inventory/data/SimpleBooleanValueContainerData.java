package me.jddev0.ep.inventory.data;


import net.minecraft.screen.PropertyDelegate;

public class SimpleBooleanValueContainerData implements PropertyDelegate {
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
    public int size() {
        return 1;
    }
}
