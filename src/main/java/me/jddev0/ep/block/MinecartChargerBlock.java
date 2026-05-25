package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.FullyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.MinecartChargerBlockEntity;
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

public class MinecartChargerBlock extends FullyOrientableWorkerMachineBlock<MinecartChargerBlockEntity> {
    public static final MapCodec<MinecartChargerBlock> CODEC = simpleCodec(MinecartChargerBlock::new);

    protected MinecartChargerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.MINECART_CHARGER_ENTITY,
                MinecartChargerBlockEntity.class, MinecartChargerBlockEntity::new, MinecartChargerBlockEntity::tick
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
        public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag type) {
            if(Minecraft.getInstance().hasShiftDown()) {
                tooltip.accept(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(MinecartChargerBlockEntity.MAX_TRANSFER)).
                        withStyle(ChatFormatting.GRAY));
            }else {
                tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
