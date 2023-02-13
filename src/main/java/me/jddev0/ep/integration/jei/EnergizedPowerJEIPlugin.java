package me.jddev0.ep.integration.jei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.recipe.CrusherRecipe;
import me.jddev0.ep.recipe.EnergizerRecipe;
import me.jddev0.ep.recipe.SawmillRecipe;
import me.jddev0.ep.screen.ChargerScreen;
import me.jddev0.ep.screen.CrusherScreen;
import me.jddev0.ep.screen.EnergizerScreen;
import me.jddev0.ep.screen.SawmillScreen;
import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
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
                new SawmillCategory(registration.getJeiHelpers().getGuiHelper()),
                new EnergizerCategory(registration.getJeiHelpers().getGuiHelper()),

                new InWorldCategory(registration.getJeiHelpers().getGuiHelper()),
                new DispenserCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(ChargerCategory.TYPE, recipeManager.getAllRecipesFor(ChargerRecipe.Type.INSTANCE));
        registration.addRecipes(CrusherCategory.TYPE, recipeManager.getAllRecipesFor(CrusherRecipe.Type.INSTANCE));
        registration.addRecipes(SawmillCategory.TYPE, recipeManager.getAllRecipesFor(SawmillRecipe.Type.INSTANCE));
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
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHARGER_ITEM.get()), ChargerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CRUSHER_ITEM.get()), CrusherCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SAWMILL_ITEM.get()), SawmillCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ENERGIZER_ITEM.get()), EnergizerCategory.TYPE);

        registration.addRecipeCatalyst(new ItemStack(Items.SHEARS), InWorldCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.DISPENSER), DispenserCategory.TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ChargerScreen.class, 25, 16, 40, 54, ChargerCategory.TYPE);
        registration.addRecipeClickArea(ChargerScreen.class, 111, 16, 58, 54, ChargerCategory.TYPE);

        registration.addRecipeClickArea(CrusherScreen.class, 80, 34, 24, 17, CrusherCategory.TYPE);
        registration.addRecipeClickArea(SawmillScreen.class, 68, 34, 24, 17, SawmillCategory.TYPE);
        registration.addRecipeClickArea(EnergizerScreen.class, 89, 34, 24, 17, EnergizerCategory.TYPE);

        registration.addRecipeClickArea(DispenserScreen.class, 7, 16, 54, 54, DispenserCategory.TYPE);
        registration.addRecipeClickArea(DispenserScreen.class, 115, 16, 54, 54, DispenserCategory.TYPE);
    }
}
