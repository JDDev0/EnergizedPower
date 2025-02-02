package me.jddev0.ep.datagen.model;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;

public final class ModTexturedModel {
    public static final TexturedModel.Provider ITEM_CONVEYOR_BELT_FLAT = TexturedModel.createDefault(ModTexturedModel::itemConveyorBelt,
            ModModelTemplates.ITEM_CONVEYOR_BELT_FLAT_TEMPLATE);
    public static final TexturedModel.Provider ITEM_CONVEYOR_BELT_ASCENDING = TexturedModel.createDefault(ModTexturedModel::itemConveyorBelt,
            ModModelTemplates.ITEM_CONVEYOR_BELT_ASCENDING_TEMPLATE);
    public static final TexturedModel.Provider ITEM_CONVEYOR_BELT_DESCENDING = TexturedModel.createDefault(ModTexturedModel::itemConveyorBelt,
            ModModelTemplates.ITEM_CONVEYOR_BELT_DESCENDING_TEMPLATE);

    public static final TexturedModel.Provider FLUID_PIPE_CORE = TexturedModel.createDefault(ModTexturedModel::fluidPipeCore,
            ModModelTemplates.FLUID_PIPE_CORE_TEMPLATE);
    public static final TexturedModel.Provider FLUID_PIPE_SIDE_CONNECTED = TexturedModel.createDefault(ModTexturedModel::fluidPipeSideConnected,
            ModModelTemplates.FLUID_PIPE_SIDE_CONNECTED_TEMPLATE);
    public static final TexturedModel.Provider FLUID_PIPE_SIDE_EXTRACT = TexturedModel.createDefault(ModTexturedModel::fluidPipeSideExtracted,
            ModModelTemplates.FLUID_PIPE_SIDE_EXTRACT_TEMPLATE);

    public static final TexturedModel.Provider FLUID_TANK = TexturedModel.createDefault(ModTexturedModel::fluidTank,
            ModModelTemplates.FLUID_TANK_TEMPLATE);

    public static final TexturedModel.Provider CABLE_CORE = TexturedModel.createDefault(ModTexturedModel::cableCore,
            ModModelTemplates.CABLE_CORE_TEMPLATE);
    public static final TexturedModel.Provider CABLE_SIDE = TexturedModel.createDefault(ModTexturedModel::cableSide,
            ModModelTemplates.CABLE_SIDE_TEMPLATE);

    public static final TexturedModel.Provider SOLAR_PANEL = TexturedModel.createDefault(ModTexturedModel::solarPanel,
            ModModelTemplates.SOLAR_PANEL_TEMPLATE);

    private ModTexturedModel() {}

    public static TextureMapping itemConveyorBelt(Block block) {
        return new TextureMapping().
                put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block)).
                put(ModTextureSlot.BELT, TextureMapping.getBlockTexture(block));
    }

    public static TextureMapping fluidPipeCore(Block block) {
        return new TextureMapping().
                put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, "_core")).
                put(ModTextureSlot.FLUID_PIPE_CORE, TextureMapping.getBlockTexture(block, "_core"));
    }

    public static TextureMapping fluidPipeSideConnected(Block block) {
        return new TextureMapping().
                put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, "_side_connected")).
                put(ModTextureSlot.FLUID_PIPE_SIDE, TextureMapping.getBlockTexture(block, "_side_connected"));
    }

    public static TextureMapping fluidPipeSideExtracted(Block block) {
        return new TextureMapping().
                put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, "_side_outer_extract")).
                put(ModTextureSlot.FLUID_PIPE_SIDE_INNER, TextureMapping.getBlockTexture(block, "_side_inner_extract")).
                put(ModTextureSlot.FLUID_PIPE_SIDE_OUTER, TextureMapping.getBlockTexture(block, "_side_outer_extract"));
    }

    public static TextureMapping fluidTank(Block block) {
        return new TextureMapping().
                put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front")).
                put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side")).
                put(TextureSlot.UP, TextureMapping.getBlockTexture(block, "_top")).
                put(ModTextureSlot.INTERIOR, TextureMapping.getBlockTexture(block, "_interior")).
                copySlot(TextureSlot.UP, TextureSlot.PARTICLE);
    }

    public static TextureMapping cableCore(Block block) {
        return new TextureMapping().
                put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block)).
                put(ModTextureSlot.CABLE, TextureMapping.getBlockTexture(block));
    }

    public static TextureMapping cableSide(Block block) {
        return new TextureMapping().
                put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block)).
                put(ModTextureSlot.CABLE, TextureMapping.getBlockTexture(block));
    }

    public static TextureMapping solarPanel(Block block) {
        return new TextureMapping().
                put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top")).
                put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side")).
                copySlot(TextureSlot.TOP, TextureSlot.PARTICLE);
    }
}
