package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.CrystalGrowthChamberBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class CrystalGrowthChamberBlock extends WorkerMachineBlock<CrystalGrowthChamberBlockEntity> {
    public static final MapCodec<CrystalGrowthChamberBlock> CODEC = simpleCodec(CrystalGrowthChamberBlock::new);

    public CrystalGrowthChamberBlock(Properties props) {
        super(
                props,

                EPBlockEntities.CRYSTAL_GROWTH_CHAMBER_ENTITY,
                CrystalGrowthChamberBlockEntity.class, CrystalGrowthChamberBlockEntity::new, CrystalGrowthChamberBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
