package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarPanelBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.d, 0.d, 0.d, 16.d, 4.d, 16.d);

    private final Tier tier;

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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, SolarPanelBlockEntity.getEntityTypeFromTier(tier), SolarPanelBlockEntity::tick);
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
                        EnergyUtils.getEnergyWithPrefix(tier.getFePerTick())).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.energizedpower.solar_panel.txt.shift.2").formatted(Formatting.GRAY));
            }else {
                tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }

    public enum Tier {
        TIER_1("solar_panel_1", 32,
                FabricBlockSettings.of(Material.METAL).
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
        TIER_2("solar_panel_2", 256,
                FabricBlockSettings.of(Material.METAL).
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
        TIER_3("solar_panel_3", 2048,
                FabricBlockSettings.of(Material.METAL).
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
        TIER_4("solar_panel_4", 32768,
                FabricBlockSettings.of(Material.METAL).
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
        TIER_5("solar_panel_5", 262144,
                FabricBlockSettings.of(Material.METAL).
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL));

        private final String resourceId;
        private final int fePerTick;
        private final FabricBlockSettings props;

        Tier(String resourceId, int fePerTick, FabricBlockSettings props) {
            this.resourceId = resourceId;
            this.fePerTick = fePerTick;
            this.props = props;
        }

        public String getResourceId() {
            return resourceId;
        }

        public int getFePerTick() {
            return fePerTick;
        }

        public FabricBlockSettings getProperties() {
            return props;
        }
    }
}
