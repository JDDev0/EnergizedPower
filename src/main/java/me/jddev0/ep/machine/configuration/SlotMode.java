package me.jddev0.ep.machine.configuration;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum SlotMode implements StringRepresentable {
    INPUT, OUTPUT, BOTH;

    /**
     * @return Returns the enum value at index if index is valid otherwise INPUT will be returned
     */
    public static @NotNull SlotMode fromIndex(int index) {
        SlotMode[] values = values();

        if(index < 0 || index >= values.length)
            return INPUT;

        return values[index];
    }

    public boolean canInput() {
        return this == INPUT || this == BOTH;
    }

    public boolean canOutput() {
        return this == OUTPUT || this == BOTH;
    }

    public int getHighlightColorRGB() {
        return switch(this) {
            case INPUT -> 0x0000de;
            case OUTPUT -> 0xde7721;
            case BOTH -> 0x21de21;
        };
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.US);
    }
}
