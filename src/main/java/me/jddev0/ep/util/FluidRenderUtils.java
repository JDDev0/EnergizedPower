package me.jddev0.ep.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;

public final class FluidRenderUtils {
    private FluidRenderUtils() {}

    public static TextureAtlasSprite getStillSprite(FluidStack fluidStack) {
        FluidState fluidState = fluidStack.getFluid().defaultFluidState();
        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);

        return fluidModel.stillMaterial().sprite();
    }

    public static int getTintColor(FluidStack fluidStack) {
        FluidState fluidState = fluidStack.getFluid().defaultFluidState();
        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);

        if(fluidModel.fluidTintSource() == null) {
            return -1;
        }

        ClientLevel level = Minecraft.getInstance().level;
        BlockPos pos = Minecraft.getInstance().player == null?null:Minecraft.getInstance().player.getOnPos();

        if(level != null && pos != null) {
            return fluidModel.fluidTintSource().colorInWorld(Blocks.AIR.defaultBlockState(), level, pos);
        } else {
            return fluidModel.fluidTintSource().color(Blocks.AIR.defaultBlockState());
        }
    }
}
