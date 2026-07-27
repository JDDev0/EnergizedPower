package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EliteChargerBlockEntity;
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

public class EliteChargerBlock extends HorizontallyOrientableWorkerMachineBlock<EliteChargerBlockEntity> {
    public static final MapCodec<EliteChargerBlock> CODEC = simpleCodec(EliteChargerBlock::new);

    public EliteChargerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.ELITE_CHARGER_ENTITY,
                EliteChargerBlockEntity.class, EliteChargerBlockEntity::new, EliteChargerBlockEntity::tick
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
        public void appendHoverText(ItemStack stack, AdvancedChargerBlock.Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("tooltip.energizedpower.chargers.txt.shift.1").
                        withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("tooltip.energizedpower.chargers.txt.shift.2.1").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                tooltip.add(Component.translatable("tooltip.energizedpower.chargers.txt.shift.2.2").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                tooltip.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
