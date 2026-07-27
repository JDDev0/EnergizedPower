package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.ElitePoweredFurnaceBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.ToIntFunction;

public class ElitePoweredFurnaceBlock extends HorizontallyOrientableWorkerMachineBlock<ElitePoweredFurnaceBlockEntity> {
    public static final MapCodec<ElitePoweredFurnaceBlock> CODEC = simpleCodec(ElitePoweredFurnaceBlock::new);

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(WORKING) ? 5 : 0;

    protected ElitePoweredFurnaceBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ELITE_POWERED_FURNACE_ENTITY,
                ElitePoweredFurnaceBlockEntity.class, ElitePoweredFurnaceBlockEntity::new, ElitePoweredFurnaceBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
