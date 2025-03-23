package me.jddev0.ep.inventory.data;

import net.minecraft.world.inventory.ContainerData;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanValueContainerData implements ContainerData {
    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;

    public BooleanValueContainerData(Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int get(int index) {
        if(index == 0)
            return getter.get()?1:0;

        return 0;
    }

    @Override
    public void set(int index, int value) {
        if(index == 0)
            setter.accept(value != 0);
    }

    @Override
    public int getCount() {
        return 1;
    }
}
