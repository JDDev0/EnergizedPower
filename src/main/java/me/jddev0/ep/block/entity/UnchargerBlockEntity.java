package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.UnchargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class UnchargerBlockEntity extends AbstractUnchargerBlockEntity {
    public UnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.UNCHARGER_ENTITY, blockPos, blockState,

                "uncharger", UnchargerMenu::new,

                1,
                ModConfigs.COMMON_UNCHARGER_CAPACITY.getValue(),
                ModConfigs.COMMON_UNCHARGER_TRANSFER_RATE.getValue()
        );
    }
}