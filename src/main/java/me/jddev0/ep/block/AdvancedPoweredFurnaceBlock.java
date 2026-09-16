package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.AdvancedPoweredFurnaceBlockEntity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.ToIntFunction;

public class AdvancedPoweredFurnaceBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedPoweredFurnaceBlockEntity> {
    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(WORKING) ? 5 : 0;

    protected AdvancedPoweredFurnaceBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ADVANCED_POWERED_FURNACE_ENTITY,
                AdvancedPoweredFurnaceBlockEntity.class, AdvancedPoweredFurnaceBlockEntity::new, AdvancedPoweredFurnaceBlockEntity::tick
        );
    }
}
