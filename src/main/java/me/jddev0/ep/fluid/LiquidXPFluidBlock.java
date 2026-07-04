package me.jddev0.ep.fluid;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

public class LiquidXPFluidBlock extends LiquidBlock {
    public LiquidXPFluidBlock(FlowingFluid fluid, Properties settings) {
        super(fluid, settings.overrideDescription("fluid_type.energizedpower.liquid_xp"));
    }
}
