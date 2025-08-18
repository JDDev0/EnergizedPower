package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.ItemConveyorBeltSplitterBlockEntity;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltSplitterBlock extends BlockWithEntity {
    public static final MapCodec<ItemConveyorBeltSplitterBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(
                Codecs.NON_EMPTY_STRING.xmap(ConveyorBeltTier::valueOf, ConveyorBeltTier::toString).fieldOf("tier").
                        forGetter(ItemConveyorBeltSplitterBlock::getTier),
                Settings.CODEC.fieldOf("properties").forGetter(Block::getSettings)
        ).apply(instance, ItemConveyorBeltSplitterBlock::new);
    });

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private final ConveyorBeltTier tier;

    protected ItemConveyorBeltSplitterBlock(ConveyorBeltTier tier, AbstractBlock.Settings props) {
        super(props);

        this.tier = tier;

        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    public ConveyorBeltTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new ItemConveyorBeltSplitterBlockEntity(blockPos, state, tier);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, tier.getItemConveyorBeltSplitterBlockEntityFromTier(), ItemConveyorBeltSplitterBlockEntity::tick);
    }
}
