package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.XPStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.screen.XPStorageMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public enum XPStorageTier {
    TINY("xp_storage_tiny", ModConfigs.COMMON_XP_STORAGE_TINY_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    SMALL("xp_storage_small", ModConfigs.COMMON_XP_STORAGE_SMALL_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    MEDIUM("xp_storage_medium", ModConfigs.COMMON_XP_STORAGE_MEDIUM_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    LARGE("xp_storage_large", ModConfigs.COMMON_XP_STORAGE_LARGE_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    GIANT("xp_storage_giant", ModConfigs.COMMON_XP_STORAGE_GIANT_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL));

    private final String resourceId;
    private final int xpStorageCapacity;
    private final BlockBehaviour.Properties props;

    XPStorageTier(String resourceId, int xpStorageCapacity, BlockBehaviour.Properties props) {
        this.resourceId = resourceId;
        this.xpStorageCapacity = xpStorageCapacity;
        this.props = props;
    }

    public Block getBlockFromTier() {
        return switch(this) {
            case TINY -> EPBlocks.XP_STORAGE_TINY.get();
            case SMALL -> EPBlocks.XP_STORAGE_SMALL.get();
            case MEDIUM -> EPBlocks.XP_STORAGE_MEDIUM.get();
            case LARGE -> EPBlocks.XP_STORAGE_LARGE.get();
            case GIANT -> EPBlocks.XP_STORAGE_GIANT.get();
        };
    }

    public BlockEntityType<XPStorageBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case TINY -> EPBlockEntities.XP_STORAGE_TINY_ENTITY.get();
            case SMALL -> EPBlockEntities.XP_STORAGE_SMALL_ENTITY.get();
            case MEDIUM -> EPBlockEntities.XP_STORAGE_MEDIUM_ENTITY.get();
            case LARGE -> EPBlockEntities.XP_STORAGE_LARGE_ENTITY.get();
            case GIANT -> EPBlockEntities.XP_STORAGE_GIANT_ENTITY.get();
        };
    }

    public MenuType<XPStorageMenu> getMenuTypeFromTier() {
        return switch(this) {
            case TINY -> EPMenuTypes.XP_STORAGE_TINY.get();
            case SMALL -> EPMenuTypes.XP_STORAGE_SMALL.get();
            case MEDIUM -> EPMenuTypes.XP_STORAGE_MEDIUM.get();
            case LARGE -> EPMenuTypes.XP_STORAGE_LARGE.get();
            case GIANT -> EPMenuTypes.XP_STORAGE_GIANT.get();
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getXPStorageCapacity() {
        return xpStorageCapacity;
    }

    public BlockBehaviour.Properties getProperties() {
        return props;
    }
}
