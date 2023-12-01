package me.jddev0.ep.machine.configuration;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum RedstoneMode implements StringRepresentable {
    IGNORE, HIGH, LOW;

    /**
     * @return Returns the enum value at index if index is valid otherwise IGNORE will be returned
     */
    public static @NotNull RedstoneMode fromIndex(int index) {
        RedstoneMode[] values = values();

        if(index < 0 || index >= values.length)
            return IGNORE;

        return values[index];
    }

    public boolean isActive(boolean powered) {
        return switch(this) {
            case IGNORE -> true;
            case HIGH -> powered;
            case LOW -> !powered;
        };
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.US);
    }
}
