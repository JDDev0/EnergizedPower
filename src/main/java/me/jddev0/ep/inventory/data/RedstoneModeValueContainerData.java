package me.jddev0.ep.inventory.data;

import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.screen.PropertyDelegate;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RedstoneModeValueContainerData implements PropertyDelegate {
    private final Supplier<RedstoneMode> getter;
    private final Consumer<RedstoneMode> setter;

    public RedstoneModeValueContainerData(Supplier<RedstoneMode> getter, Consumer<RedstoneMode> setter) {
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
            setter.accept(RedstoneMode.fromIndex(value));
    }

    @Override
    public int size() {
        return 1;
    }
}
