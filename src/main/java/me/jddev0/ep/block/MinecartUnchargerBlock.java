package me.jddev0.ep.block;

import me.jddev0.ep.block.base.FullyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.MinecartUnchargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.*;

import java.util.function.Consumer;

public class MinecartUnchargerBlock extends FullyOrientableWorkerMachineBlock<MinecartUnchargerBlockEntity> {
    protected MinecartUnchargerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.MINECART_UNCHARGER_ENTITY,
                MinecartUnchargerBlockEntity.class, MinecartUnchargerBlockEntity::new, MinecartUnchargerBlockEntity::tick
        );
    }

    public static final class Item extends BlockItem {
        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Minecraft.getInstance().hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(MinecartUnchargerBlockEntity.MAX_TRANSFER)).
                        withStyle(ChatFormatting.GRAY));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
