package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedCrusherBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class AdvancedCrusherBlock extends HorizontallyOrientableWorkerMachineBlock<AdvancedCrusherBlockEntity> {
    public AdvancedCrusherBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ADVANCED_CRUSHER_ENTITY,
                AdvancedCrusherBlockEntity.class, AdvancedCrusherBlockEntity::new, AdvancedCrusherBlockEntity::tick
        );
    }

    public static class Item extends BlockItem {
        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Minecraft.getInstance().hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.advanced_crusher.txt.shift.1").withStyle(ChatFormatting.GRAY));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
