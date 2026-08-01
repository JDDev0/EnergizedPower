package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.AutoCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AutoCrafterBlockEntity extends AbstractAutoCrafterBlockEntity {
    public final static long ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT = ModConfigs.COMMON_AUTO_CRAFTER_ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT.getValue();

    public AutoCrafterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.AUTO_CRAFTER_ENTITY, blockPos, blockState,

                "auto_crafter", AutoCrafterMenu::new,

                18,
                ModConfigs.COMMON_AUTO_CRAFTER_RECIPE_BLACKLIST.getValue(),
                ModConfigs.COMMON_AUTO_CRAFTER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_AUTO_CRAFTER_CAPACITY.getValue(),
                ModConfigs.COMMON_AUTO_CRAFTER_TRANSFER_RATE.getValue(),
                ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT
        );
    }

    @Override
    protected int initWorkerThreadCount() {
        return 1;
    }

    @Override
    protected int initOutputOnlySlotCount() {
        return 3;
    }
}