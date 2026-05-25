package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.InductionSmelterBlockEntity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.ToIntFunction;

public class InductionSmelterBlock extends HorizontallyOrientableWorkerMachineBlock<InductionSmelterBlockEntity> {
    public static final MapCodec<InductionSmelterBlock> CODEC = simpleCodec(InductionSmelterBlock::new);

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(WORKING) ? 5 : 0;

    protected InductionSmelterBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.INDUCTION_SMELTER_ENTITY,
                InductionSmelterBlockEntity.class, InductionSmelterBlockEntity::new, InductionSmelterBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
