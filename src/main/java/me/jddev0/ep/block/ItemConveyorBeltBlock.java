package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.ItemConveyorBeltBlockEntity;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static me.jddev0.ep.block.EPBlockStateProperties.ConveyorBeltDirection;

public class ItemConveyorBeltBlock extends BlockWithEntity implements WrenchConfigurable {
    public static final MapCodec<ItemConveyorBeltBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(
                Codecs.NON_EMPTY_STRING.xmap(ConveyorBeltTier::valueOf, ConveyorBeltTier::toString).fieldOf("tier").
                        forGetter(ItemConveyorBeltBlock::getTier),
                Settings.CODEC.fieldOf("properties").forGetter(Block::getSettings)
        ).apply(instance, ItemConveyorBeltBlock::new);
    });

    public static final EnumProperty<ConveyorBeltDirection> FACING = EPBlockStateProperties.CONVEYOR_BELT_FACING;

    protected static final VoxelShape SHAPE_FLAT = Block.createCuboidShape(0., 0., 0., 16., 2., 16.);
    protected static final VoxelShape SHAPE_HALF_BLOCK = Block.createCuboidShape(0., 0., 0., 16., 8., 16.);

    private final ConveyorBeltTier tier;

    protected ItemConveyorBeltBlock(ConveyorBeltTier tier, AbstractBlock.Settings props) {
        super(props);

        this.tier = tier;

        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, ConveyorBeltDirection.NORTH_SOUTH));
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
        return new ItemConveyorBeltBlockEntity(blockPos, state, tier);
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
    protected int getComparatorOutput(BlockState state, World level, BlockPos blockPos, Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltBlockEntity itemConveyorBeltBlockEntity))
            return super.getComparatorOutput(state, level, blockPos, direction);

        return itemConveyorBeltBlockEntity.getRedstoneOutput();
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld level, BlockPos blockPos, boolean moved) {
        ItemScatterer.onStateReplaced(state, level, blockPos);
    }

    @Override
    @NotNull
    public ActionResult onUseWrench(ItemUsageContext useOnContext, Direction selectedFace, boolean nextPreviousValue) {
        World level = useOnContext.getWorld();
        BlockPos blockPos = useOnContext.getBlockPos();

        if(level.isClient() || !(level.getBlockEntity(blockPos) instanceof ItemConveyorBeltBlockEntity))
            return ActionResult.SUCCESS;

        BlockState state = level.getBlockState(blockPos);

        PlayerEntity player = useOnContext.getPlayer();

        EPBlockStateProperties.ConveyorBeltDirection facing = state.get(ItemConveyorBeltBlock.FACING);
        Boolean shape;

        if(nextPreviousValue) {
            if(facing.isAscending())
                shape = null;
            else if(facing.isDescending())
                shape = true;
            else
                shape = false;
        }else {
            if(facing.isAscending())
                shape = false;
            else if(facing.isDescending())
                shape = null;
            else
                shape = true;
        }

        level.setBlockState(blockPos, state.with(ItemConveyorBeltBlock.FACING, EPBlockStateProperties.ConveyorBeltDirection.of(facing.getDirection(), shape)), 3);

        if(player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.item_conveyor_belt.wrench_configuration.changed",
                            Text.translatable("tooltip.energizedpower.conveyor_belt_direction.slope." + (shape == null?"flat":(shape?"ascending":"descending"))).
                                    formatted(Formatting.WHITE, Formatting.BOLD)
                    ).formatted(Formatting.GREEN)
            ));
        }

        return ActionResult.SUCCESS;
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
        return validateTicker(type, tier.getItemConveyorBeltBlockEntityFromTier(), ItemConveyorBeltBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final ConveyorBeltTier tier;

        public Item(Block block, Item.Settings props, ConveyorBeltTier tier) {
            super(block, props);

            this.tier = tier;
        }

        public ConveyorBeltTier getTier() {
            return tier;
        }

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
            if(MinecraftClient.getInstance().isShiftPressed()) {
                tooltip.accept(Text.translatable("tooltip.energizedpower.wrench_configurable").
                        formatted(Formatting.GRAY, Formatting.ITALIC));
            }else {
                tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }
}
