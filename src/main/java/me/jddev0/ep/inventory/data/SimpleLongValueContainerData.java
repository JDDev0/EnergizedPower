package me.jddev0.ep.inventory.data;

import me.jddev0.ep.util.ByteUtils;
import net.minecraft.world.inventory.ContainerData;

public class SimpleLongValueContainerData implements ContainerData {
    private long value;

    public long getValue() {
        return value;
    }

    @Override
    public int get(int index) {
        return switch(index) {
            case 0, 1, 2, 3 -> ByteUtils.get2Bytes(value, index);
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        switch(index) {
            case 0, 1, 2, 3 -> this.value = ByteUtils.with2Bytes(
                    this.value, (short)value, index
            );
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
