package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.TransformerBlockEntity;
import me.jddev0.ep.machine.tier.TransformerTier;
import me.jddev0.ep.machine.tier.TransformerType;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigurableTransformerBlock extends BlockWithEntity implements Waterloggable, WrenchConfigurable {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final EnumProperty<EPBlockStateProperties.TransformerConnection> UP = EPBlockStateProperties.TRANSFORMER_CONNECTION_UP;
    public static final EnumProperty<EPBlockStateProperties.TransformerConnection> DOWN = EPBlockStateProperties.TRANSFORMER_CONNECTION_DOWN;
    public static final EnumProperty<EPBlockStateProperties.TransformerConnection> NORTH = EPBlockStateProperties.TRANSFORMER_CONNECTION_NORTH;
    public static final EnumProperty<EPBlockStateProperties.TransformerConnection> SOUTH = EPBlockStateProperties.TRANSFORMER_CONNECTION_SOUTH;
    public static final EnumProperty<EPBlockStateProperties.TransformerConnection> EAST = EPBlockStateProperties.TRANSFORMER_CONNECTION_EAST;
    public static final EnumProperty<EPBlockStateProperties.TransformerConnection> WEST = EPBlockStateProperties.TRANSFORMER_CONNECTION_WEST;

    @NotNull
    public static EnumProperty<EPBlockStateProperties.TransformerConnection> getTransformerConnectionPropertyFromDirection(@NotNull Direction dir) {
        return switch(dir) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
        };
    }

    private final TransformerTier tier;

    public ConfigurableTransformerBlock(TransformerTier tier, AbstractBlock.Settings properties) {
        super(properties);

        this.tier = tier;

        this.setDefaultState(this.getStateManager().getDefaultState().with(POWERED, false).
                with(UP, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                with(DOWN, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                with(NORTH, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                with(SOUTH, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                with(EAST, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                with(WEST, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED));
    }

    public TransformerTier getTier() {
        return tier;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new TransformerBlockEntity(blockPos, state, tier, TransformerType.CONFIGURABLE);
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
                ((TransformerBlockEntity)blockEntity).getTransformerType() != TransformerType.CONFIGURABLE)
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((TransformerBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    @NotNull
    public ActionResult onUseWrench(ItemUsageContext useOnContext, Direction selectedFace, boolean nextPreviousValue) {
        World level = useOnContext.getWorld();
        BlockPos blockPos = useOnContext.getBlockPos();

        if(level.isClient() || !(level.getBlockEntity(blockPos) instanceof TransformerBlockEntity))
            return ActionResult.SUCCESS;

        BlockState state = level.getBlockState(blockPos);

        PlayerEntity player = useOnContext.getPlayer();

        EnumProperty<EPBlockStateProperties.TransformerConnection> transformerConnectionProperty =
                ConfigurableTransformerBlock.getTransformerConnectionPropertyFromDirection(selectedFace);

        int diff = nextPreviousValue?-1:1;

        EPBlockStateProperties.TransformerConnection transformerConnection = state.get(transformerConnectionProperty);
        transformerConnection = EPBlockStateProperties.TransformerConnection.values()[(transformerConnection.ordinal() + diff +
                EPBlockStateProperties.TransformerConnection.values().length) %
                EPBlockStateProperties.TransformerConnection.values().length];

        level.setBlockState(blockPos, state.with(transformerConnectionProperty, transformerConnection), 3);

        if(player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.configurable_transformer.wrench_configuration.face_changed",
                            Text.translatable("tooltip.energizedpower.direction." + selectedFace.asString()).
                                    formatted(Formatting.WHITE),
                            Text.translatable(transformerConnection.getTranslationKey()).
                                    formatted(Formatting.WHITE, Formatting.BOLD)
                    ).formatted(Formatting.GREEN)
            ));
        }

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

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().
                with(POWERED, context.getWorld().isReceivingRedstonePower(context.getBlockPos())).
                with(UP, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                with(DOWN, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                with(NORTH, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                with(SOUTH, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                with(EAST, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED).
                with(WEST, EPBlockStateProperties.TransformerConnection.NOT_CONNECTED);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch(rotation) {
            case CLOCKWISE_90:
                return state.
                        with(NORTH, state.get(WEST)).
                        with(SOUTH, state.get(EAST)).
                        with(EAST, state.get(NORTH)).
                        with(WEST, state.get(SOUTH));
            case CLOCKWISE_180:
                return state.
                        with(NORTH, state.get(SOUTH)).
                        with(SOUTH, state.get(NORTH)).
                        with(EAST, state.get(WEST)).
                        with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90:
                return state.
                        with(NORTH, state.get(EAST)).
                        with(SOUTH, state.get(WEST)).
                        with(EAST, state.get(SOUTH)).
                        with(WEST, state.get(NORTH));
            default:
                return state;
        }
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch(mirror) {
            case LEFT_RIGHT:
                return state.
                        with(NORTH, state.get(SOUTH)).
                        with(SOUTH, state.get(NORTH));
            case FRONT_BACK:
                return state.
                        with(EAST, state.get(WEST)).
                        with(WEST, state.get(EAST));
            default:
                return state;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWERED).add(UP).add(DOWN).add(NORTH).add(SOUTH).add(EAST).add(WEST);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, tier.getEntityTypeFromTierAndType(TransformerType.CONFIGURABLE), TransformerBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final TransformerTier tier;

        public Item(Block block, Settings props, TransformerTier tier) {
            super(block, props);

            this.tier = tier;
        }

        public TransformerTier getTier() {
            return tier;
        }

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.energizedpower.wrench_configurable").
                        formatted(Formatting.GRAY, Formatting.ITALIC));

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
