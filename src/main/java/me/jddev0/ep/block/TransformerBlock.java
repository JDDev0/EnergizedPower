package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.TransformerBlockEntity;
import me.jddev0.ep.machine.tier.TransformerTier;
import me.jddev0.ep.machine.tier.TransformerType;
import me.jddev0.ep.util.EnergyUtils;
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
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TransformerBlock extends BlockWithEntity {
    public static final MapCodec<TransformerBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createSettingsCodec(),
                Codecs.NON_EMPTY_STRING.xmap(TransformerTier::valueOf, TransformerTier::toString).fieldOf("tier").
                        forGetter(TransformerBlock::getTier),
                Codecs.NON_EMPTY_STRING.xmap(TransformerType::valueOf, TransformerType::toString).fieldOf("transformer_type").
                        forGetter(TransformerBlock::getTransformerType)
        ).apply(instance, TransformerBlock::new);
    });

    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final EnumProperty<Direction> FACING = Properties.FACING;

    private final TransformerTier tier;
    private final TransformerType type;

    protected TransformerBlock(AbstractBlock.Settings props, TransformerTier tier, TransformerType type) {
        super(props);

        this.tier = tier;
        this.type = type;

        this.setDefaultState(this.getStateManager().getDefaultState().with(POWERED, false).with(FACING, Direction.NORTH));
    }

    public TransformerTier getTier() {
        return tier;
    }

    public TransformerType getTransformerType() {
        return type;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new TransformerBlockEntity(blockPos, state, tier, type);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof TransformerBlockEntity) || ((TransformerBlockEntity)blockEntity).getTier() != tier ||
                ((TransformerBlockEntity)blockEntity).getTransformerType() != type)
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((TransformerBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState selfState, World level, BlockPos selfPos, Block fromBlock, @Nullable WireOrientation wireOrientation, boolean isMoving) {
        super.neighborUpdate(selfState, level, selfPos, fromBlock, wireOrientation, isMoving);

        if(level.isClient())
            return;

        boolean isPowered = level.isReceivingRedstonePower(selfPos);
        if(isPowered != selfState.get(POWERED))
            level.setBlockState(selfPos, selfState.with(POWERED, isPowered), 3);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite()).
                with(POWERED, context.getWorld().isReceivingRedstonePower(context.getBlockPos()));
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
        stateBuilder.add(POWERED, FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, this.tier.getEntityTypeFromTierAndType(this.type), TransformerBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final TransformerTier tier;
        private final TransformerType type;

        public Item(Block block, Item.Settings props, TransformerTier tier, TransformerType type) {
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
        public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
            if(MinecraftClient.getInstance().isShiftPressed()) {
                tooltip.accept(Text.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(tier.getMaxEnergyTransferFromTier())).
                        formatted(Formatting.GRAY));
                tooltip.accept(Text.empty());
                tooltip.accept(Text.translatable("tooltip.energizedpower.transformer.txt.shift.1").formatted(Formatting.GRAY));
                tooltip.accept(Text.translatable("tooltip.energizedpower.transformer.txt.shift.2").
                        formatted(Formatting.GRAY, Formatting.ITALIC));
                tooltip.accept(Text.translatable("tooltip.energizedpower.transformer.txt.shift.3").
                        formatted(Formatting.GRAY, Formatting.ITALIC));
            }else {
                tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }

}
