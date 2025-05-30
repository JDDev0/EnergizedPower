package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SolarPanelBlock extends BaseEntityBlock {
    public static final MapCodec<SolarPanelBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(ExtraCodecs.NON_EMPTY_STRING.xmap(Tier::valueOf, Tier::toString).fieldOf("tier").
                forGetter(SolarPanelBlock::getTier),
                        Properties.CODEC.fieldOf("properties").forGetter(Block::properties)).
                apply(instance, SolarPanelBlock::new);
    });

    private static final VoxelShape SHAPE = Block.box(0.d, 0.d, 0.d, 16.d, 4.d, 16.d);

    private final Tier tier;

    public static Block getBlockFromTier(SolarPanelBlock.Tier tier) {
        return switch(tier) {
            case TIER_1 -> EPBlocks.SOLAR_PANEL_1.get();
            case TIER_2 -> EPBlocks.SOLAR_PANEL_2.get();
            case TIER_3 -> EPBlocks.SOLAR_PANEL_3.get();
            case TIER_4 -> EPBlocks.SOLAR_PANEL_4.get();
            case TIER_5 -> EPBlocks.SOLAR_PANEL_5.get();
            case TIER_6 -> EPBlocks.SOLAR_PANEL_6.get();
        };
    }

    public SolarPanelBlock(Tier tier, Properties properties) {
        super(properties);

        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new SolarPanelBlockEntity(blockPos, state, tier);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos blockPos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, level, blockPos);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof SolarPanelBlockEntity) || ((SolarPanelBlockEntity)blockEntity).getTier() != tier)
            throw new IllegalStateException("Container is invalid");

        player.openMenu((SolarPanelBlockEntity)blockEntity, blockPos);

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, SolarPanelBlockEntity.getEntityTypeFromTier(tier), SolarPanelBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final Tier tier;

        public Item(Block block, Item.Properties props, Tier tier) {
            super(block, props);

            this.tier = tier;
        }

        public Tier getTier() {
            return tier;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Screen.hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.solar_panel.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(tier.getPeakFePerTick())).withStyle(ChatFormatting.GRAY));
                components.accept(Component.translatable("tooltip.energizedpower.solar_panel.txt.shift.2").withStyle(ChatFormatting.GRAY));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    public enum Tier {
        TIER_1("solar_panel_1", ModConfigs.COMMON_SOLAR_PANEL_1_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_1_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_1_CAPACITY.getValue(),
                BlockBehaviour.Properties.of().
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        TIER_2("solar_panel_2", ModConfigs.COMMON_SOLAR_PANEL_2_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_2_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_2_CAPACITY.getValue(),
                BlockBehaviour.Properties.of().
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        TIER_3("solar_panel_3", ModConfigs.COMMON_SOLAR_PANEL_3_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_3_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_3_CAPACITY.getValue(),
                BlockBehaviour.Properties.of().
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        TIER_4("solar_panel_4", ModConfigs.COMMON_SOLAR_PANEL_4_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_4_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_4_CAPACITY.getValue(),
                BlockBehaviour.Properties.of().
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        TIER_5("solar_panel_5", ModConfigs.COMMON_SOLAR_PANEL_5_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_5_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_5_CAPACITY.getValue(),
                BlockBehaviour.Properties.of().
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        TIER_6("solar_panel_6", ModConfigs.COMMON_SOLAR_PANEL_6_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_6_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_6_CAPACITY.getValue(),
                BlockBehaviour.Properties.of().
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL));

        private final String resourceId;
        private final int peakFePerTick;
        private final int maxTransfer;
        private final int capacity;
        private final Properties props;

        Tier(String resourceId, int peakFePerTick, int maxTransfer, int capacity, Properties props) {
            this.resourceId = resourceId;
            this.peakFePerTick = peakFePerTick;
            this.maxTransfer = maxTransfer;
            this.capacity = capacity;
            this.props = props;
        }

        public String getResourceId() {
            return resourceId;
        }

        public int getPeakFePerTick() {
            return peakFePerTick;
        }

        public int getMaxTransfer() {
            return maxTransfer;
        }

        public int getCapacity() {
            return capacity;
        }

        public Properties getProperties() {
            return props;
        }
    }
}
