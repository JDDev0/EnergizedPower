package me.jddev0.ep.datagen.model;

import net.minecraft.block.Block;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.TexturedModel;

public final class ModTexturedModel {
    public static final TexturedModel.Factory ITEM_CONVEYOR_BELT_FLAT = TexturedModel.makeFactory(ModTexturedModel::itemConveyorBelt,
            ModModels.ITEM_CONVEYOR_BELT_FLAT_TEMPLATE);
    public static final TexturedModel.Factory ITEM_CONVEYOR_BELT_ASCENDING = TexturedModel.makeFactory(ModTexturedModel::itemConveyorBelt,
            ModModels.ITEM_CONVEYOR_BELT_ASCENDING_TEMPLATE);
    public static final TexturedModel.Factory ITEM_CONVEYOR_BELT_DESCENDING = TexturedModel.makeFactory(ModTexturedModel::itemConveyorBelt,
            ModModels.ITEM_CONVEYOR_BELT_DESCENDING_TEMPLATE);

    public static final TexturedModel.Factory FLUID_PIPE_CORE = TexturedModel.makeFactory(ModTexturedModel::fluidPipeCore,
            ModModels.FLUID_PIPE_CORE_TEMPLATE);
    public static final TexturedModel.Factory FLUID_PIPE_SIDE_CONNECTED = TexturedModel.makeFactory(ModTexturedModel::fluidPipeSideConnected,
            ModModels.FLUID_PIPE_SIDE_CONNECTED_TEMPLATE);
    public static final TexturedModel.Factory FLUID_PIPE_SIDE_EXTRACT = TexturedModel.makeFactory(ModTexturedModel::fluidPipeSideExtracted,
            ModModels.FLUID_PIPE_SIDE_EXTRACT_TEMPLATE);

    public static final TexturedModel.Factory FLUID_TANK = TexturedModel.makeFactory(ModTexturedModel::fluidTank,
            ModModels.FLUID_TANK_TEMPLATE);

    public static final TexturedModel.Factory CABLE_CORE = TexturedModel.makeFactory(ModTexturedModel::cableCore,
            ModModels.CABLE_CORE_TEMPLATE);
    public static final TexturedModel.Factory CABLE_SIDE = TexturedModel.makeFactory(ModTexturedModel::cableSide,
            ModModels.CABLE_SIDE_TEMPLATE);

    public static final TexturedModel.Factory SOLAR_PANEL = TexturedModel.makeFactory(ModTexturedModel::solarPanel,
            ModModels.SOLAR_PANEL_TEMPLATE);

    private ModTexturedModel() {}

    public static TextureMap itemConveyorBelt(Block block) {
        return new TextureMap().
                put(TextureKey.PARTICLE, TextureMap.getId(block)).
                put(ModTextureKey.BELT, TextureMap.getId(block));
    }

    public static TextureMap fluidPipeCore(Block block) {
        return new TextureMap().
                put(TextureKey.PARTICLE, TextureMap.getSubId(block, "_core")).
                put(ModTextureKey.FLUID_PIPE_CORE, TextureMap.getSubId(block, "_core"));
    }

    public static TextureMap fluidPipeSideConnected(Block block) {
        return new TextureMap().
                put(TextureKey.PARTICLE, TextureMap.getSubId(block, "_side_connected")).
                put(ModTextureKey.FLUID_PIPE_SIDE, TextureMap.getSubId(block, "_side_connected"));
    }

    public static TextureMap fluidPipeSideExtracted(Block block) {
        return new TextureMap().
                put(TextureKey.PARTICLE, TextureMap.getSubId(block, "_side_outer_extract")).
                put(ModTextureKey.FLUID_PIPE_SIDE_INNER, TextureMap.getSubId(block, "_side_inner_extract")).
                put(ModTextureKey.FLUID_PIPE_SIDE_OUTER, TextureMap.getSubId(block, "_side_outer_extract"));
    }

    public static TextureMap fluidTank(Block block) {
        return new TextureMap().
                put(TextureKey.FRONT, TextureMap.getSubId(block, "_front")).
                put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).
                put(TextureKey.UP, TextureMap.getSubId(block, "_top")).
                put(ModTextureKey.INTERIOR, TextureMap.getSubId(block, "_interior")).
                copy(TextureKey.UP, TextureKey.PARTICLE);
    }

    public static TextureMap cableCore(Block block) {
        return new TextureMap().
                put(TextureKey.PARTICLE, TextureMap.getId(block)).
                put(ModTextureKey.CABLE, TextureMap.getId(block));
    }

    public static TextureMap cableSide(Block block) {
        return new TextureMap().
                put(TextureKey.PARTICLE, TextureMap.getId(block)).
                put(ModTextureKey.CABLE, TextureMap.getId(block));
    }

    public static TextureMap solarPanel(Block block) {
        return new TextureMap().
                put(TextureKey.TOP, TextureMap.getSubId(block, "_top")).
                put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).
                copy(TextureKey.TOP, TextureKey.PARTICLE);
    }
}
