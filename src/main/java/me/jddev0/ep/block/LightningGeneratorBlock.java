package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.LightningGeneratorBlockEntity;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightningGeneratorBlock extends BlockWithEntity {
    public static final int ENERGY_PER_LIGHTNING_STRIKE = 1000000;

    public static final BooleanProperty HIT_BY_LIGHTNING_BOLT = BooleanProperty.of("hit_by_lightning_bolt");

    private static final int ACTIVATION_TICKS = 8;

    public LightningGeneratorBlock(FabricBlockSettings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(HIT_BY_LIGHTNING_BOLT, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new LightningGeneratorBlockEntity(blockPos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(HIT_BY_LIGHTNING_BOLT);
    }

    @Override
    public void neighborUpdate(BlockState selfState, World level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborUpdate(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClient())
            return;

        if(!selfPos.up().equals(fromPos) || !(fromBlock instanceof LightningRodBlock))
            return;

        BlockState fromState = level.getBlockState(fromPos);
        if(!fromState.contains(LightningRodBlock.POWERED) || !fromState.get(LightningRodBlock.POWERED) ||
        !fromState.contains(LightningRodBlock.POWERED) || fromState.get(FacingBlock.FACING) != Direction.UP)
            return;

        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if(!(blockEntity instanceof LightningGeneratorBlockEntity))
            return;

        level.setBlockState(selfPos, selfState.with(HIT_BY_LIGHTNING_BOLT, Boolean.TRUE), 3);
        level.scheduleBlockTick(selfPos, this, ACTIVATION_TICKS);

        ((LightningGeneratorBlockEntity)blockEntity).onLightningStrike();
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld level, BlockPos blockPos, Random randomSource) {
        level.setBlockState(blockPos, state.with(HIT_BY_LIGHTNING_BOLT, false), 3);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.LIGHTING_GENERATOR_ENTITY, LightningGeneratorBlockEntity::tick);
    }

    @Override
    public void randomDisplayTick(BlockState state, World level, BlockPos blockPos, Random randomSource) {
        if(state.get(HIT_BY_LIGHTNING_BOLT)) {
            ParticleUtil.spawnParticle(level, blockPos, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(2, 5));
        }
    }

    public static class Item extends BlockItem {
        public Item(Block block, FabricItemSettings props) {
            super(block, props);
        }

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.energizedpower.lightning_generator.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(ENERGY_PER_LIGHTNING_STRIKE)).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.energizedpower.lightning_generator.txt.shift.2").formatted(Formatting.GRAY));
            }else {
                tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }
}
