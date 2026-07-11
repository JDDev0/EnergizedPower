package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.PoweredFurnaceMenu;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredFurnaceBlockEntity extends AbstractPoweredFurnaceBlockEntity {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(1000 * ModConfigs.COMMON_POWERED_FURNACE_FLUID_TANK_CAPACITY.getValue());

    public PoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.POWERED_FURNACE_ENTITY, blockPos, blockState,

                "powered_furnace", PoweredFurnaceMenu::new,

                2,
                ModConfigs.COMMON_POWERED_FURNACE_RECIPE_BLACKLIST.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue(),

                ModConfigs.COMMON_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                TANK_CAPACITY
        );
    }

    @Override
    protected int initWorkerThreadCount() {
        return 1;
    }
}