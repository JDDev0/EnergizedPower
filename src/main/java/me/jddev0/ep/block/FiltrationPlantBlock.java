package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
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

public class FiltrationPlantBlock extends HorizontallyOrientableWorkerMachineBlock<FiltrationPlantBlockEntity> {
    public static final MapCodec<FiltrationPlantBlock> CODEC = simpleCodec(FiltrationPlantBlock::new);

    public FiltrationPlantBlock(Properties props) {
        super(
                props,

                () -> EPBlockEntities.FILTRATION_PLANT_ENTITY,
                FiltrationPlantBlockEntity.class, FiltrationPlantBlockEntity::new, FiltrationPlantBlockEntity::tick
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
                tooltip.accept(Component.translatable("tooltip.energizedpower.filtration_plant.txt.shift.1").withStyle(ChatFormatting.GRAY));
            }else {
                tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
