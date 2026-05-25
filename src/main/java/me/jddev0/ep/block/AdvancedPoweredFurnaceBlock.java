package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.AdvancedPoweredFurnaceBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.world.level.block.*;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.ToIntFunction;

public class AdvancedPoweredFurnaceBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedPoweredFurnaceBlockEntity> {
    public static final MapCodec<AdvancedPoweredFurnaceBlock> CODEC = simpleCodec(AdvancedPoweredFurnaceBlock::new);

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(WORKING) ? 5 : 0;

    protected AdvancedPoweredFurnaceBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ADVANCED_POWERED_FURNACE_ENTITY,
                AdvancedPoweredFurnaceBlockEntity.class, AdvancedPoweredFurnaceBlockEntity::new, AdvancedPoweredFurnaceBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
