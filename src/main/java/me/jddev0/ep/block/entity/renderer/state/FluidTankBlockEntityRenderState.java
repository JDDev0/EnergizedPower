package me.jddev0.ep.block.entity.renderer.state;

import me.jddev0.ep.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class FluidTankBlockEntityRenderState extends BlockEntityRenderState {
    public long tankCapacity;
    public FluidStack fluidStack;
    public Direction facing;
}
