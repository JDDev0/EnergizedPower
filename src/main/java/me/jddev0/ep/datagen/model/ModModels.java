package me.jddev0.ep.datagen.model;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public final class ModModels {
    public static final Model ORIENTABLE_VERTICAL = block("orientable_vertical",
            TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.FRONT, TextureKey.SIDE);
    public static final Model ORIENTABLE_VERTICAL_WITH_BACK = block("orientable_vertical_with_back",
            TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.FRONT, TextureKey.BACK, TextureKey.SIDE);

    public static final Model ITEM_CONVEYOR_BELT_FLAT_TEMPLATE = block("item_conveyor_belt_flat_template",
            TextureKey.PARTICLE, ModTextureKey.BELT);
    public static final Model ITEM_CONVEYOR_BELT_ASCENDING_TEMPLATE = block("item_conveyor_belt_ascending_template",
            TextureKey.PARTICLE, ModTextureKey.BELT);
    public static final Model ITEM_CONVEYOR_BELT_DESCENDING_TEMPLATE = block("item_conveyor_belt_descending_template",
            TextureKey.PARTICLE, ModTextureKey.BELT);

    public static final Model FLUID_PIPE_CORE_TEMPLATE = block("fluid_pipe_core_template",
            TextureKey.PARTICLE, ModTextureKey.FLUID_PIPE_CORE);
    public static final Model FLUID_PIPE_SIDE_CONNECTED_TEMPLATE = block("fluid_pipe_side_connected_template",
            TextureKey.PARTICLE, ModTextureKey.FLUID_PIPE_SIDE);
    public static final Model FLUID_PIPE_SIDE_EXTRACT_TEMPLATE = block("fluid_pipe_side_extract_template",
            TextureKey.PARTICLE, ModTextureKey.FLUID_PIPE_SIDE_INNER, ModTextureKey.FLUID_PIPE_SIDE_OUTER);

    public static final Model FLUID_TANK_TEMPLATE = block("fluid_tank_template",
            TextureKey.PARTICLE, TextureKey.FRONT, TextureKey.SIDE, TextureKey.UP, ModTextureKey.INTERIOR);

    public static final Model CABLE_CORE_TEMPLATE = block("cable_core_template",
            TextureKey.PARTICLE, ModTextureKey.CABLE);
    public static final Model CABLE_SIDE_TEMPLATE = block("cable_side_template",
            TextureKey.PARTICLE, ModTextureKey.CABLE);

    public static final Model SOLAR_PANEL_TEMPLATE = block("solar_panel_template",
            TextureKey.PARTICLE, TextureKey.TOP, TextureKey.SIDE);

    public static final Model SINGLE_SIDE = block("single_side",
            TextureKey.PARTICLE, TextureKey.SIDE);

    private ModModels() {}

    private static Model block(String parent, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(EPAPI.id("block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static Model block(String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(EPAPI.id("block/" + parent)), Optional.of(variant), requiredTextureKeys);
    }
}
