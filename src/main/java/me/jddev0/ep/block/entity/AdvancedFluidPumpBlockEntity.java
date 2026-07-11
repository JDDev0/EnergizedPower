package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.AdvancedFluidPumpMenu;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedFluidPumpBlockEntity extends AbstractFluidPumpBlockEntity {
    public AdvancedFluidPumpBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_FLUID_PUMP_ENTITY, blockPos, blockState,

                "advanced_fluid_pump", AdvancedFluidPumpMenu::new,

                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_CONSUMPTION_PER_TICK.getValue(),

                FluidUtils.convertMilliBucketsToDroplets(ModConfigs.COMMON_ADVANCED_FLUID_PUMP_FLUID_TANK_CAPACITY.getValue() * 1000),

                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_NEXT_BLOCK_COOLDOWN.getValue(),
                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_DURATION.getValue(),

                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_RANGE.getValue(),
                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_DEPTH.getValue()
        );
    }

    @Override
    protected int initTankCount() {
        return 4;
    }
}