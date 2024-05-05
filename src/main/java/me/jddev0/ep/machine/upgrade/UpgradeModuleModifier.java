package me.jddev0.ep.machine.upgrade;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum UpgradeModuleModifier implements StringRepresentable {
    SPEED, ENERGY_CONSUMPTION, ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, DURATION, MOON_LIGHT;

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.US);
    }
}
