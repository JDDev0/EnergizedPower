package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.LightningGeneratorBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class LightningGeneratorBlock extends BaseEntityBlock {
    public static final MapCodec<LightningGeneratorBlock> CODEC = simpleCodec(LightningGeneratorBlock::new);

    public static final int ENERGY_PER_LIGHTNING_STRIKE = ModConfigs.COMMON_LIGHTNING_GENERATOR_CAPACITY.getValue();

    public static final BooleanProperty HIT_BY_LIGHTNING_BOLT = BooleanProperty.create("hit_by_lightning_bolt");

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.getValue(HIT_BY_LIGHTNING_BOLT) ? 15 : 0;

    private static final int ACTIVATION_TICKS = 8;

    public LightningGeneratorBlock(Properties props) {
        super(props);

        this.registerDefaultState(this.stateDefinition.any().setValue(HIT_BY_LIGHTNING_BOLT, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof LightningGeneratorBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openMenu((LightningGeneratorBlockEntity)blockEntity, blockPos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState selfState, Level level, BlockPos selfPos, Block fromBlock, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(selfState, level, selfPos, fromBlock, orientation, isMoving);

        if(level.isClientSide())
            return;

        if(!(fromBlock instanceof LightningRodBlock) || !(level.getBlockState(selfPos.above()).getBlock() instanceof LightningRodBlock))
            return;

        BlockState fromState = level.getBlockState(selfPos.above());
        if(!fromState.hasProperty(LightningRodBlock.POWERED) || !fromState.getValue(LightningRodBlock.POWERED) ||
        !fromState.hasProperty(LightningRodBlock.POWERED) || fromState.getValue(DirectionalBlock.FACING) != Direction.UP)
            return;

        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if(!(blockEntity instanceof LightningGeneratorBlockEntity))
            return;

        level.setBlock(selfPos, selfState.setValue(HIT_BY_LIGHTNING_BOLT, Boolean.TRUE), 3);
        level.scheduleTick(selfPos, this, ACTIVATION_TICKS);

        ((LightningGeneratorBlockEntity)blockEntity).onLightningStrike();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EPBlockEntities.LIGHTING_GENERATOR_ENTITY.get(), LightningGeneratorBlockEntity::tick);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        level.setBlock(blockPos, state.setValue(HIT_BY_LIGHTNING_BOLT, false), 3);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource randomSource) {
        if(state.getValue(HIT_BY_LIGHTNING_BOLT)) {
            ParticleUtils.spawnParticlesOnBlockFaces(level, blockPos, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(2, 5));
        }
    }

    public static class Item extends BlockItem {
        public Item(Block block, Properties props) {
            super(block, props);
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Minecraft.getInstance().hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.lightning_generator.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(ENERGY_PER_LIGHTNING_STRIKE)).withStyle(ChatFormatting.GRAY));
                components.accept(Component.translatable("tooltip.energizedpower.lightning_generator.txt.shift.2").withStyle(ChatFormatting.GRAY));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
