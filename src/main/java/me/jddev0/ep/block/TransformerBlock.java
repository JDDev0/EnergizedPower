package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.TransformerBlockEntity;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TransformerBlock extends BlockWithEntity {
    public static final MapCodec<TransformerBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createSettingsCodec(),
                Codecs.NON_EMPTY_STRING.xmap(Tier::valueOf, Tier::toString).fieldOf("tier").
                        forGetter(TransformerBlock::getTier),
                Codecs.NON_EMPTY_STRING.xmap(Type::valueOf, Type::toString).fieldOf("transformer_type").
                        forGetter(TransformerBlock::getTransformerType)
        ).apply(instance, TransformerBlock::new);
    });

    public static final EnumProperty<Direction> FACING = Properties.FACING;

    private final Tier tier;
    private final Type type;

    protected TransformerBlock(AbstractBlock.Settings props, Tier tier, Type type) {
        super(props);

        this.tier = tier;
        this.type = type;

        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    public Tier getTier() {
        return tier;
    }

    public Type getTransformerType() {
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
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite());
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, TransformerBlockEntity.getEntityTypeFromTierAndType(this.tier, this.type), TransformerBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final Tier tier;
        private final Type type;

        public Item(Block block, Item.Settings props, Tier tier, Type type) {
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
        public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
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
