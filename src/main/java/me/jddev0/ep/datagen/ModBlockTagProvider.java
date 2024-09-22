package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.registry.tags.CommonBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(DataGenerator output, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, EnergizedPowerMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_AXE).
                add(ModBlocks.SAWDUST_BLOCK.get());

        tag(BlockTags.PREVENT_MOB_SPAWNING_INSIDE).
                add(ModBlocks.ITEM_CONVEYOR_BELT.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).
                add(
                        ModBlocks.SILICON_BLOCK.get(),
                        ModBlocks.TIN_BLOCK.get(),

                        ModBlocks.TIN_ORE.get(),
                        ModBlocks.DEEPSLATE_TIN_ORE.get(),

                        ModBlocks.RAW_TIN_BLOCK.get(),

                        ModBlocks.ITEM_CONVEYOR_BELT.get(),
                        ModBlocks.ITEM_CONVEYOR_BELT_LOADER.get(),
                        ModBlocks.ITEM_CONVEYOR_BELT_SORTER.get(),
                        ModBlocks.ITEM_CONVEYOR_BELT_SWITCH.get(),
                        ModBlocks.ITEM_CONVEYOR_BELT_SPLITTER.get(),
                        ModBlocks.ITEM_CONVEYOR_BELT_MERGER.get(),

                        ModBlocks.IRON_FLUID_PIPE.get(),
                        ModBlocks.GOLDEN_FLUID_PIPE.get(),

                        ModBlocks.FLUID_TANK_SMALL.get(),
                        ModBlocks.FLUID_TANK_MEDIUM.get(),
                        ModBlocks.FLUID_TANK_LARGE.get(),

                        ModBlocks.AUTO_CRAFTER.get(),
                        ModBlocks.ADVANCED_AUTO_CRAFTER.get(),

                        ModBlocks.PRESS_MOLD_MAKER.get(),

                        ModBlocks.ALLOY_FURNACE.get(),

                        ModBlocks.CHARGER.get(),
                        ModBlocks.ADVANCED_CHARGER.get(),

                        ModBlocks.UNCHARGER.get(),
                        ModBlocks.ADVANCED_UNCHARGER.get(),

                        ModBlocks.MINECART_CHARGER.get(),
                        ModBlocks.ADVANCED_MINECART_CHARGER.get(),

                        ModBlocks.MINECART_UNCHARGER.get(),
                        ModBlocks.ADVANCED_MINECART_UNCHARGER.get(),

                        ModBlocks.SOLAR_PANEL_1.get(),
                        ModBlocks.SOLAR_PANEL_2.get(),
                        ModBlocks.SOLAR_PANEL_3.get(),
                        ModBlocks.SOLAR_PANEL_4.get(),
                        ModBlocks.SOLAR_PANEL_5.get(),
                        ModBlocks.SOLAR_PANEL_6.get(),

                        ModBlocks.COAL_ENGINE.get(),

                        ModBlocks.HEAT_GENERATOR.get(),

                        ModBlocks.THERMAL_GENERATOR.get(),

                        ModBlocks.POWERED_FURNACE.get(),
                        ModBlocks.ADVANCED_POWERED_FURNACE.get(),

                        ModBlocks.LV_TRANSFORMER_1_TO_N.get(),
                        ModBlocks.LV_TRANSFORMER_3_TO_3.get(),
                        ModBlocks.LV_TRANSFORMER_N_TO_1.get(),
                        ModBlocks.MV_TRANSFORMER_1_TO_N.get(),
                        ModBlocks.MV_TRANSFORMER_3_TO_3.get(),
                        ModBlocks.MV_TRANSFORMER_N_TO_1.get(),
                        ModBlocks.HV_TRANSFORMER_1_TO_N.get(),
                        ModBlocks.HV_TRANSFORMER_3_TO_3.get(),
                        ModBlocks.HV_TRANSFORMER_N_TO_1.get(),
                        ModBlocks.EHV_TRANSFORMER_1_TO_N.get(),
                        ModBlocks.EHV_TRANSFORMER_3_TO_3.get(),
                        ModBlocks.EHV_TRANSFORMER_N_TO_1.get(),

                        ModBlocks.BATTERY_BOX.get(),
                        ModBlocks.ADVANCED_BATTERY_BOX.get(),

                        ModBlocks.CRUSHER.get(),
                        ModBlocks.ADVANCED_CRUSHER.get(),

                        ModBlocks.PULVERIZER.get(),
                        ModBlocks.ADVANCED_PULVERIZER.get(),

                        ModBlocks.SAWMILL.get(),

                        ModBlocks.COMPRESSOR.get(),

                        ModBlocks.METAL_PRESS.get(),

                        ModBlocks.AUTO_PRESS_MOLD_MAKER.get(),

                        ModBlocks.AUTO_STONECUTTER.get(),

                        ModBlocks.ASSEMBLING_MACHINE.get(),

                        ModBlocks.INDUCTION_SMELTER.get(),

                        ModBlocks.PLANT_GROWTH_CHAMBER.get(),

                        ModBlocks.BLOCK_PLACER.get(),

                        ModBlocks.FLUID_FILLER.get(),

                        ModBlocks.FLUID_DRAINER.get(),

                        ModBlocks.FLUID_PUMP.get(),

                        ModBlocks.DRAIN.get(),

                        ModBlocks.STONE_SOLIDIFIER.get(),

                        ModBlocks.FILTRATION_PLANT.get(),

                        ModBlocks.FLUID_TRANSPOSER.get(),

                        ModBlocks.LIGHTNING_GENERATOR.get(),

                        ModBlocks.ENERGIZER.get(),

                        ModBlocks.CHARGING_STATION.get(),

                        ModBlocks.CRYSTAL_GROWTH_CHAMBER.get(),

                        ModBlocks.WEATHER_CONTROLLER.get(),

                        ModBlocks.TIME_CONTROLLER.get(),

                        ModBlocks.TELEPORTER.get(),

                        ModBlocks.BASIC_MACHINE_FRAME.get(),
                        ModBlocks.HARDENED_MACHINE_FRAME.get(),
                        ModBlocks.ADVANCED_MACHINE_FRAME.get(),
                        ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME.get()
                );

        tag(BlockTags.NEEDS_STONE_TOOL).
                add(
                        ModBlocks.SILICON_BLOCK.get(),
                        ModBlocks.TIN_BLOCK.get(),

                        ModBlocks.TIN_ORE.get(),
                        ModBlocks.DEEPSLATE_TIN_ORE.get(),

                        ModBlocks.RAW_TIN_BLOCK.get(),

                        ModBlocks.IRON_FLUID_PIPE.get(),
                        ModBlocks.GOLDEN_FLUID_PIPE.get(),

                        ModBlocks.FLUID_TANK_SMALL.get(),
                        ModBlocks.FLUID_TANK_MEDIUM.get(),
                        ModBlocks.FLUID_TANK_LARGE.get(),

                        ModBlocks.AUTO_CRAFTER.get(),
                        ModBlocks.ADVANCED_AUTO_CRAFTER.get(),

                        ModBlocks.CHARGER.get(),
                        ModBlocks.ADVANCED_CHARGER.get(),

                        ModBlocks.UNCHARGER.get(),
                        ModBlocks.ADVANCED_UNCHARGER.get(),

                        ModBlocks.MINECART_CHARGER.get(),
                        ModBlocks.ADVANCED_MINECART_CHARGER.get(),

                        ModBlocks.MINECART_UNCHARGER.get(),
                        ModBlocks.ADVANCED_MINECART_UNCHARGER.get(),

                        ModBlocks.SOLAR_PANEL_1.get(),
                        ModBlocks.SOLAR_PANEL_2.get(),
                        ModBlocks.SOLAR_PANEL_3.get(),
                        ModBlocks.SOLAR_PANEL_4.get(),
                        ModBlocks.SOLAR_PANEL_5.get(),
                        ModBlocks.SOLAR_PANEL_6.get(),

                        ModBlocks.COAL_ENGINE.get(),

                        ModBlocks.HEAT_GENERATOR.get(),

                        ModBlocks.THERMAL_GENERATOR.get(),

                        ModBlocks.POWERED_FURNACE.get(),
                        ModBlocks.ADVANCED_POWERED_FURNACE.get(),

                        ModBlocks.LV_TRANSFORMER_1_TO_N.get(),
                        ModBlocks.LV_TRANSFORMER_3_TO_3.get(),
                        ModBlocks.LV_TRANSFORMER_N_TO_1.get(),
                        ModBlocks.MV_TRANSFORMER_1_TO_N.get(),
                        ModBlocks.MV_TRANSFORMER_3_TO_3.get(),
                        ModBlocks.MV_TRANSFORMER_N_TO_1.get(),
                        ModBlocks.HV_TRANSFORMER_1_TO_N.get(),
                        ModBlocks.HV_TRANSFORMER_3_TO_3.get(),
                        ModBlocks.HV_TRANSFORMER_N_TO_1.get(),
                        ModBlocks.EHV_TRANSFORMER_1_TO_N.get(),
                        ModBlocks.EHV_TRANSFORMER_3_TO_3.get(),
                        ModBlocks.EHV_TRANSFORMER_N_TO_1.get(),

                        ModBlocks.BATTERY_BOX.get(),
                        ModBlocks.ADVANCED_BATTERY_BOX.get(),

                        ModBlocks.CRUSHER.get(),
                        ModBlocks.ADVANCED_CRUSHER.get(),

                        ModBlocks.PULVERIZER.get(),
                        ModBlocks.ADVANCED_PULVERIZER.get(),

                        ModBlocks.SAWMILL.get(),

                        ModBlocks.COMPRESSOR.get(),

                        ModBlocks.METAL_PRESS.get(),

                        ModBlocks.AUTO_PRESS_MOLD_MAKER.get(),

                        ModBlocks.AUTO_STONECUTTER.get(),

                        ModBlocks.ASSEMBLING_MACHINE.get(),

                        ModBlocks.INDUCTION_SMELTER.get(),

                        ModBlocks.PLANT_GROWTH_CHAMBER.get(),

                        ModBlocks.BLOCK_PLACER.get(),

                        ModBlocks.FLUID_FILLER.get(),

                        ModBlocks.FLUID_DRAINER.get(),

                        ModBlocks.FLUID_PUMP.get(),

                        ModBlocks.DRAIN.get(),

                        ModBlocks.STONE_SOLIDIFIER.get(),

                        ModBlocks.FILTRATION_PLANT.get(),

                        ModBlocks.FLUID_TRANSPOSER.get(),

                        ModBlocks.LIGHTNING_GENERATOR.get(),

                        ModBlocks.ENERGIZER.get(),

                        ModBlocks.CHARGING_STATION.get(),

                        ModBlocks.CRYSTAL_GROWTH_CHAMBER.get(),

                        ModBlocks.WEATHER_CONTROLLER.get(),

                        ModBlocks.TIME_CONTROLLER.get(),

                        ModBlocks.TELEPORTER.get(),

                        ModBlocks.BASIC_MACHINE_FRAME.get(),
                        ModBlocks.HARDENED_MACHINE_FRAME.get(),
                        ModBlocks.ADVANCED_MACHINE_FRAME.get(),
                        ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME.get()
                );

        tag(Tags.Blocks.ORES).
                addTag(CommonBlockTags.ORES_TIN);
        tag(CommonBlockTags.ORES_TIN).
                add(ModBlocks.TIN_ORE.get(),
                        ModBlocks.DEEPSLATE_TIN_ORE.get());

        tag(Tags.Blocks.ORES_IN_GROUND_STONE).
                add(ModBlocks.TIN_ORE.get());
        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).
                add(ModBlocks.DEEPSLATE_TIN_ORE.get());

        tag(Tags.Blocks.STORAGE_BLOCKS).
                addTag(CommonBlockTags.STORAGE_BLOCKS_SILICON).
                addTag(CommonBlockTags.STORAGE_BLOCKS_TIN).
                addTag(CommonBlockTags.STORAGE_BLOCKS_RAW_TIN);
        tag(CommonBlockTags.STORAGE_BLOCKS_SILICON).
                add(ModBlocks.SILICON_BLOCK.get());
        tag(CommonBlockTags.STORAGE_BLOCKS_TIN).
                add(ModBlocks.TIN_BLOCK.get());
        tag(CommonBlockTags.STORAGE_BLOCKS_RAW_TIN).
                add(ModBlocks.RAW_TIN_BLOCK.get());
    }
}
