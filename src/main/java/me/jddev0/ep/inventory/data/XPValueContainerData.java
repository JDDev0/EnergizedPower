package me.jddev0.ep.inventory.data;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class XPValueContainerData extends LongValueContainerData {
    public XPValueContainerData(Supplier<Long> getter, Consumer<Long> setter) {
        super(getter, setter);
    }
}
