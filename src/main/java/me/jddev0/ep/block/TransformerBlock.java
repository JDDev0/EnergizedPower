package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.TransformerBlockEntity;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TransformerBlock extends BaseEntityBlock {
    public static final MapCodec<TransformerBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(propertiesCodec(),
                ExtraCodecs.NON_EMPTY_STRING.xmap(Tier::valueOf, Tier::toString).fieldOf("tier").
                        forGetter(TransformerBlock::getTier),
                ExtraCodecs.NON_EMPTY_STRING.xmap(Type::valueOf, Type::toString).fieldOf("transformer_type").
                        forGetter(TransformerBlock::getTransformerType)
        ).apply(instance, TransformerBlock::new);
    });

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private final Tier tier;
    private final Type type;

    protected TransformerBlock(Properties props, Tier tier, Type type) {
        super(props);

        this.tier = tier;
        this.type = type;

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public Tier getTier() {
        return tier;
    }

    public Type getTransformerType() {
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
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
        stateBuilder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, TransformerBlockEntity.getEntityTypeFromTierAndType(this.tier, this.type), TransformerBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final Tier tier;
        private final Type type;

        public Item(Block block, Properties props, Tier tier, Type type) {
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
        public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(TransformerBlockEntity.getMaxEnergyTransferFromTier(tier))).
                        withStyle(ChatFormatting.GRAY));
                components.add(Component.empty());
                components.add(Component.translatable("tooltip.energizedpower.transformer.txt.shift.1").withStyle(ChatFormatting.GRAY));
                components.add(Component.translatable("tooltip.energizedpower.transformer.txt.shift.2").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                components.add(Component.translatable("tooltip.energizedpower.transformer.txt.shift.3").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
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
