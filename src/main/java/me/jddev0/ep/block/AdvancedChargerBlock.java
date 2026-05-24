package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.AdvancedChargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class AdvancedChargerBlock extends WorkerMachineBlock<AdvancedChargerBlockEntity> {
    public static final MapCodec<AdvancedChargerBlock> CODEC = simpleCodec(AdvancedChargerBlock::new);

    public AdvancedChargerBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ADVANCED_CHARGER_ENTITY,
                AdvancedChargerBlockEntity.class, AdvancedChargerBlockEntity::new, AdvancedChargerBlockEntity::tick
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
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Minecraft.getInstance().hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.chargers.txt.shift.1").
                        withStyle(ChatFormatting.GRAY));
                components.accept(Component.empty());
                components.accept(Component.translatable("tooltip.energizedpower.chargers.txt.shift.2.1").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                components.accept(Component.translatable("tooltip.energizedpower.chargers.txt.shift.2.2").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
