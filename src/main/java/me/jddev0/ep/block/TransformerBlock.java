package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.TransformerBlockEntity;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TransformerBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final DirectionProperty FACING = Properties.FACING;

    private final Tier tier;
    private final Type type;

    public static Block getBlockFromTierAndType(TransformerBlock.Tier tier, TransformerBlock.Type type) {
        return switch(tier) {
            case TIER_LV -> switch(type) {
                case TYPE_1_TO_N -> EPBlocks.LV_TRANSFORMER_1_TO_N;
                case TYPE_3_TO_3 -> EPBlocks.LV_TRANSFORMER_3_TO_3;
                case TYPE_N_TO_1 -> EPBlocks.LV_TRANSFORMER_N_TO_1;
            };
            case TIER_MV -> switch(type) {
                case TYPE_1_TO_N -> EPBlocks.MV_TRANSFORMER_1_TO_N;
                case TYPE_3_TO_3 -> EPBlocks.MV_TRANSFORMER_3_TO_3;
                case TYPE_N_TO_1 -> EPBlocks.MV_TRANSFORMER_N_TO_1;
            };
            case TIER_HV -> switch(type) {
                case TYPE_1_TO_N -> EPBlocks.HV_TRANSFORMER_1_TO_N;
                case TYPE_3_TO_3 -> EPBlocks.HV_TRANSFORMER_3_TO_3;
                case TYPE_N_TO_1 -> EPBlocks.HV_TRANSFORMER_N_TO_1;
            };
            case TIER_EHV -> switch(type) {
                case TYPE_1_TO_N -> EPBlocks.EHV_TRANSFORMER_1_TO_N;
                case TYPE_3_TO_3 -> EPBlocks.EHV_TRANSFORMER_3_TO_3;
                case TYPE_N_TO_1 -> EPBlocks.EHV_TRANSFORMER_N_TO_1;
            };
        };
    }

    protected TransformerBlock(FabricBlockSettings props, Tier tier, Type type) {
        super(props);

        this.tier = tier;
        this.type = type;

        this.setDefaultState(this.getStateManager().getDefaultState().with(POWERED, false).with(FACING, Direction.NORTH));
    }

    public Tier getTier() {
        return tier;
    }

    public Type getTransformerType() {
        return type;
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
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, Hand handItem, BlockHitResult hit) {
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
    public void neighborUpdate(BlockState selfState, World level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborUpdate(selfState, level, selfPos, fromBlock, fromPos, isMoving);

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
        return checkType(type, TransformerBlockEntity.getEntityTypeFromTierAndType(this.tier, this.type), TransformerBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final Tier tier;
        private final Type type;

        public Item(Block block, FabricItemSettings props, Tier tier, Type type) {
            super(block, props);

            this.tier = tier;
            this.type = type;
        }

        public Tier getTier() {
            return tier;
        }

        public Type getTransformerType() {
            return type;
        }

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(TransformerBlockEntity.getMaxEnergyTransferFromTier(tier))).
                        formatted(Formatting.GRAY));
                tooltip.add(Text.empty());
                tooltip.add(Text.translatable("tooltip.energizedpower.transformer.txt.shift.1").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.energizedpower.transformer.txt.shift.2").
                        formatted(Formatting.GRAY, Formatting.ITALIC));
                tooltip.add(Text.translatable("tooltip.energizedpower.transformer.txt.shift.3").
                        formatted(Formatting.GRAY, Formatting.ITALIC));
            }else {
                tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }

    public enum Tier {
        TIER_LV, TIER_MV, TIER_HV, TIER_EHV
    }

    public enum Type {
        TYPE_1_TO_N, TYPE_3_TO_3, TYPE_N_TO_1
    }
}
