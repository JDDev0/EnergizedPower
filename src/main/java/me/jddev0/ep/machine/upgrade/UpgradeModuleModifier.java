package me.jddev0.ep.machine.upgrade;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public enum UpgradeModuleModifier implements StringRepresentable {
    SPEED, ENERGY_CONSUMPTION, ENERGY_PRODUCTION, ENERGY_CAPACITY, ENERGY_TRANSFER_RATE, DURATION, RANGE, EXTRACTION_DEPTH, EXTRACTION_RANGE, FURNACE_MODE, MOON_LIGHT,
    ITEM_EJECTOR, ITEM_PULLING;

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.US);
    }
}
