package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.EliteAutoCrafterBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

public class EliteAutoCrafterBlock extends HorizontallyOrientableWorkerMachineBlock<EliteAutoCrafterBlockEntity> {
    public static final MapCodec<EliteAutoCrafterBlock> CODEC = simpleCodec(EliteAutoCrafterBlock::new);

    public EliteAutoCrafterBlock(Properties props) {
        super(
                props,

                EPBlockEntities.ELITE_AUTO_CRAFTER_ENTITY,
                EliteAutoCrafterBlockEntity.class, EliteAutoCrafterBlockEntity::new, EliteAutoCrafterBlockEntity::tick
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
                components.add(Component.translatable("tooltip.energizedpower.auto_crafter.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(EliteAutoCrafterBlockEntity.ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT)).withStyle(ChatFormatting.GRAY));
            }else {
                components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
