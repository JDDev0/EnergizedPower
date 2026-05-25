package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.BatteryBoxBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BatteryBoxBlock extends WorkerMachineBlock<BatteryBoxBlockEntity> {
    public static final MapCodec<BatteryBoxBlock> CODEC = simpleCodec(BatteryBoxBlock::new);

    public BatteryBoxBlock(Properties props) {
        super(
                props,

                EPBlockEntities.BATTERY_BOX_ENTITY,
                BatteryBoxBlockEntity.class, BatteryBoxBlockEntity::new, BatteryBoxBlockEntity::tick
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
        public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
            if(Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.energizedpower.capacity.txt",
                                EnergyUtils.getEnergyWithPrefix(BatteryBoxBlockEntity.CAPACITY)).
                        withStyle(ChatFormatting.GRAY));
                components.add(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(BatteryBoxBlockEntity.MAX_TRANSFER)).
                        withStyle(ChatFormatting.GRAY));
            }else {
                components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
