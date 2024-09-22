package me.jddev0.ep.datagen.model;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public final class ModModels {
    public static final Model CABLE_CORE_TEMPLATE = block("cable_core_template", TextureKey.PARTICLE, ModTextureKey.CABLE);
    public static final Model CABLE_SIDE_TEMPLATE = block("cable_side_template", TextureKey.PARTICLE, ModTextureKey.CABLE);

    private ModModels() {}

    private static Model block(String parent, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.of(EnergizedPowerMod.MODID, "block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static Model block(String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.of(EnergizedPowerMod.MODID, "block/" + parent)), Optional.of(variant), requiredTextureKeys);
    }
}
