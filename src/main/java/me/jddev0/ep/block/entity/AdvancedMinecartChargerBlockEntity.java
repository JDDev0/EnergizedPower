package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.AdvancedMinecartChargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedMinecartChargerBlockEntity extends AbstractMinecartChargerBlockEntity {
    public static final int MAX_TRANSFER = ModConfigs.COMMON_ADVANCED_MINECART_CHARGER_TRANSFER_RATE.getValue();

    public AdvancedMinecartChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_MINECART_CHARGER_ENTITY.get(), blockPos, blockState,

                "advanced_minecart_charger", AdvancedMinecartChargerMenu::new,

                ModConfigs.COMMON_ADVANCED_MINECART_CHARGER_CAPACITY.getValue(),
                MAX_TRANSFER
        );
    }
}