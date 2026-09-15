package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class PlantGrowthChamberBlock extends HorizontallyOrientableWorkerMachineBlock<PlantGrowthChamberBlockEntity> {
    protected PlantGrowthChamberBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.PLANT_GROWTH_CHAMBER_ENTITY,
                PlantGrowthChamberBlockEntity.class, PlantGrowthChamberBlockEntity::new, PlantGrowthChamberBlockEntity::tick
        );
    }
}
