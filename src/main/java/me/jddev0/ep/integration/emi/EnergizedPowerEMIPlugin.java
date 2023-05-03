package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.integration.rei.PlantGrowthChamberCategory;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.screen.ModMenuTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class EnergizedPowerEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registerCategories(registry);

        registerRecipes(registry);

        registerRecipeHandlers(registry);
    }

    private void registerCategories(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ModBlocks.AUTO_CRAFTER_ITEM));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiStack.of(ModBlocks.POWERED_FURNACE_ITEM));

        registry.addCategory(ChargerEMIRecipe.CATEGORY);
        registry.addWorkstation(ChargerEMIRecipe.CATEGORY, ChargerEMIRecipe.ITEM);

        registry.addCategory(CrusherEMIRecipe.CATEGORY);
        registry.addWorkstation(CrusherEMIRecipe.CATEGORY, CrusherEMIRecipe.ITEM);

        registry.addCategory(SawmillEMIRecipe.CATEGORY);
        registry.addWorkstation(SawmillEMIRecipe.CATEGORY, SawmillEMIRecipe.ITEM);

        registry.addCategory(CompressorEMIRecipe.CATEGORY);
        registry.addWorkstation(CompressorEMIRecipe.CATEGORY, CompressorEMIRecipe.ITEM);

        registry.addCategory(PlantGrowthChamberEMIRecipe.CATEGORY);
        registry.addWorkstation(PlantGrowthChamberEMIRecipe.CATEGORY, PlantGrowthChamberEMIRecipe.ITEM);

        registry.addCategory(PlantGrowthChamberFertilizerEMIRecipe.CATEGORY);
        registry.addWorkstation(PlantGrowthChamberFertilizerEMIRecipe.CATEGORY, PlantGrowthChamberFertilizerEMIRecipe.ITEM);

        registry.addCategory(EnergizerEMIRecipe.CATEGORY);
        registry.addWorkstation(EnergizerEMIRecipe.CATEGORY, EnergizerEMIRecipe.ITEM);

        registry.addCategory(DispenserEMIRecipe.CATEGORY);
        registry.addWorkstation(DispenserEMIRecipe.CATEGORY, DispenserEMIRecipe.ITEM);

        registry.addCategory(InWorldEMIRecipe.CATEGORY);
        registry.addWorkstation(InWorldEMIRecipe.CATEGORY, InWorldEMIRecipe.ITEM);
    }

    private void registerRecipes(EmiRegistry registry) {
        RecipeManager recipeManager = registry.getRecipeManager();

        for(ChargerRecipe recipe:recipeManager.listAllOfType(ChargerRecipe.Type.INSTANCE))
            registry.addRecipe(new ChargerEMIRecipe(recipe));

        for(CrusherRecipe recipe:recipeManager.listAllOfType(CrusherRecipe.Type.INSTANCE))
            registry.addRecipe(new CrusherEMIRecipe(recipe));

        for(SawmillRecipe recipe:recipeManager.listAllOfType(SawmillRecipe.Type.INSTANCE))
            registry.addRecipe(new SawmillEMIRecipe(recipe));

        for(CompressorRecipe recipe:recipeManager.listAllOfType(CompressorRecipe.Type.INSTANCE))
            registry.addRecipe(new CompressorEMIRecipe(recipe));

        for(PlantGrowthChamberRecipe recipe:recipeManager.listAllOfType(PlantGrowthChamberRecipe.Type.INSTANCE))
            registry.addRecipe(new PlantGrowthChamberEMIRecipe(recipe));

        for(PlantGrowthChamberFertilizerRecipe recipe:recipeManager.listAllOfType(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE))
            registry.addRecipe(new PlantGrowthChamberFertilizerEMIRecipe(recipe));

        for(EnergizerRecipe recipe:recipeManager.listAllOfType(EnergizerRecipe.Type.INSTANCE))
            registry.addRecipe(new EnergizerEMIRecipe(recipe));

        registry.addRecipe(new DispenserEMIRecipe(new DispenserEMIRecipe.DispenserRecipe(
                new Identifier(EnergizedPowerMod.MODID, "dispenser/energizedpower/cable_insulator"),
                Ingredient.fromTag(CommonItemTags.SHEARS), Ingredient.fromTag(ItemTags.WOOL),
                new ItemStack(ModItems.CABLE_INSULATOR, 18))));

        registry.addRecipe(new InWorldEMIRecipe(new InWorldEMIRecipe.InWorldRecipe(
                new Identifier(EnergizedPowerMod.MODID, "in_world_crafting/energizedpower/cable_insulator"),
                Ingredient.fromTag(CommonItemTags.SHEARS), Ingredient.fromTag(ItemTags.WOOL),
                new ItemStack(ModItems.CABLE_INSULATOR, 18))));
    }

    private void registerRecipeHandlers(EmiRegistry registry) {
        registry.addRecipeHandler(ModMenuTypes.AUTO_CRAFTER_MENU, new AutoCrafterRecipeHandler());
    }
}
