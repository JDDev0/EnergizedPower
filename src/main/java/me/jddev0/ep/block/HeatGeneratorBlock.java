package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.HeatGeneratorBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class HeatGeneratorBlock extends BaseEntityBlock {
    public static final MapCodec<HeatGeneratorBlock> CODEC = simpleCodec(HeatGeneratorBlock::new);

    public HeatGeneratorBlock(Properties props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new HeatGeneratorBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos blockPos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, level, blockPos);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof HeatGeneratorBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openMenu((HeatGeneratorBlockEntity)blockEntity, blockPos);

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EPBlockEntities.HEAT_GENERATOR_ENTITY.get(), HeatGeneratorBlockEntity::tick);
    }
}
