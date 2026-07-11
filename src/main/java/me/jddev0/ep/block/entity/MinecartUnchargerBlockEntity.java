package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.MinecartUnchargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartUnchargerBlockEntity extends AbstractMinecartUnchargerBlockEntity {
    public static final int MAX_TRANSFER = ModConfigs.COMMON_MINECART_UNCHARGER_TRANSFER_RATE.getValue();

    public MinecartUnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.MINECART_UNCHARGER_ENTITY.get(), blockPos, blockState,

                "minecart_uncharger", MinecartUnchargerMenu::new,

                ModConfigs.COMMON_MINECART_UNCHARGER_CAPACITY.getValue(),
                MAX_TRANSFER
        );
    }
}