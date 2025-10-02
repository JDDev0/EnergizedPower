package me.jddev0.ep.block.entity.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTankBlockEntityRenderState extends BlockEntityRenderState {
    public int tankCapacity;
    public FluidStack fluidStack;
    public Direction facing;
}
