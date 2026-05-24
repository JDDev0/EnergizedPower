package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.machine.tier.SolarPanelTier;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Consumer;

public class SolarPanelBlock extends WorkerMachineBlock<SolarPanelBlockEntity> {
    public static final MapCodec<SolarPanelBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(ExtraCodecs.NON_EMPTY_STRING.xmap(SolarPanelTier::valueOf, SolarPanelTier::toString).fieldOf("tier").
                forGetter(SolarPanelBlock::getTier),
                        Properties.CODEC.fieldOf("properties").forGetter(Block::properties)).
                apply(instance, SolarPanelBlock::new);
    });

    private static final VoxelShape SHAPE = Block.box(0.d, 0.d, 0.d, 16.d, 4.d, 16.d);

    private final SolarPanelTier tier;

    public SolarPanelBlock(SolarPanelTier tier, Properties props) {
        super(
                props,

                tier::getEntityTypeFromTier,
                SolarPanelBlockEntity.class, (blockPos, state) -> new SolarPanelBlockEntity(blockPos, state, tier), SolarPanelBlockEntity::tick
        );

        this.tier = tier;
    }

    public SolarPanelTier getTier() {
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

    public static class Item extends BlockItem {
        private final SolarPanelTier tier;

        public Item(Block block, Item.Properties props, SolarPanelTier tier) {
            super(block, props);

            this.tier = tier;
        }

        public SolarPanelTier getTier() {
            return tier;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Minecraft.getInstance().hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.solar_panel.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(tier.getPeakFePerTick())).withStyle(ChatFormatting.GRAY));
                components.accept(Component.translatable("tooltip.energizedpower.solar_panel.txt.shift.2").withStyle(ChatFormatting.GRAY));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
