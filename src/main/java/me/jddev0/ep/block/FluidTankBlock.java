package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class FluidTankBlock extends BaseEntityBlock {
    public static final MapCodec<FluidTankBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(ExtraCodecs.NON_EMPTY_STRING.xmap(Tier::valueOf, Tier::toString).fieldOf("tier").
                forGetter(FluidTankBlock::getTier),
                        Properties.CODEC.fieldOf("properties").forGetter(Block::properties)).
                apply(instance, FluidTankBlock::new);
    });

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final Tier tier;

    public static Block getBlockFromTier(Tier tier) {
        return switch(tier) {
            case SMALL -> EPBlocks.FLUID_TANK_SMALL.get();
            case MEDIUM -> EPBlocks.FLUID_TANK_MEDIUM.get();
            case LARGE -> EPBlocks.FLUID_TANK_LARGE.get();
        };
    }

    public FluidTankBlock(Tier tier, Properties properties) {
        super(properties);

        this.tier = tier;

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState state) {
        return new FluidTankBlockEntity(blockPos, state, tier);
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
        if(!(blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity))
            return super.getAnalogOutputSignal(state, level, blockPos);

        return fluidTankBlockEntity.getRedstoneOutput();
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof FluidTankBlockEntity) || ((FluidTankBlockEntity)blockEntity).getTier() != tier)
            throw new IllegalStateException("Container is invalid");

        player.openMenu((FluidTankBlockEntity)blockEntity, blockPos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, FluidTankBlockEntity.getEntityTypeFromTier(tier), FluidTankBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final Tier tier;

        public Item(Block block, Properties props, Tier tier) {
            super(block, props);

            this.tier = tier;
        }

        public Tier getTier() {
            return tier;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
            if(Screen.hasShiftDown()) {
                components.accept(Component.translatable("tooltip.energizedpower.tank_capacity.txt",
                                FluidUtils.getFluidAmountWithPrefix(tier.getTankCapacity())).withStyle(ChatFormatting.GRAY));
            }else {
                components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    public enum Tier {
        SMALL("fluid_tank_small", 1000 * ModConfigs.COMMON_FLUID_TANK_SMALL_TANK_CAPACITY.getValue(),
                Properties.of().
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        MEDIUM("fluid_tank_medium", 1000 * ModConfigs.COMMON_FLUID_TANK_MEDIUM_TANK_CAPACITY.getValue(),
                Properties.of().
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
        LARGE("fluid_tank_large", 1000 * ModConfigs.COMMON_FLUID_TANK_LARGE_TANK_CAPACITY.getValue(),
                Properties.of().
                        requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL));

        private final String resourceId;
        private final int tankCapacity;
        private final Properties props;

        Tier(String resourceId, int tankCapacity, Properties props) {
            this.resourceId = resourceId;
            this.tankCapacity = tankCapacity;
            this.props = props;
        }

        public String getResourceId() {
            return resourceId;
        }

        public int getTankCapacity() {
            return tankCapacity;
        }

        public Properties getProperties() {
            return props;
        }
    }
}
