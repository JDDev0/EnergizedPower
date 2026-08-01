package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EliteAutoCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EliteAutoCrafterBlockEntity extends AbstractAutoCrafterBlockEntity {
    public final static long ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT = ModConfigs.COMMON_ELITE_AUTO_CRAFTER_ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT.getValue();

    public EliteAutoCrafterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ELITE_AUTO_CRAFTER_ENTITY, blockPos, blockState,

                "elite_auto_crafter", EliteAutoCrafterMenu::new,

                36,
                ModConfigs.COMMON_ELITE_AUTO_CRAFTER_RECIPE_BLACKLIST.getValue(),
                ModConfigs.COMMON_ELITE_AUTO_CRAFTER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_ELITE_AUTO_CRAFTER_CAPACITY.getValue(),
                ModConfigs.COMMON_ELITE_AUTO_CRAFTER_TRANSFER_RATE.getValue(),
                ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT
        );
    }

    @Override
    protected int initWorkerThreadCount() {
        return 7;
    }

    @Override
    protected int initOutputOnlySlotCount() {
        return 9;
    }
}