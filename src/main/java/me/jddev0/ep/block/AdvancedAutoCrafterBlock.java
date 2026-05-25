package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.function.Consumer;

public class AdvancedAutoCrafterBlock extends WorkerMachineBlock<AdvancedAutoCrafterBlockEntity> {
    public static final MapCodec<AdvancedAutoCrafterBlock> CODEC = simpleCodec(AdvancedAutoCrafterBlock::new);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public AdvancedAutoCrafterBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ADVANCED_AUTO_CRAFTER_ENTITY,
                AdvancedAutoCrafterBlockEntity.class, AdvancedAutoCrafterBlockEntity::new, AdvancedAutoCrafterBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public static class Item extends BlockItem {
        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag type) {
            if(Minecraft.getInstance().hasShiftDown()) {
                tooltip.accept(Component.translatable("tooltip.energizedpower.auto_crafter.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(AdvancedAutoCrafterBlockEntity.ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT)).withStyle(ChatFormatting.GRAY));
            }else {
                tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
