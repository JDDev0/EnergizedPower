package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.AdvancedMinecartUnchargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedMinecartUnchargerBlockEntity extends AbstractMinecartUnchargerBlockEntity {
    public static final int MAX_TRANSFER = ModConfigs.COMMON_ADVANCED_MINECART_UNCHARGER_TRANSFER_RATE.getValue();

    public AdvancedMinecartUnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_MINECART_UNCHARGER_ENTITY.get(), blockPos, blockState,

                "advanced_minecart_uncharger", AdvancedMinecartUnchargerMenu::new,

                ModConfigs.COMMON_ADVANCED_MINECART_UNCHARGER_CAPACITY.getValue(),
                MAX_TRANSFER
        );
    }
}