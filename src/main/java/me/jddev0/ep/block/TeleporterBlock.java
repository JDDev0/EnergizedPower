package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.input.ModKeyBindings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class TeleporterBlock extends BlockWithEntity {
    public static final MapCodec<TeleporterBlock> CODEC = createCodec(TeleporterBlock::new);

    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    public TeleporterBlock(AbstractBlock.Settings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(POWERED, false).with(TRIGGERED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new TeleporterBlockEntity(blockPos, state);
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
        if(!(blockEntity instanceof TeleporterBlockEntity teleporterBlockEntity))
            return super.getComparatorOutput(state, level, blockPos, direction);

        return teleporterBlockEntity.getRedstoneOutput();
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView getter, BlockPos blockPos, Direction direction) {
        return state.get(POWERED)?15:0;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld level, BlockPos blockPos, boolean moved) {
        ItemScatterer.onStateReplaced(state, level, blockPos);
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof TeleporterBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((TeleporterBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState selfState, World level, BlockPos selfPos, Block fromBlock, @Nullable WireOrientation wireOrientation, boolean isMoving) {
        super.neighborUpdate(selfState, level, selfPos, fromBlock, wireOrientation, isMoving);

        if(level.isClient())
            return;

        boolean isPowered = level.isReceivingRedstonePower(selfPos);
        if(isPowered != selfState.get(TRIGGERED)) {
            if(isPowered) {
                BlockEntity blockEntity = level.getBlockEntity(selfPos);
                if(!(blockEntity instanceof TeleporterBlockEntity teleporterBlockEntity))
                    throw new IllegalStateException("Container is invalid");

                teleporterBlockEntity.onRedstoneTriggered();
            }

            level.setBlockState(selfPos, selfState.with(TRIGGERED, isPowered), 2);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWERED, TRIGGERED);
    }

    public static class Item extends BlockItem {

        public Item(Block block, Item.Settings props) {
            super(block, props);
        }

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
            if(MinecraftClient.getInstance().isShiftPressed()) {
                tooltip.accept(Text.translatable("tooltip.energizedpower.teleporter.txt.shift.1",
                        Text.keybind(ModKeyBindings.KEY_TELEPORTER_USE)).formatted(Formatting.GRAY, Formatting.ITALIC));
                tooltip.accept(Text.translatable("tooltip.energizedpower.teleporter.txt.shift.2").
                        formatted(Formatting.GRAY, Formatting.ITALIC));
            }else {
                tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }
}
