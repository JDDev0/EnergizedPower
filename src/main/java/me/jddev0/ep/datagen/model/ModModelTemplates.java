package me.jddev0.ep.datagen.model;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;

import java.util.Optional;

public final class ModModelTemplates {
    public static final ModelTemplate ORIENTABLE_VERTICAL = block("orientable_vertical",
            TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.FRONT, TextureSlot.SIDE);
    public static final ModelTemplate ORIENTABLE_VERTICAL_WITH_BACK = block("orientable_vertical_with_back",
            TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.FRONT, TextureSlot.BACK, TextureSlot.SIDE);

    public static final ModelTemplate ITEM_CONVEYOR_BELT_FLAT_TEMPLATE = block("item_conveyor_belt_flat_template",
            TextureSlot.PARTICLE, ModTextureSlot.BELT);
    public static final ModelTemplate ITEM_CONVEYOR_BELT_ASCENDING_TEMPLATE = block("item_conveyor_belt_ascending_template",
            TextureSlot.PARTICLE, ModTextureSlot.BELT);
    public static final ModelTemplate ITEM_CONVEYOR_BELT_DESCENDING_TEMPLATE = block("item_conveyor_belt_descending_template",
            TextureSlot.PARTICLE, ModTextureSlot.BELT);

    public static final ModelTemplate FLUID_PIPE_CORE_TEMPLATE = block("fluid_pipe_core_template",
            TextureSlot.PARTICLE, ModTextureSlot.FLUID_PIPE_CORE);
    public static final ModelTemplate FLUID_PIPE_SIDE_CONNECTED_TEMPLATE = block("fluid_pipe_side_connected_template",
            TextureSlot.PARTICLE, ModTextureSlot.FLUID_PIPE_SIDE);
    public static final ModelTemplate FLUID_PIPE_SIDE_EXTRACT_TEMPLATE = block("fluid_pipe_side_extract_template",
            TextureSlot.PARTICLE, ModTextureSlot.FLUID_PIPE_SIDE_INNER, ModTextureSlot.FLUID_PIPE_SIDE_OUTER);

    public static final ModelTemplate FLUID_TANK_TEMPLATE = block("fluid_tank_template",
            TextureSlot.PARTICLE, TextureSlot.FRONT, TextureSlot.SIDE, TextureSlot.UP, ModTextureSlot.INTERIOR);

    public static final ModelTemplate CABLE_CORE_TEMPLATE = block("cable_core_template",
            TextureSlot.PARTICLE, ModTextureSlot.CABLE);
    public static final ModelTemplate CABLE_SIDE_TEMPLATE = block("cable_side_template",
            TextureSlot.PARTICLE, ModTextureSlot.CABLE);

    public static final ModelTemplate SOLAR_PANEL_TEMPLATE = block("solar_panel_template",
            TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.SIDE);

    private ModModelTemplates() {}

    private static ModelTemplate block(String parent, TextureSlot... requiredTextureSlots) {
        return new ModelTemplate(Optional.of(EPAPI.id("block/" + parent)), Optional.empty(), requiredTextureSlots);
    }

    private static ModelTemplate block(String parent, String variant, TextureSlot... requiredTextureSlots) {
        return new ModelTemplate(Optional.of(EPAPI.id("block/" + parent)), Optional.of(variant), requiredTextureSlots);
    }
}
