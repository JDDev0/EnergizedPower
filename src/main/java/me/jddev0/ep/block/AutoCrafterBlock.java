package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
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

public class AutoCrafterBlock extends HorizontallyOrientableWorkerMachineBlock<AutoCrafterBlockEntity> {
    public static final MapCodec<AutoCrafterBlock> CODEC = simpleCodec(AutoCrafterBlock::new);

    public AutoCrafterBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.AUTO_CRAFTER_ENTITY,
                AutoCrafterBlockEntity.class, AutoCrafterBlockEntity::new, AutoCrafterBlockEntity::tick
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
                tooltip.accept(Component.translatable("tooltip.energizedpower.auto_crafter.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(AutoCrafterBlockEntity.ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT)).withStyle(ChatFormatting.GRAY));
            }else {
                tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
