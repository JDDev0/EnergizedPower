package me.jddev0.ep.inventory.data;

import me.jddev0.ep.util.ByteUtils;
import net.minecraft.screen.PropertyDelegate;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LongValueContainerData implements PropertyDelegate {
    private final Supplier<Long> getter;
    private final Consumer<Long> setter;

    public LongValueContainerData(Supplier<Long> getter, Consumer<Long> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int get(int index) {
        return switch(index) {
            case 0, 1, 2, 3 -> ByteUtils.get2Bytes(getter.get(), index);
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        switch(index) {
            case 0, 1, 2, 3 -> setter.accept(ByteUtils.with2Bytes(
                    getter.get(), (short)value, index
            ));
        }
    }

    @Override
    public int size() {
        return 4;
    }
}
