package me.jddev0.ep.block;

import me.jddev0.ep.block.entity.ChargingStationBlockEntity;
import me.jddev0.ep.block.entity.ModBlockEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.ToIntFunction;

public class ChargingStationBlock extends BlockWithEntity {
    public static final BooleanProperty CHARGING = BooleanProperty.of("charging");

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.get(CHARGING) ? 8 : 0;


    public ChargingStationBlock(FabricBlockSettings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(CHARGING, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new ChargingStationBlockEntity(blockPos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos blockPos, PlayerEntity player, Hand handItem, BlockHitResult hit) {
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof ChargingStationBlockEntity))
            throw new IllegalStateException("Container is invalid");

        player.openHandledScreen((ChargingStationBlockEntity)blockEntity);

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(CHARGING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CHARGING_STATION_ENTITY, ChargingStationBlockEntity::tick);
    }

    @Override
    public void randomDisplayTick(BlockState state, World level, BlockPos blockPos, Random randomSource) {
        if(state.get(CHARGING)) {
            double x = blockPos.getX() + .5;
            double y = blockPos.getY() + .9;
            double z = blockPos.getZ() + .5;

            for(Direction direction:Direction.values()) {
                if(direction.getAxis() == Direction.Axis.Y)
                    continue;

                double dxz = randomSource.nextDouble() * .3 - .15;

                double dx = direction.getAxis() == Direction.Axis.X?direction.getOffsetX() * .35:dxz;
                double dy = randomSource.nextDouble() * .2;
                double dz = direction.getAxis() == Direction.Axis.Z?direction.getOffsetZ() * .35:dxz;

                level.addParticle(ParticleTypes.ELECTRIC_SPARK, x + dx, y + dy, z + dz, 0., 0., 0.);
            }
        }
    }

    public static class Item extends BlockItem {
        public Item(Block block, FabricItemSettings props) {
            super(block, props);
        }

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.energizedpower.charging_station.txt.shift.1").formatted(Formatting.GRAY));
            }else {
                tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }
}
