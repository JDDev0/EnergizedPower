package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.AdvancedUnchargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedUnchargerBlockEntity extends AbstractUnchargerBlockEntity {
    public AdvancedUnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_UNCHARGER_ENTITY, blockPos, blockState,

                "advanced_uncharger", AdvancedUnchargerMenu::new,

                3,
                ModConfigs.COMMON_ADVANCED_UNCHARGER_CAPACITY_PER_SLOT.getValue(),
                ModConfigs.COMMON_ADVANCED_UNCHARGER_TRANSFER_RATE_PER_SLOT.getValue()
        );
    }
}