package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.AdvancedBatteryBoxBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class AdvancedBatteryBoxBlock extends BaseEntityBlock {
    public static final MapCodec<AdvancedBatteryBoxBlock> CODEC = simpleCodec(AdvancedBatteryBoxBlock::new);

    public AdvancedBatteryBoxBlock(Properties props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new AdvancedBatteryBoxBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof AdvancedBatteryBoxBlockEntity advancedBatteryBoxBlockEntity))
            return super.getAnalogOutputSignal(state, level, blockPos);

        return advancedBatteryBoxBlockEntity.getRedstoneOutput();
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof AdvancedBatteryBoxBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openMenu((AdvancedBatteryBoxBlockEntity)blockEntity, blockPos);

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EPBlockEntities.ADVANCED_BATTERY_BOX_ENTITY.get(), AdvancedBatteryBoxBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Screen.hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.capacity.txt",
                                EnergyUtils.getEnergyWithPrefix(AdvancedBatteryBoxBlockEntity.CAPACITY)).
                        withStyle(ChatFormatting.GRAY));
                components.accept(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                                EnergyUtils.getEnergyWithPrefix(AdvancedBatteryBoxBlockEntity.MAX_TRANSFER)).
                        withStyle(ChatFormatting.GRAY));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
