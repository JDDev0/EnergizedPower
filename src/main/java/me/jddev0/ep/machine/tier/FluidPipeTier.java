package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.FluidPipeBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public enum FluidPipeTier {
    IRON("fluid_pipe", ModConfigs.COMMON_IRON_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)),
    GOLDEN("golden_fluid_pipe", ModConfigs.COMMON_GOLDEN_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));

    private final String resourceId;
    private final int transferRate;
    private final BlockBehaviour.Properties props;

    FluidPipeTier(String resourceId, int transferRate, BlockBehaviour.Properties props) {
        this.resourceId = resourceId;
        this.transferRate = transferRate;
        this.props = props;
    }

    public Block getBlockFromTier() {
        return switch(this) {
            case IRON -> EPBlocks.IRON_FLUID_PIPE.get();
            case GOLDEN -> EPBlocks.GOLDEN_FLUID_PIPE.get();
        };
    }

    public BlockEntityType<FluidPipeBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case IRON -> EPBlockEntities.IRON_FLUID_PIPE_ENTITY.get();
            case GOLDEN -> EPBlockEntities.GOLDEN_FLUID_PIPE_ENTITY.get();
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getTransferRate() {
        return transferRate;
    }

    public BlockBehaviour.Properties getProperties() {
        return props;
    }
}
