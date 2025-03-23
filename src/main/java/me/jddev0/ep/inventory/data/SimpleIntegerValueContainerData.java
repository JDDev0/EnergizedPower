package me.jddev0.ep.inventory.data;

import me.jddev0.ep.util.ByteUtils;
import net.minecraft.screen.PropertyDelegate;

public class SimpleIntegerValueContainerData implements PropertyDelegate {
    private int value;

    public int getValue() {
        return value;
    }

    @Override
    public int get(int index) {
        return switch(index) {
            case 0, 1 -> ByteUtils.get2Bytes(value, index);
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        switch(index) {
            case 0, 1 -> this.value = ByteUtils.with2Bytes(
                    this.value, (short)value, index
            );
        }
    }

    @Override
    public int size() {
        return 2;
    }
}
