package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.*;
import me.jddev0.ep.block.entity.*;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.screen.ItemConveyorBeltLoaderMenu;
import me.jddev0.ep.screen.ItemConveyorBeltSorterMenu;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandlerType;

public enum ConveyorBeltTier {
    BASIC, FAST, EXPRESS;

    public ItemConveyorBeltBlock getItemConveyorBeltBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT;
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT;
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT;
        };
    }

    public BlockEntityType<ItemConveyorBeltBlockEntity> getItemConveyorBeltBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_ENTITY;
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_ENTITY;
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_ENTITY;
        };
    }

    public ItemConveyorBeltLoaderBlock getItemConveyorBeltLoaderBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER;
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER;
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER;
        };
    }

    public BlockEntityType<ItemConveyorBeltLoaderBlockEntity> getItemConveyorBeltLoaderBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_LOADER_ENTITY;
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_LOADER_ENTITY;
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ENTITY;
        };
    }

    public ScreenHandlerType<ItemConveyorBeltLoaderMenu> getItemConveyorBeltLoaderMenuTypeFromTier() {
        return switch(this) {
            case BASIC -> EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_LOADER_MENU;
            case FAST -> EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_LOADER_MENU;
            case EXPRESS -> EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_MENU;
        };
    }

    public ItemConveyorBeltMergerBlock getItemConveyorBeltMergerBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER;
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER;
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER;
        };
    }

    public BlockEntityType<ItemConveyorBeltMergerBlockEntity> getItemConveyorBeltMergerBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_MERGER_ENTITY;
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_MERGER_ENTITY;
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ENTITY;
        };
    }

    public ItemConveyorBeltSorterBlock getItemConveyorBeltSorterBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER;
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER;
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER;
        };
    }

    public BlockEntityType<ItemConveyorBeltSorterBlockEntity> getItemConveyorBeltSorterBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_SORTER_ENTITY;
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_SORTER_ENTITY;
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ENTITY;
        };
    }

    public ScreenHandlerType<ItemConveyorBeltSorterMenu> getItemConveyorBeltSorterMenuTypeFromTier() {
        return switch(this) {
            case BASIC -> EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_SORTER_MENU;
            case FAST -> EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_SORTER_MENU;
            case EXPRESS -> EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_MENU;
        };
    }

    public ItemConveyorBeltSplitterBlock getItemConveyorBeltSplitterBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER;
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER;
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER;
        };
    }

    public BlockEntityType<ItemConveyorBeltSplitterBlockEntity> getItemConveyorBeltSplitterBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY;
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY;
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY;
        };
    }

    public ItemConveyorBeltSwitchBlock getItemConveyorBeltSwitchBlockFromTier() {
        return switch(this) {
            case BASIC -> EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH;
            case FAST -> EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH;
            case EXPRESS -> EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH;
        };
    }

    public BlockEntityType<ItemConveyorBeltSwitchBlockEntity> getItemConveyorBeltSwitchBlockEntityFromTier() {
        return switch(this) {
            case BASIC -> EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ENTITY;
            case FAST -> EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_SWITCH_ENTITY;
            case EXPRESS -> EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ENTITY;
        };
    }
}
