package me.jddev0.ep.datagen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.registry.tags.CommonBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.tag.BlockTags;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataGenerator output) {
        super(output);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).
                add(EPBlocks.SAWDUST_BLOCK);

        getOrCreateTagBuilder(BlockTags.PREVENT_MOB_SPAWNING_INSIDE).
                add(EPBlocks.ITEM_CONVEYOR_BELT);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).
                add(
                        EPBlocks.SILICON_BLOCK,
                        EPBlocks.TIN_BLOCK,

                        EPBlocks.TIN_ORE,
                        EPBlocks.DEEPSLATE_TIN_ORE,

                        EPBlocks.RAW_TIN_BLOCK,

                        EPBlocks.ITEM_CONVEYOR_BELT,
                        EPBlocks.ITEM_CONVEYOR_BELT_LOADER,
                        EPBlocks.ITEM_CONVEYOR_BELT_SORTER,
                        EPBlocks.ITEM_CONVEYOR_BELT_SWITCH,
                        EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER,
                        EPBlocks.ITEM_CONVEYOR_BELT_MERGER,

                        EPBlocks.IRON_FLUID_PIPE,
                        EPBlocks.GOLDEN_FLUID_PIPE,

                        EPBlocks.FLUID_TANK_SMALL,
                        EPBlocks.FLUID_TANK_MEDIUM,
                        EPBlocks.FLUID_TANK_LARGE,

                        EPBlocks.AUTO_CRAFTER,
                        EPBlocks.ADVANCED_AUTO_CRAFTER,

                        EPBlocks.PRESS_MOLD_MAKER,

                        EPBlocks.ALLOY_FURNACE,

                        EPBlocks.CHARGER,
                        EPBlocks.ADVANCED_CHARGER,

                        EPBlocks.UNCHARGER,
                        EPBlocks.ADVANCED_UNCHARGER,

                        EPBlocks.MINECART_CHARGER,
                        EPBlocks.ADVANCED_MINECART_CHARGER,

                        EPBlocks.MINECART_UNCHARGER,
                        EPBlocks.ADVANCED_MINECART_UNCHARGER,

                        EPBlocks.SOLAR_PANEL_1,
                        EPBlocks.SOLAR_PANEL_2,
                        EPBlocks.SOLAR_PANEL_3,
                        EPBlocks.SOLAR_PANEL_4,
                        EPBlocks.SOLAR_PANEL_5,
                        EPBlocks.SOLAR_PANEL_6,

                        EPBlocks.COAL_ENGINE,

                        EPBlocks.HEAT_GENERATOR,

                        EPBlocks.THERMAL_GENERATOR,

                        EPBlocks.POWERED_FURNACE,
                        EPBlocks.ADVANCED_POWERED_FURNACE,

                        EPBlocks.LV_TRANSFORMER_1_TO_N,
                        EPBlocks.LV_TRANSFORMER_3_TO_3,
                        EPBlocks.LV_TRANSFORMER_N_TO_1,
                        EPBlocks.MV_TRANSFORMER_1_TO_N,
                        EPBlocks.MV_TRANSFORMER_3_TO_3,
                        EPBlocks.MV_TRANSFORMER_N_TO_1,
                        EPBlocks.HV_TRANSFORMER_1_TO_N,
                        EPBlocks.HV_TRANSFORMER_3_TO_3,
                        EPBlocks.HV_TRANSFORMER_N_TO_1,
                        EPBlocks.EHV_TRANSFORMER_1_TO_N,
                        EPBlocks.EHV_TRANSFORMER_3_TO_3,
                        EPBlocks.EHV_TRANSFORMER_N_TO_1,

                        EPBlocks.BATTERY_BOX,
                        EPBlocks.ADVANCED_BATTERY_BOX,

                        EPBlocks.CRUSHER,
                        EPBlocks.ADVANCED_CRUSHER,

                        EPBlocks.PULVERIZER,
                        EPBlocks.ADVANCED_PULVERIZER,

                        EPBlocks.SAWMILL,

                        EPBlocks.COMPRESSOR,

                        EPBlocks.METAL_PRESS,

                        EPBlocks.AUTO_PRESS_MOLD_MAKER,

                        EPBlocks.AUTO_STONECUTTER,

                        EPBlocks.ASSEMBLING_MACHINE,

                        EPBlocks.INDUCTION_SMELTER,

                        EPBlocks.PLANT_GROWTH_CHAMBER,

                        EPBlocks.BLOCK_PLACER,

                        EPBlocks.FLUID_FILLER,

                        EPBlocks.FLUID_DRAINER,

                        EPBlocks.FLUID_PUMP,

                        EPBlocks.DRAIN,

                        EPBlocks.STONE_SOLIDIFIER,

                        EPBlocks.FILTRATION_PLANT,

                        EPBlocks.FLUID_TRANSPOSER,

                        EPBlocks.LIGHTNING_GENERATOR,

                        EPBlocks.ENERGIZER,

                        EPBlocks.CHARGING_STATION,

                        EPBlocks.CRYSTAL_GROWTH_CHAMBER,

                        EPBlocks.WEATHER_CONTROLLER,

                        EPBlocks.TIME_CONTROLLER,

                        EPBlocks.TELEPORTER,

                        EPBlocks.BASIC_MACHINE_FRAME,
                        EPBlocks.HARDENED_MACHINE_FRAME,
                        EPBlocks.ADVANCED_MACHINE_FRAME,
                        EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME
                );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).
                add(
                        EPBlocks.SILICON_BLOCK,
                        EPBlocks.TIN_BLOCK,

                        EPBlocks.TIN_ORE,
                        EPBlocks.DEEPSLATE_TIN_ORE,

                        EPBlocks.RAW_TIN_BLOCK,

                        EPBlocks.IRON_FLUID_PIPE,
                        EPBlocks.GOLDEN_FLUID_PIPE,

                        EPBlocks.FLUID_TANK_SMALL,
                        EPBlocks.FLUID_TANK_MEDIUM,
                        EPBlocks.FLUID_TANK_LARGE,

                        EPBlocks.AUTO_CRAFTER,
                        EPBlocks.ADVANCED_AUTO_CRAFTER,

                        EPBlocks.CHARGER,
                        EPBlocks.ADVANCED_CHARGER,

                        EPBlocks.UNCHARGER,
                        EPBlocks.ADVANCED_UNCHARGER,

                        EPBlocks.MINECART_CHARGER,
                        EPBlocks.ADVANCED_MINECART_CHARGER,

                        EPBlocks.MINECART_UNCHARGER,
                        EPBlocks.ADVANCED_MINECART_UNCHARGER,

                        EPBlocks.SOLAR_PANEL_1,
                        EPBlocks.SOLAR_PANEL_2,
                        EPBlocks.SOLAR_PANEL_3,
                        EPBlocks.SOLAR_PANEL_4,
                        EPBlocks.SOLAR_PANEL_5,
                        EPBlocks.SOLAR_PANEL_6,

                        EPBlocks.COAL_ENGINE,

                        EPBlocks.HEAT_GENERATOR,

                        EPBlocks.THERMAL_GENERATOR,

                        EPBlocks.POWERED_FURNACE,
                        EPBlocks.ADVANCED_POWERED_FURNACE,

                        EPBlocks.LV_TRANSFORMER_1_TO_N,
                        EPBlocks.LV_TRANSFORMER_3_TO_3,
                        EPBlocks.LV_TRANSFORMER_N_TO_1,
                        EPBlocks.MV_TRANSFORMER_1_TO_N,
                        EPBlocks.MV_TRANSFORMER_3_TO_3,
                        EPBlocks.MV_TRANSFORMER_N_TO_1,
                        EPBlocks.HV_TRANSFORMER_1_TO_N,
                        EPBlocks.HV_TRANSFORMER_3_TO_3,
                        EPBlocks.HV_TRANSFORMER_N_TO_1,
                        EPBlocks.EHV_TRANSFORMER_1_TO_N,
                        EPBlocks.EHV_TRANSFORMER_3_TO_3,
                        EPBlocks.EHV_TRANSFORMER_N_TO_1,

                        EPBlocks.BATTERY_BOX,
                        EPBlocks.ADVANCED_BATTERY_BOX,

                        EPBlocks.CRUSHER,
                        EPBlocks.ADVANCED_CRUSHER,

                        EPBlocks.PULVERIZER,
                        EPBlocks.ADVANCED_PULVERIZER,

                        EPBlocks.SAWMILL,

                        EPBlocks.COMPRESSOR,

                        EPBlocks.METAL_PRESS,

                        EPBlocks.AUTO_PRESS_MOLD_MAKER,

                        EPBlocks.AUTO_STONECUTTER,

                        EPBlocks.ASSEMBLING_MACHINE,

                        EPBlocks.INDUCTION_SMELTER,

                        EPBlocks.PLANT_GROWTH_CHAMBER,

                        EPBlocks.BLOCK_PLACER,

                        EPBlocks.FLUID_FILLER,

                        EPBlocks.FLUID_DRAINER,

                        EPBlocks.FLUID_PUMP,

                        EPBlocks.DRAIN,

                        EPBlocks.STONE_SOLIDIFIER,

                        EPBlocks.FILTRATION_PLANT,

                        EPBlocks.FLUID_TRANSPOSER,

                        EPBlocks.LIGHTNING_GENERATOR,

                        EPBlocks.ENERGIZER,

                        EPBlocks.CHARGING_STATION,

                        EPBlocks.CRYSTAL_GROWTH_CHAMBER,

                        EPBlocks.WEATHER_CONTROLLER,

                        EPBlocks.TIME_CONTROLLER,

                        EPBlocks.TELEPORTER,

                        EPBlocks.BASIC_MACHINE_FRAME,
                        EPBlocks.HARDENED_MACHINE_FRAME,
                        EPBlocks.ADVANCED_MACHINE_FRAME,
                        EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME
                );

        getOrCreateTagBuilder(ConventionalBlockTags.ORES).
                addTag(CommonBlockTags.TIN_ORES);
        getOrCreateTagBuilder(CommonBlockTags.TIN_ORES).
                add(EPBlocks.TIN_ORE,
                        EPBlocks.DEEPSLATE_TIN_ORE);

        getOrCreateTagBuilder(CommonBlockTags.SILICON_BLOCKS).
                add(EPBlocks.SILICON_BLOCK);
        getOrCreateTagBuilder(CommonBlockTags.TIN_BLOCKS).
                add(EPBlocks.TIN_BLOCK);
        getOrCreateTagBuilder(CommonBlockTags.RAW_TIN_BLOCKS).
                add(EPBlocks.RAW_TIN_BLOCK);
    }
}
