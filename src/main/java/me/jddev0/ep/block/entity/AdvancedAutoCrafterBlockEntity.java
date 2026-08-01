package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedAutoCrafterBlockEntity extends AbstractAutoCrafterBlockEntity {
    public final static long ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT = ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT.getValue();

    public AdvancedAutoCrafterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_AUTO_CRAFTER_ENTITY, blockPos, blockState,

                "advanced_auto_crafter", AdvancedAutoCrafterMenu::new,

                27,
                ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_RECIPE_BLACKLIST.getValue(),
                ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_TRANSFER_RATE.getValue(),
                ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT
        );
    }

    @Override
    protected int initWorkerThreadCount() {
        return 3;
    }

    @Override
    protected int initOutputOnlySlotCount() {
        return 5;
    }
}