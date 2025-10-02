package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.block.entity.LightningGeneratorBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class LightningGeneratorBlock extends BlockWithEntity {
    public static final MapCodec<LightningGeneratorBlock> CODEC = createCodec(LightningGeneratorBlock::new);

    public static final long ENERGY_PER_LIGHTNING_STRIKE = ModConfigs.COMMON_LIGHTNING_GENERATOR_CAPACITY.getValue();

    public static final BooleanProperty HIT_BY_LIGHTNING_BOLT = BooleanProperty.of("hit_by_lightning_bolt");

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.get(HIT_BY_LIGHTNING_BOLT) ? 8 : 0;

    private static final int ACTIVATION_TICKS = 8;

    public LightningGeneratorBlock(AbstractBlock.Settings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(HIT_BY_LIGHTNING_BOLT, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
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
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof LightningGeneratorBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((LightningGeneratorBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(HIT_BY_LIGHTNING_BOLT);
    }

    @Override
    public void neighborUpdate(BlockState selfState, World level, BlockPos selfPos, Block fromBlock, @Nullable WireOrientation wireOrientation, boolean isMoving) {
        super.neighborUpdate(selfState, level, selfPos, fromBlock, wireOrientation, isMoving);

        if(level.isClient())
            return;

        if(!(fromBlock instanceof LightningRodBlock) || !(level.getBlockState(selfPos.up()).getBlock() instanceof LightningRodBlock))
            return;

        BlockState fromState = level.getBlockState(selfPos.up());
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
        return validateTicker(type, EPBlockEntities.LIGHTING_GENERATOR_ENTITY, LightningGeneratorBlockEntity::tick);
    }

    @Override
    public void randomDisplayTick(BlockState state, World level, BlockPos blockPos, Random randomSource) {
        if(state.get(HIT_BY_LIGHTNING_BOLT)) {
            ParticleUtil.spawnParticle(level, blockPos, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(2, 5));
        }
    }

    public static class Item extends BlockItem {
        public Item(Block block, Item.Settings props) {
            super(block, props);
        }

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
            if(MinecraftClient.getInstance().isShiftPressed()) {
                tooltip.accept(Text.translatable("tooltip.energizedpower.lightning_generator.txt.shift.1",
                        EnergyUtils.getEnergyWithPrefix(ENERGY_PER_LIGHTNING_STRIKE)).formatted(Formatting.GRAY));
                tooltip.accept(Text.translatable("tooltip.energizedpower.lightning_generator.txt.shift.2").formatted(Formatting.GRAY));
            }else {
                tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }
}
