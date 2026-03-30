package me.jddev0.ep.util;

import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.FluidState;

public final class FluidRenderUtils {
    private FluidRenderUtils() {}

    public static TextureAtlasSprite getStillSprite(FluidVariant fluidVariant) {
        FluidState fluidState = fluidVariant.getFluid().defaultFluidState();
        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);

        return fluidModel.stillMaterial().sprite();
    }

    public static int getTintColor(FluidVariant fluidVariant) {
        return FluidVariantRendering.getColor(fluidVariant, Minecraft.getInstance().level,
                Minecraft.getInstance().player == null?null:Minecraft.getInstance().player.getOnPos());
    }
}
