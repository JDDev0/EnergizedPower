package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EliteMinecartChargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EliteMinecartChargerBlockEntity extends AbstractMinecartChargerBlockEntity {
    public static final long MAX_TRANSFER = ModConfigs.COMMON_ELITE_MINECART_CHARGER_TRANSFER_RATE.getValue();

    public EliteMinecartChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ELITE_MINECART_CHARGER_ENTITY, blockPos, blockState,

                "elite_minecart_charger", EliteMinecartChargerMenu::new,

                ModConfigs.COMMON_ELITE_MINECART_CHARGER_CAPACITY.getValue(),
                MAX_TRANSFER
        );
    }
}