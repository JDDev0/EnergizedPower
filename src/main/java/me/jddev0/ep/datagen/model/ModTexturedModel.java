package me.jddev0.ep.datagen.model;

import net.minecraft.block.Block;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.TexturedModel;

public final class ModTexturedModel {
    public static final TexturedModel.Factory CABLE_CORE = TexturedModel.makeFactory(ModTexturedModel::cableCore, ModModels.CABLE_CORE_TEMPLATE);
    public static final TexturedModel.Factory CABLE_SIDE = TexturedModel.makeFactory(ModTexturedModel::cableSide, ModModels.CABLE_SIDE_TEMPLATE);

    private ModTexturedModel() {}

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
}
