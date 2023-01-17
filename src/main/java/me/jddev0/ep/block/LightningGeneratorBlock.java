package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.LightningGeneratorBlockEntity;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class LightningGeneratorBlock extends BaseEntityBlock {
    public static final int ENERGY_PER_LIGHTNING_STRIKE = 1000000;

    public static final BooleanProperty HIT_BY_LIGHTNING_BOLT = BooleanProperty.create("hit_by_lightning_bolt");

    private static final int ACTIVATION_TICKS = 8;

    public LightningGeneratorBlock(Properties props) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(HIT_BY_LIGHTNING_BOLT, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new LightningGeneratorBlockEntity(blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(HIT_BY_LIGHTNING_BOLT);
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, fromPos, isMoving);

        if(level.isClientSide())
            return;

        if(!selfPos.above().equals(fromPos) || !(fromBlock instanceof LightningRodBlock))
            return;

        BlockState fromState = level.getBlockState(fromPos);
        if(!fromState.hasProperty(LightningRodBlock.POWERED) || !fromState.getValue(LightningRodBlock.POWERED) ||
        !fromState.hasProperty(LightningRodBlock.POWERED) || fromState.getValue(DirectionalBlock.FACING) != Direction.UP)
            return;

        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if(!(blockEntity instanceof LightningGeneratorBlockEntity))
            return;

        level.setBlock(selfPos, selfState.setValue(HIT_BY_LIGHTNING_BOLT, Boolean.TRUE), 3);
        level.getBlockTicks().scheduleTick(selfPos, this, ACTIVATION_TICKS);

        ((LightningGeneratorBlockEntity)blockEntity).onLightningStrike();
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos blockPos, Random randomSource) {
        level.setBlock(blockPos, state.setValue(HIT_BY_LIGHTNING_BOLT, false), 3);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, Random randomSource) {
        if(state.getValue(HIT_BY_LIGHTNING_BOLT)) {
            ParticleUtils.spawnParticlesOnBlockFaces(level, blockPos, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(2, 5));
        }
    }

    public static class Item extends BlockItem {
        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
            if(Screen.hasShiftDown()) {
                components.add(new TranslatableComponent("tooltip.energizedpower.lightning_generator.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(ENERGY_PER_LIGHTNING_STRIKE)).withStyle(ChatFormatting.GRAY));
                components.add(new TranslatableComponent("tooltip.energizedpower.lightning_generator.txt.shift.2").withStyle(ChatFormatting.GRAY));
            }else {
                components.add(new TranslatableComponent("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
