package me.jddev0.ep.datagen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.registry.tags.CommonBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookupProvider) {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).
                add(ModBlocks.SAWDUST_BLOCK);

        getOrCreateTagBuilder(BlockTags.PREVENT_MOB_SPAWNING_INSIDE).
                add(ModBlocks.ITEM_CONVEYOR_BELT);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).
                add(
                        ModBlocks.SILICON_BLOCK,
                        ModBlocks.TIN_BLOCK,

                        ModBlocks.TIN_ORE,
                        ModBlocks.DEEPSLATE_TIN_ORE,

                        ModBlocks.RAW_TIN_BLOCK,

                        ModBlocks.ITEM_CONVEYOR_BELT,
                        ModBlocks.ITEM_CONVEYOR_BELT_LOADER,
                        ModBlocks.ITEM_CONVEYOR_BELT_SORTER,
                        ModBlocks.ITEM_CONVEYOR_BELT_SWITCH,
                        ModBlocks.ITEM_CONVEYOR_BELT_SPLITTER,
                        ModBlocks.ITEM_CONVEYOR_BELT_MERGER,

                        ModBlocks.IRON_FLUID_PIPE,
                        ModBlocks.GOLDEN_FLUID_PIPE,

                        ModBlocks.FLUID_TANK_SMALL,
                        ModBlocks.FLUID_TANK_MEDIUM,
                        ModBlocks.FLUID_TANK_LARGE,

                        ModBlocks.AUTO_CRAFTER,
                        ModBlocks.ADVANCED_AUTO_CRAFTER,

                        ModBlocks.PRESS_MOLD_MAKER,

                        ModBlocks.ALLOY_FURNACE,

                        ModBlocks.CHARGER,
                        ModBlocks.ADVANCED_CHARGER,

                        ModBlocks.UNCHARGER,
                        ModBlocks.ADVANCED_UNCHARGER,

                        ModBlocks.MINECART_CHARGER,
                        ModBlocks.ADVANCED_MINECART_CHARGER,

                        ModBlocks.MINECART_UNCHARGER,
                        ModBlocks.ADVANCED_MINECART_UNCHARGER,

                        ModBlocks.SOLAR_PANEL_1,
                        ModBlocks.SOLAR_PANEL_2,
                        ModBlocks.SOLAR_PANEL_3,
                        ModBlocks.SOLAR_PANEL_4,
                        ModBlocks.SOLAR_PANEL_5,
                        ModBlocks.SOLAR_PANEL_6,

                        ModBlocks.COAL_ENGINE,

                        ModBlocks.HEAT_GENERATOR,

                        ModBlocks.THERMAL_GENERATOR,

                        ModBlocks.POWERED_FURNACE,
                        ModBlocks.ADVANCED_POWERED_FURNACE,

                        ModBlocks.LV_TRANSFORMER_1_TO_N,
                        ModBlocks.LV_TRANSFORMER_3_TO_3,
                        ModBlocks.LV_TRANSFORMER_N_TO_1,
                        ModBlocks.MV_TRANSFORMER_1_TO_N,
                        ModBlocks.MV_TRANSFORMER_3_TO_3,
                        ModBlocks.MV_TRANSFORMER_N_TO_1,
                        ModBlocks.HV_TRANSFORMER_1_TO_N,
                        ModBlocks.HV_TRANSFORMER_3_TO_3,
                        ModBlocks.HV_TRANSFORMER_N_TO_1,
                        ModBlocks.EHV_TRANSFORMER_1_TO_N,
                        ModBlocks.EHV_TRANSFORMER_3_TO_3,
                        ModBlocks.EHV_TRANSFORMER_N_TO_1,

                        ModBlocks.BATTERY_BOX,
                        ModBlocks.ADVANCED_BATTERY_BOX,

                        ModBlocks.CRUSHER,
                        ModBlocks.ADVANCED_CRUSHER,

                        ModBlocks.PULVERIZER,
                        ModBlocks.ADVANCED_PULVERIZER,

                        ModBlocks.SAWMILL,

                        ModBlocks.COMPRESSOR,

                        ModBlocks.METAL_PRESS,

                        ModBlocks.AUTO_PRESS_MOLD_MAKER,

                        ModBlocks.AUTO_STONECUTTER,

                        ModBlocks.ASSEMBLING_MACHINE,

                        ModBlocks.INDUCTION_SMELTER,

                        ModBlocks.PLANT_GROWTH_CHAMBER,

                        ModBlocks.BLOCK_PLACER,

                        ModBlocks.FLUID_FILLER,

                        ModBlocks.FLUID_DRAINER,

                        ModBlocks.FLUID_PUMP,

                        ModBlocks.DRAIN,

                        ModBlocks.STONE_SOLIDIFIER,

                        ModBlocks.FILTRATION_PLANT,

                        ModBlocks.FLUID_TRANSPOSER,

                        ModBlocks.LIGHTNING_GENERATOR,

                        ModBlocks.ENERGIZER,

                        ModBlocks.CHARGING_STATION,

                        ModBlocks.CRYSTAL_GROWTH_CHAMBER,

                        ModBlocks.WEATHER_CONTROLLER,

                        ModBlocks.TIME_CONTROLLER,

                        ModBlocks.TELEPORTER,

                        ModBlocks.BASIC_MACHINE_FRAME,
                        ModBlocks.HARDENED_MACHINE_FRAME,
                        ModBlocks.ADVANCED_MACHINE_FRAME,
                        ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME
                );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).
                add(
                        ModBlocks.SILICON_BLOCK,
                        ModBlocks.TIN_BLOCK,

                        ModBlocks.TIN_ORE,
                        ModBlocks.DEEPSLATE_TIN_ORE,

                        ModBlocks.RAW_TIN_BLOCK,

                        ModBlocks.IRON_FLUID_PIPE,
                        ModBlocks.GOLDEN_FLUID_PIPE,

                        ModBlocks.FLUID_TANK_SMALL,
                        ModBlocks.FLUID_TANK_MEDIUM,
                        ModBlocks.FLUID_TANK_LARGE,

                        ModBlocks.AUTO_CRAFTER,
                        ModBlocks.ADVANCED_AUTO_CRAFTER,

                        ModBlocks.CHARGER,
                        ModBlocks.ADVANCED_CHARGER,

                        ModBlocks.UNCHARGER,
                        ModBlocks.ADVANCED_UNCHARGER,

                        ModBlocks.MINECART_CHARGER,
                        ModBlocks.ADVANCED_MINECART_CHARGER,

                        ModBlocks.MINECART_UNCHARGER,
                        ModBlocks.ADVANCED_MINECART_UNCHARGER,

                        ModBlocks.SOLAR_PANEL_1,
                        ModBlocks.SOLAR_PANEL_2,
                        ModBlocks.SOLAR_PANEL_3,
                        ModBlocks.SOLAR_PANEL_4,
                        ModBlocks.SOLAR_PANEL_5,
                        ModBlocks.SOLAR_PANEL_6,

                        ModBlocks.COAL_ENGINE,

                        ModBlocks.HEAT_GENERATOR,

                        ModBlocks.THERMAL_GENERATOR,

                        ModBlocks.POWERED_FURNACE,
                        ModBlocks.ADVANCED_POWERED_FURNACE,

                        ModBlocks.LV_TRANSFORMER_1_TO_N,
                        ModBlocks.LV_TRANSFORMER_3_TO_3,
                        ModBlocks.LV_TRANSFORMER_N_TO_1,
                        ModBlocks.MV_TRANSFORMER_1_TO_N,
                        ModBlocks.MV_TRANSFORMER_3_TO_3,
                        ModBlocks.MV_TRANSFORMER_N_TO_1,
                        ModBlocks.HV_TRANSFORMER_1_TO_N,
                        ModBlocks.HV_TRANSFORMER_3_TO_3,
                        ModBlocks.HV_TRANSFORMER_N_TO_1,
                        ModBlocks.EHV_TRANSFORMER_1_TO_N,
                        ModBlocks.EHV_TRANSFORMER_3_TO_3,
                        ModBlocks.EHV_TRANSFORMER_N_TO_1,

                        ModBlocks.BATTERY_BOX,
                        ModBlocks.ADVANCED_BATTERY_BOX,

                        ModBlocks.CRUSHER,
                        ModBlocks.ADVANCED_CRUSHER,

                        ModBlocks.PULVERIZER,
                        ModBlocks.ADVANCED_PULVERIZER,

                        ModBlocks.SAWMILL,

                        ModBlocks.COMPRESSOR,

                        ModBlocks.METAL_PRESS,

                        ModBlocks.AUTO_PRESS_MOLD_MAKER,

                        ModBlocks.AUTO_STONECUTTER,

                        ModBlocks.ASSEMBLING_MACHINE,

                        ModBlocks.INDUCTION_SMELTER,

                        ModBlocks.PLANT_GROWTH_CHAMBER,

                        ModBlocks.BLOCK_PLACER,

                        ModBlocks.FLUID_FILLER,

                        ModBlocks.FLUID_DRAINER,

                        ModBlocks.FLUID_PUMP,

                        ModBlocks.DRAIN,

                        ModBlocks.STONE_SOLIDIFIER,

                        ModBlocks.FILTRATION_PLANT,

                        ModBlocks.FLUID_TRANSPOSER,

                        ModBlocks.LIGHTNING_GENERATOR,

                        ModBlocks.ENERGIZER,

                        ModBlocks.CHARGING_STATION,

                        ModBlocks.CRYSTAL_GROWTH_CHAMBER,

                        ModBlocks.WEATHER_CONTROLLER,

                        ModBlocks.TIME_CONTROLLER,

                        ModBlocks.TELEPORTER,

                        ModBlocks.BASIC_MACHINE_FRAME,
                        ModBlocks.HARDENED_MACHINE_FRAME,
                        ModBlocks.ADVANCED_MACHINE_FRAME,
                        ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME
                );

        getOrCreateTagBuilder(ConventionalBlockTags.ORES).
                addTag(CommonBlockTags.ORES_TIN);
        getOrCreateTagBuilder(CommonBlockTags.ORES_TIN).
                add(ModBlocks.TIN_ORE,
                        ModBlocks.DEEPSLATE_TIN_ORE);

        getOrCreateTagBuilder(CommonBlockTags.ORES_IN_GROUND_STONE).
                add(ModBlocks.TIN_ORE);
        getOrCreateTagBuilder(CommonBlockTags.ORES_IN_GROUND_DEEPSLATE).
                add(ModBlocks.DEEPSLATE_TIN_ORE);

        getOrCreateTagBuilder(ConventionalBlockTags.STORAGE_BLOCKS).
                addTag(CommonBlockTags.STORAGE_BLOCKS_SILICON).
                addTag(CommonBlockTags.STORAGE_BLOCKS_TIN).
                addTag(CommonBlockTags.STORAGE_BLOCKS_RAW_TIN);
        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_SILICON).
                add(ModBlocks.SILICON_BLOCK);
        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_TIN).
                add(ModBlocks.TIN_BLOCK);
        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_RAW_TIN).
                add(ModBlocks.RAW_TIN_BLOCK);
    }
}
