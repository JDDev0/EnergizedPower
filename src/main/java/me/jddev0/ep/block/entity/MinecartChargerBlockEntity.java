package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.MinecartChargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartChargerBlockEntity extends AbstractMinecartChargerBlockEntity {
    public static final long MAX_TRANSFER = ModConfigs.COMMON_MINECART_CHARGER_TRANSFER_RATE.getValue();

    public MinecartChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.MINECART_CHARGER_ENTITY, blockPos, blockState,

                "minecart_charger", MinecartChargerMenu::new,

                ModConfigs.COMMON_MINECART_CHARGER_CAPACITY.getValue(),
                MAX_TRANSFER
        );
    }
}