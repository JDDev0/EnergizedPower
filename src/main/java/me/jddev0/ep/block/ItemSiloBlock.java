package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.ItemSiloBlockEntity;
import me.jddev0.ep.machine.tier.ItemSiloTier;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.StateManager;
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
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemSiloBlock extends BlockWithEntity {
    public static final MapCodec<ItemSiloBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codecs.NON_EMPTY_STRING.xmap(ItemSiloTier::valueOf, ItemSiloTier::toString).fieldOf("tier").
                forGetter(ItemSiloBlock::getTier),
                        AbstractBlock.Settings.CODEC.fieldOf("properties").forGetter(AbstractBlock::getSettings)).
                apply(instance, ItemSiloBlock::new);
    });

    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    private final ItemSiloTier tier;

    public ItemSiloBlock(ItemSiloTier tier, AbstractBlock.Settings properties) {
        super(properties);

        this.tier = tier;

        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    public ItemSiloTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new ItemSiloBlockEntity(blockPos, state, tier);
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
        if(!(blockEntity instanceof ItemSiloBlockEntity itemSiloBlockEntity))
            return super.getComparatorOutput(state, level, blockPos, direction);

        return itemSiloBlockEntity.getRedstoneOutput();
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ItemSiloBlockEntity) || ((ItemSiloBlockEntity)blockEntity).getTier() != tier)
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((ItemSiloBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
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
        stateBuilder.add(FACING);
    }

    public static class Item extends BlockItem {
        private final ItemSiloTier tier;

        public Item(Block block, Settings props, ItemSiloTier tier) {
            super(block, props);

            this.tier = tier;
        }

        public ItemSiloTier getTier() {
            return tier;
        }

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
            if(MinecraftClient.getInstance().isShiftPressed()) {
                tooltip.accept(Text.translatable("tooltip.energizedpower.item_silo_capacity.txt", tier.getItemSiloCapacity()).formatted(Formatting.GRAY));

                tooltip.accept(Text.translatable("tooltip.energizedpower.item_silo.txt.shift").formatted(Formatting.GRAY));
            }else {
                tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }
}
