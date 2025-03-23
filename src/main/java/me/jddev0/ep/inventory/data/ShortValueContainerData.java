package me.jddev0.ep.inventory.data;

import net.minecraft.world.inventory.ContainerData;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShortValueContainerData implements ContainerData {
    private final Supplier<Short> getter;
    private final Consumer<Short> setter;

    public ShortValueContainerData(Supplier<Short> getter, Consumer<Short> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int get(int index) {
        if(index == 0)
            return getter.get();

        return 0;
    }

    @Override
    public void set(int index, int value) {
        if(index == 0)
            setter.accept((short)value);
    }

    @Override
    public int getCount() {
        return 1;
    }
}
