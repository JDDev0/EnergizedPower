package me.jddev0.ep.block;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.EliteBatteryBoxBlockEntity;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class EliteBatteryBoxBlock extends HorizontallyOrientableWorkerMachineBlock<EliteBatteryBoxBlockEntity> {
    public EliteBatteryBoxBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ELITE_BATTERY_BOX_ENTITY,
                EliteBatteryBoxBlockEntity.class, EliteBatteryBoxBlockEntity::new, EliteBatteryBoxBlockEntity::tick
        );
    }

    public static class Item extends BlockItem {
        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Minecraft.getInstance().hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.capacity.txt",
                                EnergyUtils.getEnergyWithPrefix(EliteBatteryBoxBlockEntity.CAPACITY)).
                        withStyle(ChatFormatting.GRAY));
                components.accept(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(EliteBatteryBoxBlockEntity.MAX_TRANSFER)).
                        withStyle(ChatFormatting.GRAY));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
