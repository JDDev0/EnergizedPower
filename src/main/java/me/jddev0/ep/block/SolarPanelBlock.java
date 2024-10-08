package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarPanelBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.d, 0.d, 0.d, 16.d, 4.d, 16.d);

    private final Tier tier;

    public static Block getBlockFromTier(SolarPanelBlock.Tier tier) {
        return switch(tier) {
            case TIER_1 -> EPBlocks.SOLAR_PANEL_1;
            case TIER_2 -> EPBlocks.SOLAR_PANEL_2;
            case TIER_3 -> EPBlocks.SOLAR_PANEL_3;
            case TIER_4 -> EPBlocks.SOLAR_PANEL_4;
            case TIER_5 -> EPBlocks.SOLAR_PANEL_5;
            case TIER_6 -> EPBlocks.SOLAR_PANEL_6;
        };
    }

    public SolarPanelBlock(Tier tier) {
        super(tier.getProperties());

        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockGetter, BlockPos blockPos, ShapeContext collisionContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new SolarPanelBlockEntity(blockPos, state, tier);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof SolarPanelBlockEntity))
            return;

        ((SolarPanelBlockEntity)blockEntity).drops(level, blockPos);

        super.onStateReplaced(state, level, blockPos, newState, isMoving);
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, Hand handItem, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof SolarPanelBlockEntity) || ((SolarPanelBlockEntity)blockEntity).getTier() != tier)
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((SolarPanelBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, SolarPanelBlockEntity.getEntityTypeFromTier(tier), SolarPanelBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final Tier tier;

        public Item(Block block, FabricItemSettings props, Tier tier) {
            super(block, props);

            this.tier = tier;
        }

        public Tier getTier() {
            return tier;
        }

        @Override
        public void appendTooltip(ItemStack itemStack, @Nullable World level, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.energizedpower.solar_panel.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(tier.getPeakFePerTick())).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.energizedpower.solar_panel.txt.shift.2").formatted(Formatting.GRAY));
            }else {
                tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }

    public enum Tier {
        TIER_1("solar_panel_1", ModConfigs.COMMON_SOLAR_PANEL_1_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_1_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_1_CAPACITY.getValue(),
                FabricBlockSettings.create().
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
        TIER_2("solar_panel_2", ModConfigs.COMMON_SOLAR_PANEL_2_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_2_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_2_CAPACITY.getValue(),
                FabricBlockSettings.create().
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
        TIER_3("solar_panel_3", ModConfigs.COMMON_SOLAR_PANEL_3_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_3_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_3_CAPACITY.getValue(),
                FabricBlockSettings.create().
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
        TIER_4("solar_panel_4", ModConfigs.COMMON_SOLAR_PANEL_4_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_4_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_4_CAPACITY.getValue(),
                FabricBlockSettings.create().
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
        TIER_5("solar_panel_5", ModConfigs.COMMON_SOLAR_PANEL_5_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_5_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_5_CAPACITY.getValue(),
                FabricBlockSettings.create().
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
        TIER_6("solar_panel_6", ModConfigs.COMMON_SOLAR_PANEL_6_ENERGY_PEAK_PRODUCTION.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_6_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SOLAR_PANEL_6_CAPACITY.getValue(),
                FabricBlockSettings.create().
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL));

        private final String resourceId;
        private final long peakFePerTick;
        private final long maxTransfer;
        private final long capacity;
        private final FabricBlockSettings props;

        Tier(String resourceId, long peakFePerTick, long maxTransfer, long capacity, FabricBlockSettings props) {
            this.resourceId = resourceId;
            this.peakFePerTick = peakFePerTick;
            this.maxTransfer = maxTransfer;
            this.capacity = capacity;
            this.props = props;
        }

        public String getResourceId() {
            return resourceId;
        }

        public long getPeakFePerTick() {
            return peakFePerTick;
        }

        public long getMaxTransfer() {
            return maxTransfer;
        }

        public long getCapacity() {
            return capacity;
        }

        public FabricBlockSettings getProperties() {
            return props;
        }
    }
}
