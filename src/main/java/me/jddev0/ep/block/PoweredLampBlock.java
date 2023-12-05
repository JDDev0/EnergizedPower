package me.jddev0.ep.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.block.entity.PoweredLampBlockEntity;
import me.jddev0.ep.codec.CodecFix;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class PoweredLampBlock extends BlockWithEntity {
    public static final MapCodec<PoweredLampBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(CodecFix.FABRIC_BLOCK_SETTINGS_CODEC.fieldOf("properties").forGetter(block -> {
            return (FabricBlockSettings)block.getSettings();
        })).apply(instance, PoweredLampBlock::new);
    });

    public static final IntProperty LEVEL = Properties.LEVEL_15;

    public static final ToIntFunction<BlockState> LIGHT_EMISSION =
            (state) -> state.get(LEVEL);

    protected PoweredLampBlock(FabricBlockSettings props) {
        super(props);

        this.setDefaultState(this.getStateManager().getDefaultState().with(LEVEL, 0));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState state) {
        return new PoweredLampBlockEntity(blockPos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(LEVEL);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.POWERED_LAMP_ENTITY, PoweredLampBlockEntity::tick);
    }
}
