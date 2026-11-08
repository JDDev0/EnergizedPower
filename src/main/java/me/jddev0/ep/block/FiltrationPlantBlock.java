package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class FiltrationPlantBlock extends HorizontallyOrientableWorkerMachineBlock<FiltrationPlantBlockEntity> {
    public static final MapCodec<FiltrationPlantBlock> CODEC = simpleCodec(FiltrationPlantBlock::new);

    public FiltrationPlantBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.FILTRATION_PLANT_ENTITY,
                FiltrationPlantBlockEntity.class, FiltrationPlantBlockEntity::new, FiltrationPlantBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
