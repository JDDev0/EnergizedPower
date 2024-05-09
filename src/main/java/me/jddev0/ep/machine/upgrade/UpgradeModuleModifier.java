package me.jddev0.ep.machine.upgrade;

import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum UpgradeModuleModifier implements StringIdentifiable {
    SPEED, ENERGY_CONSUMPTION, ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, DURATION, RANGE, FURNACE_MODE, MOON_LIGHT;

    @Override
    @NotNull
    public String asString() {
        return name().toLowerCase(Locale.US);
    }
}
