package me.jddev0.ep.integration.rei;

import me.jddev0.ep.recipe.*;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.forge.REIPluginCommon;
import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;

@REIPluginCommon
@REIPluginCompatIgnore
public class EnergizedPowerREIPlugin implements REICommonPlugin {
    @Override
    public String getPluginProviderName() {
        return "EnergizedPower";
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(ChargerRecipe.class).filterType(EPRecipes.CHARGER_TYPE.get()).fill(ChargerDisplay::new);
        registry.beginRecipeFiller(CrusherRecipe.class).filterType(EPRecipes.CRUSHER_TYPE.get()).fill(CrusherDisplay::new);
        registry.beginRecipeFiller(PulverizerRecipe.class).filterType(EPRecipes.PULVERIZER_TYPE.get()).fill(PulverizerDisplay::new);
        registry.beginRecipeFiller(PulverizerRecipe.class).filterType(EPRecipes.PULVERIZER_TYPE.get()).fill(AdvancedPulverizerDisplay::new);
        registry.beginRecipeFiller(SawmillRecipe.class).filterType(EPRecipes.SAWMILL_TYPE.get()).fill(SawmillDisplay::new);
        registry.beginRecipeFiller(CompressorRecipe.class).filterType(EPRecipes.COMPRESSOR_TYPE.get()).fill(CompressorDisplay::new);
        registry.beginRecipeFiller(MetalPressRecipe.class).filterType(EPRecipes.METAL_PRESS_TYPE.get()).fill(MetalPressDisplay::new);
        registry.beginRecipeFiller(AssemblingMachineRecipe.class).filterType(EPRecipes.ASSEMBLING_MACHINE_TYPE.get()).fill(AssemblingMachineDisplay::new);
        registry.beginRecipeFiller(PlantGrowthChamberRecipe.class).filterType(EPRecipes.PLANT_GROWTH_CHAMBER_TYPE.get()).fill(PlantGrowthChamberDisplay::new);
        registry.beginRecipeFiller(PlantGrowthChamberFertilizerRecipe.class).filterType(EPRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_TYPE.get()).fill(PlantGrowthChamberFertilizerDisplay::new);
        registry.beginRecipeFiller(EnergizerRecipe.class).filterType(EPRecipes.ENERGIZER_TYPE.get()).fill(EnergizerDisplay::new);
        registry.beginRecipeFiller(CrystalGrowthChamberRecipe.class).filterType(EPRecipes.CRYSTAL_GROWTH_CHAMBER_TYPE.get()).fill(CrystalGrowthChamberDisplay::new);
        registry.beginRecipeFiller(PressMoldMakerRecipe.class).filterType(EPRecipes.PRESS_MOLD_MAKER_TYPE.get()).fill(PressMoldMakerDisplay::new);
        registry.beginRecipeFiller(AlloyFurnaceRecipe.class).filterType(EPRecipes.ALLOY_FURNACE_TYPE.get()).fill(AlloyFurnaceDisplay::new);
        registry.beginRecipeFiller(StoneLiquefierRecipe.class).filterType(EPRecipes.STONE_LIQUEFIER_TYPE.get()).fill(StoneLiquefierDisplay::new);
        registry.beginRecipeFiller(StoneSolidifierRecipe.class).filterType(EPRecipes.STONE_SOLIDIFIER_TYPE.get()).fill(StoneSolidifierDisplay::new);
        registry.beginRecipeFiller(FiltrationPlantRecipe.class).filterType(EPRecipes.FILTRATION_PLANT_TYPE.get()).fill(FiltrationPlantDisplay::new);
        registry.beginRecipeFiller(FluidTransposerRecipe.class).filterType(EPRecipes.FLUID_TRANSPOSER_TYPE.get()).fill(FluidTransposerDisplay::new);
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
        registry.register(StoneLiquefierDisplay.CATEGORY.getIdentifier(), StoneLiquefierDisplay.SERIALIZER);
        registry.register(StoneSolidifierDisplay.CATEGORY.getIdentifier(), StoneSolidifierDisplay.SERIALIZER);
        registry.register(FiltrationPlantDisplay.CATEGORY.getIdentifier(), FiltrationPlantDisplay.SERIALIZER);
        registry.register(FluidTransposerDisplay.CATEGORY.getIdentifier(), FluidTransposerDisplay.SERIALIZER);
    }
}
