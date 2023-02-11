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
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ChargingStationBlock extends BlockWithEntity {
    public static final BooleanProperty CHARGING = BooleanProperty.of("charging");

    private static final int ACTIVATION_TICKS = 8;

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
            //TODO
        }
    }

    public static class Item extends BlockItem {
        public Item(Block block, FabricItemSettings props) {
            super(block, props);
        }

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            if(Screen.hasShiftDown()) {
                tooltip.add(new TranslatableText("tooltip.energizedpower.charging_station.txt.shift.1").formatted(Formatting.GRAY));
            }else {
                tooltip.add(new TranslatableText("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
            }
        }
    }
}
