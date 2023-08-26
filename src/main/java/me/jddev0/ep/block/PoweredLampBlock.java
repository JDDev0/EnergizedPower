package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.block.entity.PoweredLampBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class PoweredLampBlock extends BaseEntityBlock {
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(LEVEL);

    protected PoweredLampBlock(Properties props) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new PoweredLampBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(LEVEL);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.POWERED_LAMP_ENTITY.get(), PoweredLampBlockEntity::tick);
    }
}
