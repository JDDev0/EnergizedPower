package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedUnchargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class AdvancedUnchargerBlock extends WorkerMachineBlock<AdvancedUnchargerBlockEntity> {
    public static final MapCodec<AdvancedUnchargerBlock> CODEC = simpleCodec(AdvancedUnchargerBlock::new);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public AdvancedUnchargerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ADVANCED_UNCHARGER_ENTITY,
                AdvancedUnchargerBlockEntity.class, AdvancedUnchargerBlockEntity::new, AdvancedUnchargerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
