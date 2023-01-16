package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarPanelBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(0.d, 0.d, 0.d, 16.d, 4.d, 16.d);

    private final Tier tier;

    public SolarPanelBlock(Tier tier) {
        super(tier.getProperties());

        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
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
        public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
            if(Screen.hasShiftDown()) {
                components.add(new TranslatableComponent("tooltip.energizedpower.solar_panel.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(tier.getFePerTick())).withStyle(ChatFormatting.GRAY));
                components.add(new TranslatableComponent("tooltip.energizedpower.solar_panel.txt.shift.2").withStyle(ChatFormatting.GRAY));
            }else {
                components.add(new TranslatableComponent("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    public enum Tier {
        TIER_1("solar_panel_1", 64,
                BlockBehaviour.Properties.of(Material.METAL).
                requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        TIER_2("solar_panel_2", 512,
                BlockBehaviour.Properties.of(Material.METAL).
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        TIER_3("solar_panel_3", 4096,
                BlockBehaviour.Properties.of(Material.METAL).
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        TIER_4("solar_panel_4", 32768,
                BlockBehaviour.Properties.of(Material.METAL).
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        TIER_5("solar_panel_5", 262144,
                BlockBehaviour.Properties.of(Material.METAL).
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL));

        private final String resourceId;
        private final int fePerTick;
        private final Properties props;

        Tier(String resourceId, int fePerTick, Properties props) {
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

        public Properties getProperties() {
            return props;
        }
    }
}
