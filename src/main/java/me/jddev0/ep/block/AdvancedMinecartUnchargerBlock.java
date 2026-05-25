package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.FullyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedMinecartUnchargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.*;

import java.util.List;

public class AdvancedMinecartUnchargerBlock extends FullyOrientableWorkerMachineBlock<AdvancedMinecartUnchargerBlockEntity> {
    public static final MapCodec<AdvancedMinecartUnchargerBlock> CODEC = simpleCodec(AdvancedMinecartUnchargerBlock::new);

    protected AdvancedMinecartUnchargerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ADVANCED_MINECART_UNCHARGER_ENTITY,
                AdvancedMinecartUnchargerBlockEntity.class, AdvancedMinecartUnchargerBlockEntity::new, AdvancedMinecartUnchargerBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public static final class Item extends BlockItem {
        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(AdvancedMinecartUnchargerBlockEntity.MAX_TRANSFER)).
                        withStyle(ChatFormatting.GRAY));
            }else {
                tooltip.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
