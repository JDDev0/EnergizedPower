package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.*;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;

public class EnergizedPowerREIPlugin implements REICommonPlugin {
    @Override
    public String getPluginProviderName() {
        return "EnergizedPower";
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(ChargerRecipe.class).filterType(EPRecipes.CHARGER_TYPE).fill(ChargerDisplay::new);
        registry.beginRecipeFiller(CrusherRecipe.class).filterType(EPRecipes.CRUSHER_TYPE).fill(CrusherDisplay::new);
        registry.beginRecipeFiller(PulverizerRecipe.class).filterType(EPRecipes.PULVERIZER_TYPE).fill(PulverizerDisplay::new);
        registry.beginRecipeFiller(PulverizerRecipe.class).filterType(EPRecipes.PULVERIZER_TYPE).fill(AdvancedPulverizerDisplay::new);
        registry.beginRecipeFiller(SawmillRecipe.class).filterType(EPRecipes.SAWMILL_TYPE).fill(SawmillDisplay::new);
        registry.beginRecipeFiller(CompressorRecipe.class).filterType(EPRecipes.COMPRESSOR_TYPE).fill(CompressorDisplay::new);
        registry.beginRecipeFiller(MetalPressRecipe.class).filterType(EPRecipes.METAL_PRESS_TYPE).fill(MetalPressDisplay::new);
        registry.beginRecipeFiller(AssemblingMachineRecipe.class).filterType(EPRecipes.ASSEMBLING_MACHINE_TYPE).fill(AssemblingMachineDisplay::new);
        registry.beginRecipeFiller(PlantGrowthChamberRecipe.class).filterType(EPRecipes.PLANT_GROWTH_CHAMBER_TYPE).fill(PlantGrowthChamberDisplay::new);
        registry.beginRecipeFiller(PlantGrowthChamberFertilizerRecipe.class).filterType(EPRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_TYPE).fill(PlantGrowthChamberFertilizerDisplay::new);
        registry.beginRecipeFiller(EnergizerRecipe.class).filterType(EPRecipes.ENERGIZER_TYPE).fill(EnergizerDisplay::new);
        registry.beginRecipeFiller(CrystalGrowthChamberRecipe.class).filterType(EPRecipes.CRYSTAL_GROWTH_CHAMBER_TYPE).fill(CrystalGrowthChamberDisplay::new);
        registry.beginRecipeFiller(PressMoldMakerRecipe.class).filterType(EPRecipes.PRESS_MOLD_MAKER_TYPE).fill(PressMoldMakerDisplay::new);
        registry.beginRecipeFiller(AlloyFurnaceRecipe.class).filterType(EPRecipes.ALLOY_FURNACE_TYPE).fill(AlloyFurnaceDisplay::new);
        registry.beginRecipeFiller(StoneSolidifierRecipe.class).filterType(EPRecipes.STONE_SOLIDIFIER_TYPE).fill(StoneSolidifierDisplay::new);
        registry.beginRecipeFiller(FiltrationPlantRecipe.class).filterType(EPRecipes.FILTRATION_PLANT_TYPE).fill(FiltrationPlantDisplay::new);
        registry.beginRecipeFiller(FluidTransposerRecipe.class).filterType(EPRecipes.FLUID_TRANSPOSER_TYPE).fill(FluidTransposerDisplay::new);
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(ChargerDisplay.CATEGORY.getIdentifier(), ChargerDisplay.SERIALIZER);
        registry.register(CrusherDisplay.CATEGORY.getIdentifier(), CrusherDisplay.SERIALIZER);
        registry.register(PulverizerDisplay.CATEGORY.getIdentifier(), PulverizerDisplay.SERIALIZER);
        registry.register(AdvancedPulverizerDisplay.CATEGORY.getIdentifier(), AdvancedPulverizerDisplay.SERIALIZER);
        registry.register(SawmillDisplay.CATEGORY.getIdentifier(), SawmillDisplay.SERIALIZER);
        registry.register(CompressorDisplay.CATEGORY.getIdentifier(), CompressorDisplay.SERIALIZER);
        registry.register(MetalPressDisplay.CATEGORY.getIdentifier(), MetalPressDisplay.SERIALIZER);
        registry.register(AssemblingMachineDisplay.CATEGORY.getIdentifier(), AssemblingMachineDisplay.SERIALIZER);
        registry.register(PlantGrowthChamberDisplay.CATEGORY.getIdentifier(), PlantGrowthChamberDisplay.SERIALIZER);
        registry.register(PlantGrowthChamberFertilizerDisplay.CATEGORY.getIdentifier(), PlantGrowthChamberFertilizerDisplay.SERIALIZER);
        registry.register(EnergizerDisplay.CATEGORY.getIdentifier(), EnergizerDisplay.SERIALIZER);
        registry.register(CrystalGrowthChamberDisplay.CATEGORY.getIdentifier(), CrystalGrowthChamberDisplay.SERIALIZER);
        registry.register(PressMoldMakerDisplay.CATEGORY.getIdentifier(), PressMoldMakerDisplay.SERIALIZER);
        registry.register(AlloyFurnaceDisplay.CATEGORY.getIdentifier(), AlloyFurnaceDisplay.SERIALIZER);
        registry.register(StoneSolidifierDisplay.CATEGORY.getIdentifier(), StoneSolidifierDisplay.SERIALIZER);
        registry.register(FiltrationPlantDisplay.CATEGORY.getIdentifier(), FiltrationPlantDisplay.SERIALIZER);
        registry.register(FluidTransposerDisplay.CATEGORY.getIdentifier(), FluidTransposerDisplay.SERIALIZER);
    }
}
