package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.ItemSiloBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.screen.ItemSiloMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public enum ItemSiloTier {
    TINY("item_silo_tiny", ModConfigs.COMMON_ITEM_SILO_TINY_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    SMALL("item_silo_small", ModConfigs.COMMON_ITEM_SILO_SMALL_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    MEDIUM("item_silo_medium", ModConfigs.COMMON_ITEM_SILO_MEDIUM_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    LARGE("item_silo_large", ModConfigs.COMMON_ITEM_SILO_LARGE_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    GIANT("item_silo_giant", ModConfigs.COMMON_ITEM_SILO_GIANT_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL));

    private final String resourceId;
    private final int itemSiloCapacity;
    private final BlockBehaviour.Properties props;

    ItemSiloTier(String resourceId, int itemSiloCapacity, BlockBehaviour.Properties props) {
        this.resourceId = resourceId;
        this.itemSiloCapacity = itemSiloCapacity;
        this.props = props;
    }

    public Block getBlockFromTier() {
        return switch(this) {
            case TINY -> EPBlocks.ITEM_SILO_TINY.get();
            case SMALL -> EPBlocks.ITEM_SILO_SMALL.get();
            case MEDIUM -> EPBlocks.ITEM_SILO_MEDIUM.get();
            case LARGE -> EPBlocks.ITEM_SILO_LARGE.get();
            case GIANT -> EPBlocks.ITEM_SILO_GIANT.get();
        };
    }

    public BlockEntityType<ItemSiloBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case TINY -> EPBlockEntities.ITEM_SILO_TINY_ENTITY.get();
            case SMALL -> EPBlockEntities.ITEM_SILO_SMALL_ENTITY.get();
            case MEDIUM -> EPBlockEntities.ITEM_SILO_MEDIUM_ENTITY.get();
            case LARGE -> EPBlockEntities.ITEM_SILO_LARGE_ENTITY.get();
            case GIANT -> EPBlockEntities.ITEM_SILO_GIANT_ENTITY.get();
        };
    }

    public MenuType<ItemSiloMenu> getMenuTypeFromTier() {
        return switch(this) {
            case TINY -> EPMenuTypes.ITEM_SILO_TINY.get();
            case SMALL -> EPMenuTypes.ITEM_SILO_SMALL.get();
            case MEDIUM -> EPMenuTypes.ITEM_SILO_MEDIUM.get();
            case LARGE -> EPMenuTypes.ITEM_SILO_LARGE.get();
            case GIANT -> EPMenuTypes.ITEM_SILO_GIANT.get();
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getItemSiloCapacity() {
        return itemSiloCapacity;
    }

    public BlockBehaviour.Properties getProperties() {
        return props;
    }
}
