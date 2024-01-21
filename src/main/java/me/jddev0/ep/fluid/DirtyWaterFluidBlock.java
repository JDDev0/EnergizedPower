package me.jddev0.ep.fluid;

import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;

public class DirtyWaterFluidBlock extends FluidBlock {
    public DirtyWaterFluidBlock(FlowableFluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Override
    public String getTranslationKey() {
        return "fluid_type.energizedpower.dirty_water";
    }
}
