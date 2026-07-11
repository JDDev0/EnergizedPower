package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.FluidPumpMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FluidPumpBlockEntity extends AbstractFluidPumpBlockEntity {
    public FluidPumpBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FLUID_PUMP_ENTITY.get(), blockPos, blockState,

                "fluid_pump", FluidPumpMenu::new,

                ModConfigs.COMMON_FLUID_PUMP_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_PUMP_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_FLUID_PUMP_CONSUMPTION_PER_TICK.getValue(),

                ModConfigs.COMMON_FLUID_PUMP_FLUID_TANK_CAPACITY.getValue() * 1000,

                ModConfigs.COMMON_FLUID_PUMP_NEXT_BLOCK_COOLDOWN.getValue(),
                ModConfigs.COMMON_FLUID_PUMP_EXTRACTION_DURATION.getValue(),

                ModConfigs.COMMON_FLUID_PUMP_EXTRACTION_RANGE.getValue(),
                ModConfigs.COMMON_FLUID_PUMP_EXTRACTION_DEPTH.getValue()
        );
    }

    @Override
    protected int initTankCount() {
        return 1;
    }
}