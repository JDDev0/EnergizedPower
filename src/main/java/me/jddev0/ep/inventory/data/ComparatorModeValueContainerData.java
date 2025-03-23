package me.jddev0.ep.inventory.data;

import me.jddev0.ep.machine.configuration.ComparatorMode;
import net.minecraft.world.inventory.ContainerData;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ComparatorModeValueContainerData implements ContainerData {
    private final Supplier<ComparatorMode> getter;
    private final Consumer<ComparatorMode> setter;

    public ComparatorModeValueContainerData(Supplier<ComparatorMode> getter, Consumer<ComparatorMode> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int get(int index) {
        if(index == 0)
            return getter.get().ordinal();

        return 0;
    }

    @Override
    public void set(int index, int value) {
        if(index == 0)
            setter.accept(ComparatorMode.fromIndex(value));
    }

    @Override
    public int getCount() {
        return 1;
    }
}
