package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.ItemConveyorBeltBlockEntity;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static me.jddev0.ep.block.EPBlockStateProperties.ConveyorBeltDirection;

public class ItemConveyorBeltBlock extends BaseEntityBlock implements WrenchConfigurable {
    public static final MapCodec<ItemConveyorBeltBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(
                ExtraCodecs.NON_EMPTY_STRING.xmap(ConveyorBeltTier::valueOf, ConveyorBeltTier::toString).fieldOf("tier").
                        forGetter(ItemConveyorBeltBlock::getTier),
                Properties.CODEC.fieldOf("properties").forGetter(Block::properties)
        ).apply(instance, ItemConveyorBeltBlock::new);
    });

    public static final EnumProperty<ConveyorBeltDirection> FACING = EPBlockStateProperties.CONVEYOR_BELT_FACING;

    protected static final VoxelShape SHAPE_FLAT = Block.box(0., 0., 0., 16., 2., 16.);
    protected static final VoxelShape SHAPE_HALF_BLOCK = Block.box(0., 0., 0., 16., 8., 16.);

    private final ConveyorBeltTier tier;

    protected ItemConveyorBeltBlock(ConveyorBeltTier tier, Properties props) {
        super(props);

        this.tier = tier;

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, ConveyorBeltDirection.NORTH_SOUTH));
    }

    public ConveyorBeltTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new ItemConveyorBeltBlockEntity(blockPos, state, tier);
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
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos blockPos, Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemConveyorBeltBlockEntity itemConveyorBeltBlockEntity))
            return super.getAnalogOutputSignal(state, level, blockPos, direction);

        return itemConveyorBeltBlockEntity.getRedstoneOutput();
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos blockPos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, level, blockPos);
    }

    @Override
    @NotNull
    public InteractionResult onUseWrench(UseOnContext useOnContext, Direction selectedFace, boolean nextPreviousValue) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();

        if(level.isClientSide() || !(level.getBlockEntity(blockPos) instanceof ItemConveyorBeltBlockEntity))
            return InteractionResult.SUCCESS;
        BlockState state = level.getBlockState(blockPos);

        Player player = useOnContext.getPlayer();

        EPBlockStateProperties.ConveyorBeltDirection facing = state.getValue(ItemConveyorBeltBlock.FACING);
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

        level.setBlock(blockPos, state.setValue(ItemConveyorBeltBlock.FACING, EPBlockStateProperties.ConveyorBeltDirection.of(facing.getDirection(), shape)), 3);

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.item_conveyor_belt.wrench_configuration.changed",
                            Component.translatable("tooltip.energizedpower.conveyor_belt_direction.slope." + (shape == null?"flat":(shape?"ascending":"descending"))).
                                    withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
                    ).withStyle(ChatFormatting.GREEN)
            ));
        }

        return InteractionResult.SUCCESS;
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
        return createTickerHelper(type, tier.getItemConveyorBeltBlockEntityFromTier(), ItemConveyorBeltBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final ConveyorBeltTier tier;

        public Item(Block block, Properties props, ConveyorBeltTier tier) {
            super(block, props);

            this.tier = tier;
        }

        public ConveyorBeltTier getTier() {
            return tier;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Minecraft.getInstance().hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.wrench_configurable").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
