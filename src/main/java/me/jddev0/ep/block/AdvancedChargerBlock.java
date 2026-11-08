package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedChargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;

public class AdvancedChargerBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedChargerBlockEntity> {
    public static final MapCodec<AdvancedChargerBlock> CODEC = simpleCodec(AdvancedChargerBlock::new);

    public AdvancedChargerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ADVANCED_CHARGER_ENTITY,
                AdvancedChargerBlockEntity.class, AdvancedChargerBlockEntity::new, AdvancedChargerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
