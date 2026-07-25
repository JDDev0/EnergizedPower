package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.ChargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChargerBlockEntity extends AbstractChargerBlockEntity {
    public static final float CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_CHARGER_CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER.getValue();

    public ChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.CHARGER_ENTITY.get(), blockPos, blockState,

                "charger", ChargerMenu::new,

                1, CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER,

                ModConfigs.COMMON_CHARGER_CAPACITY.getValue(),
                ModConfigs.COMMON_CHARGER_TRANSFER_RATE.getValue()
        );
    }
}