package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.ItemConveyorBeltBlockEntity;
import me.jddev0.ep.block.entity.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.jddev0.ep.block.ModBlockStateProperties.ConveyorBeltDirection;

public class ItemConveyorBeltBlock extends BaseEntityBlock {
    public static final EnumProperty<ConveyorBeltDirection> FACING = ModBlockStateProperties.CONVEYOR_BELT_FACING;

    protected static final VoxelShape SHAPE_FLAT = Block.box(0., 0., 0., 16., 2., 16.);
    protected static final VoxelShape SHAPE_HALF_BLOCK = Block.box(0., 0., 0., 16., 8., 16.);

    protected ItemConveyorBeltBlock(Properties props) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, ConveyorBeltDirection.NORTH_SOUTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new ItemConveyorBeltBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltBlockEntity itemConveyorBeltBlockEntity))
            return super.getAnalogOutputSignal(state, level, blockPos);

        return itemConveyorBeltBlockEntity.getRedstoneOutput();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltBlockEntity))
            return;

        ((ItemConveyorBeltBlockEntity)blockEntity).drops(level, blockPos);

        super.onRemove(state, level, blockPos, newState, isMoving);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, ConveyorBeltDirection.of(context.getHorizontalDirection(), null));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        ConveyorBeltDirection facing = state.getValue(FACING);
        Boolean slope = facing.getSlope();

        return state.setValue(FACING, ConveyorBeltDirection.of(rotation.rotate(facing.getDirection()), slope));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING).getDirection()));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        ConveyorBeltDirection facing = blockState.getValue(FACING);
        if(facing.isAscending() || facing.isDescending())
            return SHAPE_HALF_BLOCK;

        return SHAPE_FLAT;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntity::tick);
    }

    public static class Item extends BlockItem {

        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.energizedpower.wrench_configurable").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
