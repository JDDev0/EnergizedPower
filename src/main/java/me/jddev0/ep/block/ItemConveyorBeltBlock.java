package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.ItemConveyorBeltBlockEntity;
import me.jddev0.ep.block.entity.ModBlockEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.jddev0.ep.block.ModBlockStateProperties.ConveyorBeltDirection;

public class ItemConveyorBeltBlock extends BlockWithEntity {
    public static final EnumProperty<ConveyorBeltDirection> FACING = ModBlockStateProperties.CONVEYOR_BELT_FACING;

    protected static final VoxelShape SHAPE_FLAT = Block.createCuboidShape(0., 0., 0., 16., 2., 16.);
    protected static final VoxelShape SHAPE_HALF_BLOCK = Block.createCuboidShape(0., 0., 0., 16., 8., 16.);

    protected ItemConveyorBeltBlock(FabricBlockSettings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, ConveyorBeltDirection.NORTH_SOUTH));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new ItemConveyorBeltBlockEntity(blockPos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltBlockEntity itemConveyorBeltBlockEntity))
            return super.getComparatorOutput(state, level, blockPos);

        return itemConveyorBeltBlockEntity.getRedstoneOutput();
    }

    @Override
    public void onStateReplaced(BlockState state, World level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltBlockEntity))
            return;

        ((ItemConveyorBeltBlockEntity)blockEntity).drops(level, blockPos);

        super.onStateReplaced(state, level, blockPos, newState, isMoving);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, ConveyorBeltDirection.of(context.getHorizontalPlayerFacing(), null));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        ConveyorBeltDirection facing = state.get(FACING);
        Boolean slope = facing.getSlope();

        return state.with(FACING, ConveyorBeltDirection.of(rotation.rotate(facing.getDirection()), slope));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING).getDirection()));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockGetter, BlockPos blockPos, ShapeContext collisionContext) {
        ConveyorBeltDirection facing = blockState.get(FACING);
        if(facing.isAscending() || facing.isDescending())
            return SHAPE_HALF_BLOCK;

        return SHAPE_FLAT;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        public Item(Block block, FabricItemSettings props) {
            super(block, props);
        }

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.energizedpower.wrench_configurable").
                        formatted(Formatting.GRAY, Formatting.ITALIC));
            }else {
                tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }
}
