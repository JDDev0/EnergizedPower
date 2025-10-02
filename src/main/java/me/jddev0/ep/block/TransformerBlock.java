package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.TransformerBlockEntity;
import me.jddev0.ep.machine.tier.TransformerTier;
import me.jddev0.ep.machine.tier.TransformerType;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TransformerBlock extends BaseEntityBlock {
    public static final MapCodec<TransformerBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(propertiesCodec(),
                ExtraCodecs.NON_EMPTY_STRING.xmap(TransformerTier::valueOf, TransformerTier::toString).fieldOf("tier").
                        forGetter(TransformerBlock::getTier),
                ExtraCodecs.NON_EMPTY_STRING.xmap(TransformerType::valueOf, TransformerType::toString).fieldOf("transformer_type").
                        forGetter(TransformerBlock::getTransformerType)
        ).apply(instance, TransformerBlock::new);
    });

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    private final TransformerTier tier;
    private final TransformerType type;

    protected TransformerBlock(Properties props, TransformerTier tier, TransformerType type) {
        super(props);

        this.tier = tier;
        this.type = type;

        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    public TransformerTier getTier() {
        return tier;
    }

    public TransformerType getTransformerType() {
        return type;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new TransformerBlockEntity(blockPos, state, tier, type);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof TransformerBlockEntity) || ((TransformerBlockEntity)blockEntity).getTier() != tier ||
                ((TransformerBlockEntity)blockEntity).getTransformerType() != type)
            throw new IllegalStateException("Container is invalid");

        player.openMenu((TransformerBlockEntity)blockEntity, blockPos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, orientation, isMoving);

        if(level.isClientSide())
            return;

        boolean isPowered = level.hasNeighborSignal(selfPos);
        if(isPowered != selfState.getValue(POWERED))
            level.setBlock(selfPos, selfState.setValue(POWERED, isPowered), 3);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).
                setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWERED, FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, this.tier.getEntityTypeFromTierAndType(this.type), TransformerBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final TransformerTier tier;
        private final TransformerType type;

        public Item(Block block, Properties props, TransformerTier tier, TransformerType type) {
            super(block, props);

            this.tier = tier;
            this.type = type;
        }

        public TransformerTier getTier() {
            return tier;
        }

        public TransformerType getTransformerType() {
            return type;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Minecraft.getInstance().hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(tier.getMaxEnergyTransferFromTier())).
                        withStyle(ChatFormatting.GRAY));
                components.accept(Component.empty());
                components.accept(Component.translatable("tooltip.energizedpower.transformer.txt.shift.1").withStyle(ChatFormatting.GRAY));
                components.accept(Component.translatable("tooltip.energizedpower.transformer.txt.shift.2").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                components.accept(Component.translatable("tooltip.energizedpower.transformer.txt.shift.3").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
