package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.CrystalGrowthChamberBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;

public class CrystalGrowthChamberBlock extends HorizontallyOrientableWorkerMachineBlock<CrystalGrowthChamberBlockEntity> {
    public CrystalGrowthChamberBlock(Properties props) {
        super(
                props,

                EPBlockEntities.CRYSTAL_GROWTH_CHAMBER_ENTITY,
                CrystalGrowthChamberBlockEntity.class, CrystalGrowthChamberBlockEntity::new, CrystalGrowthChamberBlockEntity::tick
        );
    }
}
