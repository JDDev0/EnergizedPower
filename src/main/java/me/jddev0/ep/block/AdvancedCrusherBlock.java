package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedCrusherBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class AdvancedCrusherBlock extends WorkerMachineBlock<AdvancedCrusherBlockEntity> {
    public static final MapCodec<AdvancedCrusherBlock> CODEC = simpleCodec(AdvancedCrusherBlock::new);

    public AdvancedCrusherBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ADVANCED_CRUSHER_ENTITY,
                AdvancedCrusherBlockEntity.class, AdvancedCrusherBlockEntity::new, AdvancedCrusherBlockEntity::tick
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public static class Item extends BlockItem {
        public Item(Block block,  Item.Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("tooltip.energizedpower.advanced_crusher.txt.shift.1").withStyle(ChatFormatting.GRAY));
            }else {
                tooltip.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
