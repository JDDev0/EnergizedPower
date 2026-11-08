package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class FiltrationPlantBlock extends HorizontallyOrientableWorkerMachineBlock<FiltrationPlantBlockEntity> {
    public FiltrationPlantBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.FILTRATION_PLANT_ENTITY,
                FiltrationPlantBlockEntity.class, FiltrationPlantBlockEntity::new, FiltrationPlantBlockEntity::tick
        );
    }
}
