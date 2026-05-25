package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.FullyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.MinecartUnchargerBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import java.util.List;

public class MinecartUnchargerBlock extends FullyOrientableWorkerMachineBlock<MinecartUnchargerBlockEntity> {
    public static final MapCodec<MinecartUnchargerBlock> CODEC = simpleCodec(MinecartUnchargerBlock::new);

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    protected MinecartUnchargerBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.MINECART_UNCHARGER_ENTITY,
                MinecartUnchargerBlockEntity.class, MinecartUnchargerBlockEntity::new, MinecartUnchargerBlockEntity::tick
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
                                EnergyUtils.getEnergyWithPrefix(MinecartUnchargerBlockEntity.MAX_TRANSFER)).
                        withStyle(ChatFormatting.GRAY));
            }else {
                tooltip.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
