package me.jddev0.ep.inventory.data;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnergyValueContainerData extends IntegerValueContainerData {
    public EnergyValueContainerData(Supplier<Integer> getter, Consumer<Integer> setter) {
        super(getter, setter);
    }
}
