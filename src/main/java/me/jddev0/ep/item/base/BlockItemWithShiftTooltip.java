package me.jddev0.ep.item.base;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

public class BlockItemWithShiftTooltip extends BlockItem {
    private final List<Component> shiftTooltip;

    public BlockItemWithShiftTooltip(Block block, Properties properties, Component... shiftTooltip) {
        super(block, properties);

        this.shiftTooltip = List.of(shiftTooltip);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        if(Minecraft.getInstance().hasShiftDown()) {
            for(Component shiftTooltipLine:shiftTooltip)
                components.accept(shiftTooltipLine);
        }else {
            components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }
}
