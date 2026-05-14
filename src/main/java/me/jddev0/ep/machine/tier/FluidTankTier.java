package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.screen.FluidTankMenu;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public enum FluidTankTier {
    SMALL("fluid_tank_small", FluidUtils.convertMilliBucketsToDroplets(
            1000 * ModConfigs.COMMON_FLUID_TANK_SMALL_TANK_CAPACITY.getValue()),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    MEDIUM("fluid_tank_medium", FluidUtils.convertMilliBucketsToDroplets(
            1000 * ModConfigs.COMMON_FLUID_TANK_MEDIUM_TANK_CAPACITY.getValue()),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    LARGE("fluid_tank_large", FluidUtils.convertMilliBucketsToDroplets(
            1000 * ModConfigs.COMMON_FLUID_TANK_LARGE_TANK_CAPACITY.getValue()),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL));

    private final String resourceId;
    private final long tankCapacity;
    private final BlockBehaviour.Properties props;

    FluidTankTier(String resourceId, long tankCapacity, BlockBehaviour.Properties props) {
        this.resourceId = resourceId;
        this.tankCapacity = tankCapacity;
        this.props = props;
    }

    public Block getBlockFromTier() {
        return switch(this) {
            case SMALL -> EPBlocks.FLUID_TANK_SMALL;
            case MEDIUM -> EPBlocks.FLUID_TANK_MEDIUM;
            case LARGE -> EPBlocks.FLUID_TANK_LARGE;
        };
    }

    public BlockEntityType<FluidTankBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case SMALL -> EPBlockEntities.FLUID_TANK_SMALL_ENTITY;
            case MEDIUM -> EPBlockEntities.FLUID_TANK_MEDIUM_ENTITY;
            case LARGE -> EPBlockEntities.FLUID_TANK_LARGE_ENTITY;
        };
    }

    public MenuType<FluidTankMenu> getMenuTypeFromTier() {
        return switch(this) {
            case SMALL -> EPMenuTypes.FLUID_TANK_SMALL;
            case MEDIUM -> EPMenuTypes.FLUID_TANK_MEDIUM;
            case LARGE -> EPMenuTypes.FLUID_TANK_LARGE;
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public long getTankCapacity() {
        return tankCapacity;
    }

    public BlockBehaviour.Properties getProperties() {
        return props;
    }
}
