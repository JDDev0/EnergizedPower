package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.ChargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.*;

import java.util.function.Consumer;

public class ChargerBlock extends WorkerMachineBlock<ChargerBlockEntity> {
    public static final MapCodec<ChargerBlock> CODEC = simpleCodec(ChargerBlock::new);

    public ChargerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.CHARGER_ENTITY,
                ChargerBlockEntity.class, ChargerBlockEntity::new, ChargerBlockEntity::tick
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
                tooltip.accept(Component.translatable("tooltip.energizedpower.chargers.txt.shift.1").
                        withStyle(ChatFormatting.GRAY));
                tooltip.accept(Component.empty());
                tooltip.accept(Component.translatable("tooltip.energizedpower.chargers.txt.shift.2.1").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                tooltip.accept(Component.translatable("tooltip.energizedpower.chargers.txt.shift.2.2").
                        withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }else {
                tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
