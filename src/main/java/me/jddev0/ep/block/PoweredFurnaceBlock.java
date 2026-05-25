package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.PoweredFurnaceBlockEntity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.ToIntFunction;

public class PoweredFurnaceBlock extends HorizontallyOrientableWorkerMachineBlock<PoweredFurnaceBlockEntity> {
    public static final MapCodec<PoweredFurnaceBlock> CODEC = simpleCodec(PoweredFurnaceBlock::new);

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(WORKING) ? 5 : 0;

    protected PoweredFurnaceBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.POWERED_FURNACE_ENTITY,
                PoweredFurnaceBlockEntity.class, PoweredFurnaceBlockEntity::new, PoweredFurnaceBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
