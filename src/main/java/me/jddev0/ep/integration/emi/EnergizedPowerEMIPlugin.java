package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.screen.EPMenuTypes;
import net.minecraft.recipe.RecipeManager;

public class EnergizedPowerEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registerCategories(registry);

        registerRecipes(registry);

        registerRecipeHandlers(registry);
    }

    private void registerCategories(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(EPBlocks.AUTO_CRAFTER_ITEM));

        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM));

        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiStack.of(EPBlocks.POWERED_FURNACE_ITEM));
        registry.addWorkstation(VanillaEmiRecipeCategories.BLASTING, EmiStack.of(EPBlocks.POWERED_FURNACE_ITEM));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMOKING, EmiStack.of(EPBlocks.POWERED_FURNACE_ITEM));

        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiStack.of(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM));
        registry.addWorkstation(VanillaEmiRecipeCategories.BLASTING, EmiStack.of(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMOKING, EmiStack.of(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM));

        registry.addWorkstation(VanillaEmiRecipeCategories.STONECUTTING, EmiStack.of(EPBlocks.AUTO_STONECUTTER_ITEM));

        registry.addWorkstation(VanillaEmiRecipeCategories.FUEL, EmiStack.of(EPBlocks.COAL_ENGINE_ITEM));

        registry.addWorkstation(VanillaEmiRecipeCategories.FUEL, EmiStack.of(EPItems.INVENTORY_COAL_ENGINE));

        registry.addCategory(ChargerEMIRecipe.CATEGORY);
        registry.addWorkstation(ChargerEMIRecipe.CATEGORY, ChargerEMIRecipe.ITEM);
        registry.addWorkstation(ChargerEMIRecipe.CATEGORY, EmiStack.of(EPBlocks.ADVANCED_CHARGER_ITEM));

        registry.addCategory(CrusherEMIRecipe.CATEGORY);
        registry.addWorkstation(CrusherEMIRecipe.CATEGORY, CrusherEMIRecipe.ITEM);
        registry.addWorkstation(CrusherEMIRecipe.CATEGORY, EmiStack.of(EPBlocks.ADVANCED_CRUSHER_ITEM));

        registry.addCategory(PulverizerEMIRecipe.CATEGORY);
        registry.addWorkstation(PulverizerEMIRecipe.CATEGORY, PulverizerEMIRecipe.ITEM);

        registry.addCategory(AdvancedPulverizerEMIRecipe.CATEGORY);
        registry.addWorkstation(AdvancedPulverizerEMIRecipe.CATEGORY, AdvancedPulverizerEMIRecipe.ITEM);

        registry.addCategory(SawmillEMIRecipe.CATEGORY);
        registry.addWorkstation(SawmillEMIRecipe.CATEGORY, SawmillEMIRecipe.ITEM);

        registry.addCategory(CompressorEMIRecipe.CATEGORY);
        registry.addWorkstation(CompressorEMIRecipe.CATEGORY, CompressorEMIRecipe.ITEM);

        registry.addCategory(MetalPressEMIRecipe.CATEGORY);
        registry.addWorkstation(MetalPressEMIRecipe.CATEGORY, MetalPressEMIRecipe.ITEM);

        registry.addCategory(AssemblingMachineEMIRecipe.CATEGORY);
        registry.addWorkstation(AssemblingMachineEMIRecipe.CATEGORY, AssemblingMachineEMIRecipe.ITEM);

        registry.addCategory(PlantGrowthChamberEMIRecipe.CATEGORY);
        registry.addWorkstation(PlantGrowthChamberEMIRecipe.CATEGORY, PlantGrowthChamberEMIRecipe.ITEM);

        registry.addCategory(PlantGrowthChamberFertilizerEMIRecipe.CATEGORY);
        registry.addWorkstation(PlantGrowthChamberFertilizerEMIRecipe.CATEGORY, PlantGrowthChamberFertilizerEMIRecipe.ITEM);

        registry.addCategory(EnergizerEMIRecipe.CATEGORY);
        registry.addWorkstation(EnergizerEMIRecipe.CATEGORY, EnergizerEMIRecipe.ITEM);

        registry.addCategory(CrystalGrowthChamberEMIRecipe.CATEGORY);
        registry.addWorkstation(CrystalGrowthChamberEMIRecipe.CATEGORY, CrystalGrowthChamberEMIRecipe.ITEM);

        registry.addCategory(PressMoldMakerEMIRecipe.CATEGORY);
        registry.addWorkstation(PressMoldMakerEMIRecipe.CATEGORY, PressMoldMakerEMIRecipe.ITEM);
        registry.addWorkstation(PressMoldMakerEMIRecipe.CATEGORY, EmiStack.of(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM));

        registry.addCategory(AlloyFurnaceEMIRecipe.CATEGORY);
        registry.addWorkstation(AlloyFurnaceEMIRecipe.CATEGORY, AlloyFurnaceEMIRecipe.ITEM);
        registry.addWorkstation(AlloyFurnaceEMIRecipe.CATEGORY, EmiStack.of(EPBlocks.INDUCTION_SMELTER_ITEM));

        registry.addCategory(StoneLiquefierEMIRecipe.CATEGORY);
        registry.addWorkstation(StoneLiquefierEMIRecipe.CATEGORY, StoneLiquefierEMIRecipe.ITEM);

        registry.addCategory(StoneSolidifierEMIRecipe.CATEGORY);
        registry.addWorkstation(StoneSolidifierEMIRecipe.CATEGORY, StoneSolidifierEMIRecipe.ITEM);

        registry.addCategory(FiltrationPlantEMIRecipe.CATEGORY);
        registry.addWorkstation(FiltrationPlantEMIRecipe.CATEGORY, FiltrationPlantEMIRecipe.ITEM);

        registry.addCategory(FluidTransposerEMIRecipe.CATEGORY);
        registry.addWorkstation(FluidTransposerEMIRecipe.CATEGORY, FluidTransposerEMIRecipe.ITEM);

        registry.addCategory(DispenserEMIRecipe.CATEGORY);
        registry.addWorkstation(DispenserEMIRecipe.CATEGORY, DispenserEMIRecipe.ITEM);

        registry.addCategory(InWorldEMIRecipe.CATEGORY);
        registry.addWorkstation(InWorldEMIRecipe.CATEGORY, InWorldEMIRecipe.ITEM);
    }

    private void registerRecipes(EmiRegistry registry) {
        RecipeManager recipeManager = registry.getRecipeManager();

        //TODO fix
        /*
        for(RecipeEntry<ChargerRecipe> recipe:recipeManager.listAllOfType(ChargerRecipe.Type.INSTANCE))
            registry.addRecipe(new ChargerEMIRecipe(recipe));

        for(RecipeEntry<CrusherRecipe> recipe:recipeManager.listAllOfType(CrusherRecipe.Type.INSTANCE))
            registry.addRecipe(new CrusherEMIRecipe(recipe));

        for(RecipeEntry<PulverizerRecipe> recipe:recipeManager.listAllOfType(PulverizerRecipe.Type.INSTANCE))
            registry.addRecipe(new PulverizerEMIRecipe(recipe));

        for(RecipeEntry<PulverizerRecipe> recipe:recipeManager.listAllOfType(PulverizerRecipe.Type.INSTANCE))
            registry.addRecipe(new AdvancedPulverizerEMIRecipe(recipe));

        for(RecipeEntry<SawmillRecipe> recipe:recipeManager.listAllOfType(SawmillRecipe.Type.INSTANCE))
            registry.addRecipe(new SawmillEMIRecipe(recipe));

        for(RecipeEntry<CompressorRecipe> recipe:recipeManager.listAllOfType(CompressorRecipe.Type.INSTANCE))
            registry.addRecipe(new CompressorEMIRecipe(recipe));

        for(RecipeEntry<MetalPressRecipe> recipe:recipeManager.listAllOfType(MetalPressRecipe.Type.INSTANCE))
            registry.addRecipe(new MetalPressEMIRecipe(recipe));

        for(RecipeEntry<AssemblingMachineRecipe> recipe:recipeManager.listAllOfType(AssemblingMachineRecipe.Type.INSTANCE))
            registry.addRecipe(new AssemblingMachineEMIRecipe(recipe));

        for(RecipeEntry<PlantGrowthChamberRecipe> recipe:recipeManager.listAllOfType(PlantGrowthChamberRecipe.Type.INSTANCE))
            registry.addRecipe(new PlantGrowthChamberEMIRecipe(recipe));

        for(RecipeEntry<PlantGrowthChamberFertilizerRecipe> recipe:recipeManager.listAllOfType(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE))
            registry.addRecipe(new PlantGrowthChamberFertilizerEMIRecipe(recipe));

        for(RecipeEntry<EnergizerRecipe> recipe:recipeManager.listAllOfType(EnergizerRecipe.Type.INSTANCE))
            registry.addRecipe(new EnergizerEMIRecipe(recipe));

        for(RecipeEntry<CrystalGrowthChamberRecipe> recipe:recipeManager.listAllOfType(CrystalGrowthChamberRecipe.Type.INSTANCE))
            registry.addRecipe(new CrystalGrowthChamberEMIRecipe(recipe));

        for(RecipeEntry<PressMoldMakerRecipe> recipe:recipeManager.listAllOfType(PressMoldMakerRecipe.Type.INSTANCE))
            registry.addRecipe(new PressMoldMakerEMIRecipe(recipe));

        for(RecipeEntry<AlloyFurnaceRecipe> recipe:recipeManager.listAllOfType(AlloyFurnaceRecipe.Type.INSTANCE))
            registry.addRecipe(new AlloyFurnaceEMIRecipe(recipe));

        for(RecipeEntry<StoneLiquefierRecipe> recipe:recipeManager.listAllOfType(StoneLiquefierRecipe.Type.INSTANCE))
            registry.addRecipe(new StoneLiquefierEMIRecipe(recipe));

        for(RecipeEntry<StoneSolidifierRecipe> recipe:recipeManager.listAllOfType(StoneSolidifierRecipe.Type.INSTANCE))
            registry.addRecipe(new StoneSolidifierEMIRecipe(recipe));

        for(RecipeEntry<FiltrationPlantRecipe> recipe:recipeManager.listAllOfType(FiltrationPlantRecipe.Type.INSTANCE))
            registry.addRecipe(new FiltrationPlantEMIRecipe(recipe));

        for(RecipeEntry<FluidTransposerRecipe> recipe:recipeManager.listAllOfType(FluidTransposerRecipe.Type.INSTANCE))
            registry.addRecipe(new FluidTransposerEMIRecipe(recipe));

        registry.addRecipe(new DispenserEMIRecipe(new DispenserEMIRecipe.DispenserRecipe(
                EPAPI.id("dispenser/energizedpower/cable_insulator"),
                Ingredient.fromTag(ConventionalItemTags.SHEAR_TOOLS), Ingredient.fromTag(ItemTags.WOOL),
                new ItemStack(EPItems.CABLE_INSULATOR, 18))));

        registry.addRecipe(new InWorldEMIRecipe(new InWorldEMIRecipe.InWorldRecipe(
                EPAPI.id("in_world_crafting/energizedpower/cable_insulator"),
                Ingredient.fromTag(ConventionalItemTags.SHEAR_TOOLS), Ingredient.fromTag(ItemTags.WOOL),
                new ItemStack(EPItems.CABLE_INSULATOR, 18))));*/
    }

    private void registerRecipeHandlers(EmiRegistry registry) {
        registry.addRecipeHandler(EPMenuTypes.AUTO_CRAFTER_MENU, new AutoCrafterRecipeHandler());

        registry.addRecipeHandler(EPMenuTypes.ADVANCED_AUTO_CRAFTER_MENU, new AdvancedAutoCrafterRecipeHandler());

        registry.addRecipeHandler(EPMenuTypes.AUTO_PRESS_MOLD_MAKER_MENU,
                new SelectableRecipeMachineRecipeHandler<>(PressMoldMakerEMIRecipe.CATEGORY));

        registry.addRecipeHandler(EPMenuTypes.AUTO_STONECUTTER_MENU,
                new SelectableRecipeMachineRecipeHandler<>(VanillaEmiRecipeCategories.STONECUTTING));

        registry.addRecipeHandler(EPMenuTypes.STONE_SOLIDIFIER_MENU,
                new SelectableRecipeMachineRecipeHandler<>(StoneSolidifierEMIRecipe.CATEGORY));

        registry.addRecipeHandler(EPMenuTypes.FILTRATION_PLANT_MENU,
                new SelectableRecipeMachineRecipeHandler<>(FiltrationPlantEMIRecipe.CATEGORY));
    }
}
