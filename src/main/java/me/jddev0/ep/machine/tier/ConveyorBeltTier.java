package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.*;
import me.jddev0.ep.block.entity.*;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.screen.ItemConveyorBeltLoaderMenu;
import me.jddev0.ep.screen.ItemConveyorBeltSorterMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public enum ConveyorBeltTier {
    BASIC, FAST, EXPRESS;

    public ItemConveyorBeltBlock getItemConveyorBeltBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT.get();
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT.get();
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltBlockEntity> getItemConveyorBeltBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_ENTITY.get();
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_ENTITY.get();
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_ENTITY.get();
        };
    }

    public ItemConveyorBeltLoaderBlock getItemConveyorBeltLoaderBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER.get();
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER.get();
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltLoaderBlockEntity> getItemConveyorBeltLoaderBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_LOADER_ENTITY.get();
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_LOADER_ENTITY.get();
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ENTITY.get();
        };
    }

    public MenuType<ItemConveyorBeltLoaderMenu> getItemConveyorBeltLoaderMenuTypeFromTier() {
        return switch(this) {
            case BASIC -> EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_LOADER_MENU.get();
            case FAST -> EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_LOADER_MENU.get();
            case EXPRESS -> EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_MENU.get();
        };
    }

    public ItemConveyorBeltMergerBlock getItemConveyorBeltMergerBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER.get();
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER.get();
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltMergerBlockEntity> getItemConveyorBeltMergerBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_MERGER_ENTITY.get();
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_MERGER_ENTITY.get();
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ENTITY.get();
        };
    }

    public ItemConveyorBeltSorterBlock getItemConveyorBeltSorterBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER.get();
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER.get();
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltSorterBlockEntity> getItemConveyorBeltSorterBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_SORTER_ENTITY.get();
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_SORTER_ENTITY.get();
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ENTITY.get();
        };
    }

    public MenuType<ItemConveyorBeltSorterMenu> getItemConveyorBeltSorterMenuTypeFromTier() {
        return switch(this) {
            case BASIC -> EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_SORTER_MENU.get();
            case FAST -> EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_SORTER_MENU.get();
            case EXPRESS -> EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_MENU.get();
        };
    }

    public ItemConveyorBeltSplitterBlock getItemConveyorBeltSplitterBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER.get();
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER.get();
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltSplitterBlockEntity> getItemConveyorBeltSplitterBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY.get();
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY.get();
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY.get();
        };
    }

    public ItemConveyorBeltSwitchBlock getItemConveyorBeltSwitchBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH.get();
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH.get();
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH.get();
        };
    }

    public BlockEntityType<ItemConveyorBeltSwitchBlockEntity> getItemConveyorBeltSwitchBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ENTITY.get();
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_SWITCH_ENTITY.get();
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ENTITY.get();
        };
    }
}
