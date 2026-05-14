package me.jddev0.ep.machine.configuration;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ComparatorMode implements StringRepresentable {
    ITEM, FLUID, ENERGY;

    /**
     * @return Returns the enum value at index if index is valid otherwise ITEM will be returned
     */
    public static @NotNull ComparatorMode fromIndex(int index) {
        ComparatorMode[] values = values();

        if(index < 0 || index >= values.length)
            return ITEM;

        return values[index];
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.US);
    }
}
