package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.machine.tier.SolarPanelTier;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarPanelBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.d, 0.d, 0.d, 16.d, 4.d, 16.d);

    private final SolarPanelTier tier;

    public SolarPanelBlock(SolarPanelTier tier) {
        super(tier.getProperties());

        this.tier = tier;
    }

    public SolarPanelTier getTier() {
        return tier;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockGetter, BlockPos blockPos, ShapeContext collisionContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new SolarPanelBlockEntity(blockPos, state, tier);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if(state.getBlock() == newState.getBlock())
            return;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof SolarPanelBlockEntity))
            return;

        ((SolarPanelBlockEntity)blockEntity).drops(level, blockPos);

        super.onStateReplaced(state, level, blockPos, newState, isMoving);
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, Hand handItem, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof SolarPanelBlockEntity) || ((SolarPanelBlockEntity)blockEntity).getTier() != tier)
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((SolarPanelBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, tier.getEntityTypeFromTier(), SolarPanelBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final SolarPanelTier tier;

        public Item(Block block, FabricItemSettings props, SolarPanelTier tier) {
            super(block, props);

            this.tier = tier;
        }

        public SolarPanelTier getTier() {
            return tier;
        }

        @Override
        public void appendTooltip(ItemStack itemStack, @Nullable World level, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.energizedpower.solar_panel.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(tier.getPeakFePerTick())).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.energizedpower.solar_panel.txt.shift.2").formatted(Formatting.GRAY));
            }else {
                tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }

}
