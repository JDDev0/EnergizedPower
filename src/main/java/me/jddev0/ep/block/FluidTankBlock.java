package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidTankBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private final Tier tier;

    public static Block getBlockFromTier(Tier tier) {
        return switch(tier) {
            case SMALL -> ModBlocks.FLUID_TANK_SMALL;
            case MEDIUM -> ModBlocks.FLUID_TANK_MEDIUM;
        };
    }

    public FluidTankBlock(Tier tier) {
        super(tier.getProperties());

        this.tier = tier;

        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    public Tier getTier() {
        return tier;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new FluidTankBlockEntity(blockPos, state, tier);
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
        if(!(blockEntity instanceof FluidTankBlockEntity fluidTankBlockEntity))
            return super.getComparatorOutput(state, level, blockPos);

        return fluidTankBlockEntity.getRedstoneOutput();
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, Hand handItem, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof FluidTankBlockEntity) || ((FluidTankBlockEntity)blockEntity).getTier() != tier)
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((FluidTankBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, FluidTankBlockEntity.getEntityTypeFromTier(tier), FluidTankBlockEntity::tick);
    }

    public static class Item extends BlockItem {
        private final Tier tier;

        public Item(Block block, FabricItemSettings props, Tier tier) {
            super(block, props);

            this.tier = tier;
        }

        public Tier getTier() {
            return tier;
        }

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.energizedpower.tank_capacity.txt",
                                FluidUtils.getFluidAmountWithPrefix(FluidUtils.convertDropletsToMilliBuckets(tier.getTankCapacity())).
                                        formatted(Formatting.GRAY)));
            }else {
                tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }

    public enum Tier {
        SMALL("fluid_tank_small", FluidUtils.convertMilliBucketsToDroplets(
                1000 * ModConfigs.COMMON_FLUID_TANK_SMALL_TANK_CAPACITY.getValue()),
                FabricBlockSettings.of(Material.METAL).
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
        MEDIUM("fluid_tank_medium", FluidUtils.convertMilliBucketsToDroplets(
                1000 * ModConfigs.COMMON_FLUID_TANK_MEDIUM_TANK_CAPACITY.getValue()),
                FabricBlockSettings.of(Material.METAL).
                        requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL));

        private final String resourceId;
        private final long tankCapacity;
        private final FabricBlockSettings props;

        Tier(String resourceId, long tankCapacity, FabricBlockSettings props) {
            this.resourceId = resourceId;
            this.tankCapacity = tankCapacity;
            this.props = props;
        }

        public String getResourceId() {
            return resourceId;
        }

        public long getTankCapacity() {
            return tankCapacity;
        }

        public FabricBlockSettings getProperties() {
            return props;
        }
    }
}
