package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.ElitePoweredFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ElitePoweredFurnaceBlockEntity extends AbstractPoweredFurnaceBlockEntity {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_ELITE_POWERED_FURNACE_FLUID_TANK_CAPACITY.getValue();

    public ElitePoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ELITE_POWERED_FURNACE_ENTITY.get(), blockPos, blockState,

                "elite_powered_furnace", ElitePoweredFurnaceMenu::new,

                14,
                ModConfigs.COMMON_ELITE_POWERED_FURNACE_RECIPE_BLACKLIST.getValue(),
                ModConfigs.COMMON_ELITE_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue(),

                ModConfigs.COMMON_ELITE_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_ELITE_POWERED_FURNACE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ELITE_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_INPUT_PER_TICK.getValue(),

                TANK_CAPACITY
        );
    }

    @Override
    protected int initWorkerThreadCount() {
        return 7;
    }
}