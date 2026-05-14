package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.FluidPipeBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public enum FluidPipeTier {
    IRON("fluid_pipe", FluidUtils.convertMilliBucketsToDroplets(
            ModConfigs.COMMON_IRON_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue()),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)),
    GOLDEN("golden_fluid_pipe", FluidUtils.convertMilliBucketsToDroplets(
            ModConfigs.COMMON_GOLDEN_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue()),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));

    private final String resourceId;
    private final long transferRate;
    private final BlockBehaviour.Properties props;

    FluidPipeTier(String resourceId, long transferRate, BlockBehaviour.Properties props) {
        this.resourceId = resourceId;
        this.transferRate = transferRate;
        this.props = props;
    }

    public BlockEntityType<FluidPipeBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case IRON -> EPBlockEntities.IRON_FLUID_PIPE_ENTITY;
            case GOLDEN -> EPBlockEntities.GOLDEN_FLUID_PIPE_ENTITY;
        };
    }

    public Block getBlockFromTier() {
        return switch(this) {
            case IRON -> EPBlocks.IRON_FLUID_PIPE;
            case GOLDEN -> EPBlocks.GOLDEN_FLUID_PIPE;
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public long getTransferRate() {
        return transferRate;
    }

    public BlockBehaviour.Properties getProperties() {
        return props;
    }
}
