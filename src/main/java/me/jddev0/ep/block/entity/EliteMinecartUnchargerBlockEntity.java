package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EliteMinecartUnchargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EliteMinecartUnchargerBlockEntity extends AbstractMinecartUnchargerBlockEntity {
    public static final long MAX_TRANSFER = ModConfigs.COMMON_ELITE_MINECART_UNCHARGER_TRANSFER_RATE.getValue();

    public EliteMinecartUnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ELITE_MINECART_UNCHARGER_ENTITY, blockPos, blockState,

                "elite_minecart_uncharger", EliteMinecartUnchargerMenu::new,

                ModConfigs.COMMON_ELITE_MINECART_UNCHARGER_CAPACITY.getValue(),
                MAX_TRANSFER
        );
    }
}