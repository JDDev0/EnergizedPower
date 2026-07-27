package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EliteChargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EliteChargerBlockEntity extends AbstractChargerBlockEntity {
    public EliteChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ELITE_CHARGER_ENTITY, blockPos, blockState,

                "elite_charger", EliteChargerMenu::new,

                7, ModConfigs.COMMON_ELITE_CHARGER_CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER.getValue(),

                ModConfigs.COMMON_ELITE_CHARGER_CAPACITY_PER_SLOT.getValue(),
                ModConfigs.COMMON_ELITE_CHARGER_TRANSFER_RATE_PER_SLOT.getValue()
        );
    }
}