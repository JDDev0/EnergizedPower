package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.registry.tags.CommonBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(DataGenerator output, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, EPAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_AXE).
                add(EPBlocks.SAWDUST_BLOCK.get());

        tag(BlockTags.PREVENT_MOB_SPAWNING_INSIDE).
                add(EPBlocks.ITEM_CONVEYOR_BELT.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).
                add(
                        EPBlocks.SILICON_BLOCK.get(),
                        EPBlocks.TIN_BLOCK.get(),

                        EPBlocks.TIN_ORE.get(),
                        EPBlocks.DEEPSLATE_TIN_ORE.get(),

                        EPBlocks.RAW_TIN_BLOCK.get(),

                        EPBlocks.ITEM_CONVEYOR_BELT.get(),
                        EPBlocks.ITEM_CONVEYOR_BELT_LOADER.get(),
                        EPBlocks.ITEM_CONVEYOR_BELT_SORTER.get(),
                        EPBlocks.ITEM_CONVEYOR_BELT_SWITCH.get(),
                        EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER.get(),
                        EPBlocks.ITEM_CONVEYOR_BELT_MERGER.get(),

                        EPBlocks.IRON_FLUID_PIPE.get(),
                        EPBlocks.GOLDEN_FLUID_PIPE.get(),

                        EPBlocks.FLUID_TANK_SMALL.get(),
                        EPBlocks.FLUID_TANK_MEDIUM.get(),
                        EPBlocks.FLUID_TANK_LARGE.get(),

                        EPBlocks.AUTO_CRAFTER.get(),
                        EPBlocks.ADVANCED_AUTO_CRAFTER.get(),

                        EPBlocks.PRESS_MOLD_MAKER.get(),

                        EPBlocks.ALLOY_FURNACE.get(),

                        EPBlocks.CHARGER.get(),
                        EPBlocks.ADVANCED_CHARGER.get(),

                        EPBlocks.UNCHARGER.get(),
                        EPBlocks.ADVANCED_UNCHARGER.get(),

                        EPBlocks.MINECART_CHARGER.get(),
                        EPBlocks.ADVANCED_MINECART_CHARGER.get(),

                        EPBlocks.MINECART_UNCHARGER.get(),
                        EPBlocks.ADVANCED_MINECART_UNCHARGER.get(),

                        EPBlocks.SOLAR_PANEL_1.get(),
                        EPBlocks.SOLAR_PANEL_2.get(),
                        EPBlocks.SOLAR_PANEL_3.get(),
                        EPBlocks.SOLAR_PANEL_4.get(),
                        EPBlocks.SOLAR_PANEL_5.get(),
                        EPBlocks.SOLAR_PANEL_6.get(),

                        EPBlocks.COAL_ENGINE.get(),

                        EPBlocks.HEAT_GENERATOR.get(),

                        EPBlocks.THERMAL_GENERATOR.get(),

                        EPBlocks.POWERED_FURNACE.get(),
                        EPBlocks.ADVANCED_POWERED_FURNACE.get(),

                        EPBlocks.LV_TRANSFORMER_1_TO_N.get(),
                        EPBlocks.LV_TRANSFORMER_3_TO_3.get(),
                        EPBlocks.LV_TRANSFORMER_N_TO_1.get(),
                        EPBlocks.MV_TRANSFORMER_1_TO_N.get(),
                        EPBlocks.MV_TRANSFORMER_3_TO_3.get(),
                        EPBlocks.MV_TRANSFORMER_N_TO_1.get(),
                        EPBlocks.HV_TRANSFORMER_1_TO_N.get(),
                        EPBlocks.HV_TRANSFORMER_3_TO_3.get(),
                        EPBlocks.HV_TRANSFORMER_N_TO_1.get(),
                        EPBlocks.EHV_TRANSFORMER_1_TO_N.get(),
                        EPBlocks.EHV_TRANSFORMER_3_TO_3.get(),
                        EPBlocks.EHV_TRANSFORMER_N_TO_1.get(),

                        EPBlocks.BATTERY_BOX.get(),
                        EPBlocks.ADVANCED_BATTERY_BOX.get(),

                        EPBlocks.CRUSHER.get(),
                        EPBlocks.ADVANCED_CRUSHER.get(),

                        EPBlocks.PULVERIZER.get(),
                        EPBlocks.ADVANCED_PULVERIZER.get(),

                        EPBlocks.SAWMILL.get(),

                        EPBlocks.COMPRESSOR.get(),

                        EPBlocks.METAL_PRESS.get(),

                        EPBlocks.AUTO_PRESS_MOLD_MAKER.get(),

                        EPBlocks.AUTO_STONECUTTER.get(),

                        EPBlocks.ASSEMBLING_MACHINE.get(),

                        EPBlocks.INDUCTION_SMELTER.get(),

                        EPBlocks.PLANT_GROWTH_CHAMBER.get(),

                        EPBlocks.BLOCK_PLACER.get(),

                        EPBlocks.FLUID_FILLER.get(),

                        EPBlocks.FLUID_DRAINER.get(),

                        EPBlocks.FLUID_PUMP.get(),

                        EPBlocks.DRAIN.get(),

                        EPBlocks.STONE_SOLIDIFIER.get(),

                        EPBlocks.FILTRATION_PLANT.get(),

                        EPBlocks.FLUID_TRANSPOSER.get(),

                        EPBlocks.LIGHTNING_GENERATOR.get(),

                        EPBlocks.ENERGIZER.get(),

                        EPBlocks.CHARGING_STATION.get(),

                        EPBlocks.CRYSTAL_GROWTH_CHAMBER.get(),

                        EPBlocks.WEATHER_CONTROLLER.get(),

                        EPBlocks.TIME_CONTROLLER.get(),

                        EPBlocks.TELEPORTER.get(),

                        EPBlocks.BASIC_MACHINE_FRAME.get(),
                        EPBlocks.HARDENED_MACHINE_FRAME.get(),
                        EPBlocks.ADVANCED_MACHINE_FRAME.get(),
                        EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME.get()
                );

        tag(BlockTags.NEEDS_STONE_TOOL).
                add(
                        EPBlocks.SILICON_BLOCK.get(),
                        EPBlocks.TIN_BLOCK.get(),

                        EPBlocks.TIN_ORE.get(),
                        EPBlocks.DEEPSLATE_TIN_ORE.get(),

                        EPBlocks.RAW_TIN_BLOCK.get(),

                        EPBlocks.IRON_FLUID_PIPE.get(),
                        EPBlocks.GOLDEN_FLUID_PIPE.get(),

                        EPBlocks.FLUID_TANK_SMALL.get(),
                        EPBlocks.FLUID_TANK_MEDIUM.get(),
                        EPBlocks.FLUID_TANK_LARGE.get(),

                        EPBlocks.AUTO_CRAFTER.get(),
                        EPBlocks.ADVANCED_AUTO_CRAFTER.get(),

                        EPBlocks.CHARGER.get(),
                        EPBlocks.ADVANCED_CHARGER.get(),

                        EPBlocks.UNCHARGER.get(),
                        EPBlocks.ADVANCED_UNCHARGER.get(),

                        EPBlocks.MINECART_CHARGER.get(),
                        EPBlocks.ADVANCED_MINECART_CHARGER.get(),

                        EPBlocks.MINECART_UNCHARGER.get(),
                        EPBlocks.ADVANCED_MINECART_UNCHARGER.get(),

                        EPBlocks.SOLAR_PANEL_1.get(),
                        EPBlocks.SOLAR_PANEL_2.get(),
                        EPBlocks.SOLAR_PANEL_3.get(),
                        EPBlocks.SOLAR_PANEL_4.get(),
                        EPBlocks.SOLAR_PANEL_5.get(),
                        EPBlocks.SOLAR_PANEL_6.get(),

                        EPBlocks.COAL_ENGINE.get(),

                        EPBlocks.HEAT_GENERATOR.get(),

                        EPBlocks.THERMAL_GENERATOR.get(),

                        EPBlocks.POWERED_FURNACE.get(),
                        EPBlocks.ADVANCED_POWERED_FURNACE.get(),

                        EPBlocks.LV_TRANSFORMER_1_TO_N.get(),
                        EPBlocks.LV_TRANSFORMER_3_TO_3.get(),
                        EPBlocks.LV_TRANSFORMER_N_TO_1.get(),
                        EPBlocks.MV_TRANSFORMER_1_TO_N.get(),
                        EPBlocks.MV_TRANSFORMER_3_TO_3.get(),
                        EPBlocks.MV_TRANSFORMER_N_TO_1.get(),
                        EPBlocks.HV_TRANSFORMER_1_TO_N.get(),
                        EPBlocks.HV_TRANSFORMER_3_TO_3.get(),
                        EPBlocks.HV_TRANSFORMER_N_TO_1.get(),
                        EPBlocks.EHV_TRANSFORMER_1_TO_N.get(),
                        EPBlocks.EHV_TRANSFORMER_3_TO_3.get(),
                        EPBlocks.EHV_TRANSFORMER_N_TO_1.get(),

                        EPBlocks.BATTERY_BOX.get(),
                        EPBlocks.ADVANCED_BATTERY_BOX.get(),

                        EPBlocks.CRUSHER.get(),
                        EPBlocks.ADVANCED_CRUSHER.get(),

                        EPBlocks.PULVERIZER.get(),
                        EPBlocks.ADVANCED_PULVERIZER.get(),

                        EPBlocks.SAWMILL.get(),

                        EPBlocks.COMPRESSOR.get(),

                        EPBlocks.METAL_PRESS.get(),

                        EPBlocks.AUTO_PRESS_MOLD_MAKER.get(),

                        EPBlocks.AUTO_STONECUTTER.get(),

                        EPBlocks.ASSEMBLING_MACHINE.get(),

                        EPBlocks.INDUCTION_SMELTER.get(),

                        EPBlocks.PLANT_GROWTH_CHAMBER.get(),

                        EPBlocks.BLOCK_PLACER.get(),

                        EPBlocks.FLUID_FILLER.get(),

                        EPBlocks.FLUID_DRAINER.get(),

                        EPBlocks.FLUID_PUMP.get(),

                        EPBlocks.DRAIN.get(),

                        EPBlocks.STONE_SOLIDIFIER.get(),

                        EPBlocks.FILTRATION_PLANT.get(),

                        EPBlocks.FLUID_TRANSPOSER.get(),

                        EPBlocks.LIGHTNING_GENERATOR.get(),

                        EPBlocks.ENERGIZER.get(),

                        EPBlocks.CHARGING_STATION.get(),

                        EPBlocks.CRYSTAL_GROWTH_CHAMBER.get(),

                        EPBlocks.WEATHER_CONTROLLER.get(),

                        EPBlocks.TIME_CONTROLLER.get(),

                        EPBlocks.TELEPORTER.get(),

                        EPBlocks.BASIC_MACHINE_FRAME.get(),
                        EPBlocks.HARDENED_MACHINE_FRAME.get(),
                        EPBlocks.ADVANCED_MACHINE_FRAME.get(),
                        EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME.get()
                );

        tag(Tags.Blocks.ORES).
                addTag(CommonBlockTags.ORES_TIN);
        tag(CommonBlockTags.ORES_TIN).
                add(EPBlocks.TIN_ORE.get(),
                        EPBlocks.DEEPSLATE_TIN_ORE.get());

        tag(Tags.Blocks.ORES_IN_GROUND_STONE).
                add(EPBlocks.TIN_ORE.get());
        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).
                add(EPBlocks.DEEPSLATE_TIN_ORE.get());

        tag(Tags.Blocks.STORAGE_BLOCKS).
                addTag(CommonBlockTags.STORAGE_BLOCKS_SILICON).
                addTag(CommonBlockTags.STORAGE_BLOCKS_TIN).
                addTag(CommonBlockTags.STORAGE_BLOCKS_RAW_TIN);
        tag(CommonBlockTags.STORAGE_BLOCKS_SILICON).
                add(EPBlocks.SILICON_BLOCK.get());
        tag(CommonBlockTags.STORAGE_BLOCKS_TIN).
                add(EPBlocks.TIN_BLOCK.get());
        tag(CommonBlockTags.STORAGE_BLOCKS_RAW_TIN).
                add(EPBlocks.RAW_TIN_BLOCK.get());
    }
}
