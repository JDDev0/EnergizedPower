package me.jddev0.ep.fluid;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

public class DirtyWaterFluidBlock extends LiquidBlock {
    public DirtyWaterFluidBlock(FlowingFluid fluid, Properties settings) {
        super(fluid, settings.overrideDescription("fluid_type.energizedpower.dirty_water"));
    }
}
