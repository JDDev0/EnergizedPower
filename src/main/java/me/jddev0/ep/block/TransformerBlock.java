package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.TransformerBlockEntity;
import me.jddev0.ep.machine.tier.TransformerTier;
import me.jddev0.ep.machine.tier.TransformerType;
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

    private final TransformerTier tier;
    private final TransformerType type;

    protected TransformerBlock(FabricBlockSettings props, TransformerTier tier, TransformerType type) {
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
        return checkType(type, this.tier.getEntityTypeFromTierAndType(this.type), TransformerBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final TransformerTier tier;
        private final TransformerType type;

        public Item(Block block, FabricItemSettings props, TransformerTier tier, TransformerType type) {
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
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(tier.getMaxEnergyTransferFromTier())).
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

}
