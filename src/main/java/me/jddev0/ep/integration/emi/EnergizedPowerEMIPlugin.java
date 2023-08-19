package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.screen.ModMenuTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.Tags;

@EmiEntrypoint
public class EnergizedPowerEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registerCategories(registry);

        registerRecipes(registry);

        registerRecipeHandlers(registry);
    }

    private void registerCategories(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ModBlocks.AUTO_CRAFTER_ITEM.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiStack.of(ModBlocks.POWERED_FURNACE_ITEM.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiStack.of(ModBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()));

        registry.addCategory(ChargerEMIRecipe.CATEGORY);
        registry.addWorkstation(ChargerEMIRecipe.CATEGORY, ChargerEMIRecipe.ITEM);
        registry.addWorkstation(ChargerEMIRecipe.CATEGORY, EmiStack.of(ModBlocks.ADVANCED_CHARGER_ITEM.get()));

        registry.addCategory(CrusherEMIRecipe.CATEGORY);
        registry.addWorkstation(CrusherEMIRecipe.CATEGORY, CrusherEMIRecipe.ITEM);

        registry.addCategory(PulverizerEMIRecipe.CATEGORY);
        registry.addWorkstation(PulverizerEMIRecipe.CATEGORY, PulverizerEMIRecipe.ITEM);

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

        for(ChargerRecipe recipe:recipeManager.getAllRecipesFor(ChargerRecipe.Type.INSTANCE))
            registry.addRecipe(new ChargerEMIRecipe(recipe));

        for(CrusherRecipe recipe:recipeManager.getAllRecipesFor(CrusherRecipe.Type.INSTANCE))
            registry.addRecipe(new CrusherEMIRecipe(recipe));

        for(PulverizerRecipe recipe:recipeManager.getAllRecipesFor(PulverizerRecipe.Type.INSTANCE))
            registry.addRecipe(new PulverizerEMIRecipe(recipe));

        for(SawmillRecipe recipe:recipeManager.getAllRecipesFor(SawmillRecipe.Type.INSTANCE))
            registry.addRecipe(new SawmillEMIRecipe(recipe));

        for(CompressorRecipe recipe:recipeManager.getAllRecipesFor(CompressorRecipe.Type.INSTANCE))
            registry.addRecipe(new CompressorEMIRecipe(recipe));

        for(PlantGrowthChamberRecipe recipe:recipeManager.getAllRecipesFor(PlantGrowthChamberRecipe.Type.INSTANCE))
            registry.addRecipe(new PlantGrowthChamberEMIRecipe(recipe));

        for(PlantGrowthChamberFertilizerRecipe recipe:recipeManager.getAllRecipesFor(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE))
            registry.addRecipe(new PlantGrowthChamberFertilizerEMIRecipe(recipe));

        for(EnergizerRecipe recipe:recipeManager.getAllRecipesFor(EnergizerRecipe.Type.INSTANCE))
            registry.addRecipe(new EnergizerEMIRecipe(recipe));

        registry.addRecipe(new DispenserEMIRecipe(new DispenserEMIRecipe.DispenserRecipe(
                new ResourceLocation(EnergizedPowerMod.MODID, "dispenser/energizedpower/cable_insulator"),
                Ingredient.of(Tags.Items.SHEARS), Ingredient.of(ItemTags.WOOL),
                new ItemStack(ModItems.CABLE_INSULATOR.get(), 18))));

        registry.addRecipe(new InWorldEMIRecipe(new InWorldEMIRecipe.InWorldRecipe(
                new ResourceLocation(EnergizedPowerMod.MODID, "in_world_crafting/energizedpower/cable_insulator"),
                Ingredient.of(Tags.Items.SHEARS), Ingredient.of(ItemTags.WOOL),
                new ItemStack(ModItems.CABLE_INSULATOR.get(), 18))));
    }

    private void registerRecipeHandlers(EmiRegistry registry) {
        registry.addRecipeHandler(ModMenuTypes.AUTO_CRAFTER_MENU.get(), new AutoCrafterRecipeHandler());
    }
}
