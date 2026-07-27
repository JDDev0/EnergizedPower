package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EliteUnchargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EliteUnchargerBlockEntity extends AbstractUnchargerBlockEntity {
    public EliteUnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ELITE_UNCHARGER_ENTITY.get(), blockPos, blockState,

                "elite_uncharger", EliteUnchargerMenu::new,

                7,
                ModConfigs.COMMON_ELITE_UNCHARGER_CAPACITY_PER_SLOT.getValue(),
                ModConfigs.COMMON_ELITE_UNCHARGER_TRANSFER_RATE_PER_SLOT.getValue()
        );
    }
}