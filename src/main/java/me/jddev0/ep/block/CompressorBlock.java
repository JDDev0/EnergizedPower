package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.CompressorBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class CompressorBlock extends WorkerMachineBlock<CompressorBlockEntity> {
    public static final MapCodec<CompressorBlock> CODEC = simpleCodec(CompressorBlock::new);

    public CompressorBlock(Properties props) {
        super(
                props,

                EPBlockEntities.COMPRESSOR_ENTITY,
                CompressorBlockEntity.class, CompressorBlockEntity::new, CompressorBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
