package me.jddev0.ep.item;

import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.List;
import java.util.function.Consumer;

public class CreativeBatteryItem extends Item {
    public CreativeBatteryItem(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return 13;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 16733695; //ChatFormatting.LIGHT_PURPLE
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag type) {
        tooltip.accept(Component.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(Long.MAX_VALUE), EnergyUtils.getEnergyWithPrefix(Long.MAX_VALUE)).
                withStyle(ChatFormatting.GRAY));

        if(Minecraft.getInstance().hasShiftDown()) {
            tooltip.accept(Component.translatable("tooltip.energizedpower.capacity.txt",
                            Component.translatable("tooltip.energizedpower.infinite.txt").
                                    withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)).
                    withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                            Component.translatable("tooltip.energizedpower.infinite.txt").
                                    withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)).
                    withStyle(ChatFormatting.GRAY));
        }else {
            tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }
}
