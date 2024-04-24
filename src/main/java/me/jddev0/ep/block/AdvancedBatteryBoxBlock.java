package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.AdvancedBatteryBoxBlockEntity;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdvancedBatteryBoxBlock extends BlockWithEntity {
    public static final MapCodec<AdvancedBatteryBoxBlock> CODEC = createCodec(AdvancedBatteryBoxBlock::new);

    public AdvancedBatteryBoxBlock(AbstractBlock.Settings props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new AdvancedBatteryBoxBlockEntity(blockPos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof AdvancedBatteryBoxBlockEntity advancedBatteryBoxBlockEntity))
            return super.getComparatorOutput(state, level, blockPos);

        return advancedBatteryBoxBlockEntity.getRedstoneOutput();
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof AdvancedBatteryBoxBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((AdvancedBatteryBoxBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.ADVANCED_BATTERY_BOX_ENTITY, AdvancedBatteryBoxBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        public Item(Block block, Item.Settings props) {
            super(block, props);
        }

        @Override
        public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.energizedpower.capacity.txt",
                                EnergyUtils.getEnergyWithPrefix(AdvancedBatteryBoxBlockEntity.CAPACITY)).
                        formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(AdvancedBatteryBoxBlockEntity.MAX_TRANSFER)).
                        formatted(Formatting.GRAY));
            }else {
                tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }
}
