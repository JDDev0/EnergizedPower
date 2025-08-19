package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.ItemSiloBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.screen.ItemSiloMenu;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;

public enum ItemSiloTier {
    TINY("item_silo_tiny", ModConfigs.COMMON_ITEM_SILO_TINY_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
    SMALL("item_silo_small", ModConfigs.COMMON_ITEM_SILO_SMALL_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
    MEDIUM("item_silo_medium", ModConfigs.COMMON_ITEM_SILO_MEDIUM_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
    LARGE("item_silo_large", ModConfigs.COMMON_ITEM_SILO_LARGE_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
    GIANT("item_silo_giant", ModConfigs.COMMON_ITEM_SILO_GIANT_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL));

    private final String resourceId;
    private final int itemSiloCapacity;
    private final AbstractBlock.Settings props;

    ItemSiloTier(String resourceId, int itemSiloCapacity, AbstractBlock.Settings props) {
        this.resourceId = resourceId;
        this.itemSiloCapacity = itemSiloCapacity;
        this.props = props;
    }

    public Block getBlockFromTier() {
        return switch(this) {
            case TINY -> EPBlocks.ITEM_SILO_TINY;
            case SMALL -> EPBlocks.ITEM_SILO_SMALL;
            case MEDIUM -> EPBlocks.ITEM_SILO_MEDIUM;
            case LARGE -> EPBlocks.ITEM_SILO_LARGE;
            case GIANT -> EPBlocks.ITEM_SILO_GIANT;
        };
    }

    public BlockEntityType<ItemSiloBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case TINY -> EPBlockEntities.ITEM_SILO_TINY_ENTITY;
            case SMALL -> EPBlockEntities.ITEM_SILO_SMALL_ENTITY;
            case MEDIUM -> EPBlockEntities.ITEM_SILO_MEDIUM_ENTITY;
            case LARGE -> EPBlockEntities.ITEM_SILO_LARGE_ENTITY;
            case GIANT -> EPBlockEntities.ITEM_SILO_GIANT_ENTITY;
        };
    }

    public ScreenHandlerType<ItemSiloMenu> getMenuTypeFromTier() {
        return switch(this) {
            case TINY -> EPMenuTypes.ITEM_SILO_TINY;
            case SMALL -> EPMenuTypes.ITEM_SILO_SMALL;
            case MEDIUM -> EPMenuTypes.ITEM_SILO_MEDIUM;
            case LARGE -> EPMenuTypes.ITEM_SILO_LARGE;
            case GIANT -> EPMenuTypes.ITEM_SILO_GIANT;
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getItemSiloCapacity() {
        return itemSiloCapacity;
    }

    public AbstractBlock.Settings getProperties() {
        return props;
    }
}
