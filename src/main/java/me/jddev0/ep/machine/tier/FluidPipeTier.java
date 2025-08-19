package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.FluidPipeBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;

public enum FluidPipeTier {
    IRON("fluid_pipe", FluidUtils.convertMilliBucketsToDroplets(
            ModConfigs.COMMON_IRON_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue()),
            FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)),
    GOLDEN("golden_fluid_pipe", FluidUtils.convertMilliBucketsToDroplets(
            ModConfigs.COMMON_GOLDEN_FLUID_PIPE_FLUID_TRANSFER_RATE.getValue()),
            FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));

    private final String resourceId;
    private final long transferRate;
    private final FabricBlockSettings props;

    FluidPipeTier(String resourceId, long transferRate, FabricBlockSettings props) {
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

    public FabricBlockSettings getProperties() {
        return props;
    }
}
