package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.*;

public class PlantGrowthChamberBlock extends HorizontallyOrientableWorkerMachineBlock<PlantGrowthChamberBlockEntity> {
    public static final MapCodec<PlantGrowthChamberBlock> CODEC = simpleCodec(PlantGrowthChamberBlock::new);

    protected PlantGrowthChamberBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.PLANT_GROWTH_CHAMBER_ENTITY,
                PlantGrowthChamberBlockEntity.class, PlantGrowthChamberBlockEntity::new, PlantGrowthChamberBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
