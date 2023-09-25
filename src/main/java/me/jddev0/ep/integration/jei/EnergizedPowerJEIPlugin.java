package me.jddev0.ep.integration.jei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.screen.*;
import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.Tags;

import java.util.Arrays;

@JeiPlugin
@REIPluginCompatIgnore
public class EnergizedPowerJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(EnergizedPowerMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ChargerCategory(registration.getJeiHelpers().getGuiHelper()),
                new CrusherCategory(registration.getJeiHelpers().getGuiHelper()),
                new PulverizerCategory(registration.getJeiHelpers().getGuiHelper()),
                new SawmillCategory(registration.getJeiHelpers().getGuiHelper()),
                new CompressorCategory(registration.getJeiHelpers().getGuiHelper()),
                new PlantGrowthChamberCategory(registration.getJeiHelpers().getGuiHelper()),
                new PlantGrowthChamberFertilizerCategory(registration.getJeiHelpers().getGuiHelper()),
                new EnergizerCategory(registration.getJeiHelpers().getGuiHelper()),

                new InWorldCategory(registration.getJeiHelpers().getGuiHelper()),
                new DispenserCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new AutoCrafterTransferHandler(registration.getTransferHelper()),
                RecipeTypes.CRAFTING);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(ChargerCategory.TYPE, recipeManager.getAllRecipesFor(ChargerRecipe.Type.INSTANCE));
        registration.addRecipes(CrusherCategory.TYPE, recipeManager.getAllRecipesFor(CrusherRecipe.Type.INSTANCE));
        registration.addRecipes(PulverizerCategory.TYPE, recipeManager.getAllRecipesFor(PulverizerRecipe.Type.INSTANCE));
        registration.addRecipes(SawmillCategory.TYPE, recipeManager.getAllRecipesFor(SawmillRecipe.Type.INSTANCE));
        registration.addRecipes(CompressorCategory.TYPE, recipeManager.getAllRecipesFor(CompressorRecipe.Type.INSTANCE));
        registration.addRecipes(PlantGrowthChamberCategory.TYPE, recipeManager.getAllRecipesFor(PlantGrowthChamberRecipe.Type.INSTANCE));
        registration.addRecipes(PlantGrowthChamberFertilizerCategory.TYPE, recipeManager.getAllRecipesFor(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE));
        registration.addRecipes(EnergizerCategory.TYPE, recipeManager.getAllRecipesFor(EnergizerRecipe.Type.INSTANCE));

        registration.addRecipes(InWorldCategory.TYPE, Arrays.asList(
                new InWorldCategory.InWorldRecipe(Ingredient.of(Tags.Items.SHEARS), Ingredient.of(ItemTags.WOOL),
                        new ItemStack(ModItems.CABLE_INSULATOR.get(), 18))
        ));

        registration.addRecipes(DispenserCategory.TYPE, Arrays.asList(
                new DispenserCategory.DispenserRecipe(Ingredient.of(Tags.Items.SHEARS), Ingredient.of(ItemTags.WOOL),
                        new ItemStack(ModItems.CABLE_INSULATOR.get(), 18))
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.AUTO_CRAFTER_ITEM.get()), RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.POWERED_FURNACE_ITEM.get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COAL_ENGINE_ITEM.get()), RecipeTypes.FUELING);
        registration.addRecipeCatalyst(new ItemStack(ModItems.INVENTORY_COAL_ENGINE.get()), RecipeTypes.FUELING);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHARGER_ITEM.get()), ChargerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ADVANCED_CHARGER_ITEM.get()), ChargerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CRUSHER_ITEM.get()), CrusherCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PULVERIZER_ITEM.get()), PulverizerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SAWMILL_ITEM.get()), SawmillCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COMPRESSOR_ITEM.get()), CompressorCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()), PlantGrowthChamberCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()), PlantGrowthChamberFertilizerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ENERGIZER_ITEM.get()), EnergizerCategory.TYPE);

        registration.addRecipeCatalyst(new ItemStack(Items.SHEARS), InWorldCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.DISPENSER), DispenserCategory.TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AutoCrafterScreen.class, 89, 34, 24, 17, RecipeTypes.CRAFTING);
        registration.addRecipeClickArea(PoweredFurnaceScreen.class, 80, 34, 24, 17, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(AdvancedPoweredFurnaceScreen.class, 43, 34, 18, 18, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(AdvancedPoweredFurnaceScreen.class, 97, 34, 18, 18, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(AdvancedPoweredFurnaceScreen.class, 151, 34, 18, 18, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(CoalEngineScreen.class, 79, 25, 18, 17, RecipeTypes.FUELING);

        registration.addRecipeClickArea(ChargerScreen.class, 25, 16, 40, 54, ChargerCategory.TYPE);
        registration.addRecipeClickArea(ChargerScreen.class, 111, 16, 58, 54, ChargerCategory.TYPE);

        registration.addRecipeClickArea(CrusherScreen.class, 80, 34, 24, 17, CrusherCategory.TYPE);
        registration.addRecipeClickArea(PulverizerScreen.class, 68, 34, 24, 17, PulverizerCategory.TYPE);
        registration.addRecipeClickArea(SawmillScreen.class, 68, 34, 24, 17, SawmillCategory.TYPE);
        registration.addRecipeClickArea(CompressorScreen.class, 79, 30, 26, 25, CompressorCategory.TYPE);
        registration.addRecipeClickArea(PlantGrowthChamberScreen.class, 94, 34, 24, 17, PlantGrowthChamberCategory.TYPE);
        registration.addRecipeClickArea(PlantGrowthChamberScreen.class, 34, 16, 18, 17, PlantGrowthChamberFertilizerCategory.TYPE);
        registration.addRecipeClickArea(PlantGrowthChamberScreen.class, 34, 53, 18, 17, PlantGrowthChamberFertilizerCategory.TYPE);
        registration.addRecipeClickArea(EnergizerScreen.class, 89, 34, 24, 17, EnergizerCategory.TYPE);

        registration.addRecipeClickArea(DispenserScreen.class, 7, 16, 54, 54, DispenserCategory.TYPE);
        registration.addRecipeClickArea(DispenserScreen.class, 115, 16, 54, 54, DispenserCategory.TYPE);
    }
}
