package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.AdvancedPoweredFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedPoweredFurnaceBlockEntity extends AbstractPoweredFurnaceBlockEntity {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_FLUID_TANK_CAPACITY.getValue();

    public AdvancedPoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_POWERED_FURNACE_ENTITY.get(), blockPos, blockState,

                "advanced_powered_furnace", AdvancedPoweredFurnaceMenu::new,

                6,
                ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_BLACKLIST.getValue(),
                ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue(),

                ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_INPUT_PER_TICK.getValue(),

                TANK_CAPACITY
        );
    }

    @Override
    protected int initWorkerThreadCount() {
        return 3;
    }
}