package me.jddev0.ep.machine.upgrade;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum UpgradeModuleModifier implements StringRepresentable {
    ENERGY_CONSUMPTION, SPEED, MOON_LIGHT;

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.US);
    }
}
