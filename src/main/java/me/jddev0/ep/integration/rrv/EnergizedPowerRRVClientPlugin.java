package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.builtin.crafting.CraftingClientRecipe;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.FarmlandCraftingRecipe;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

public class EnergizedPowerRRVClientPlugin implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        ItemView.addClientRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.CHARGER_TYPE).stream().
                    map(ChargerClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.CRUSHER_TYPE).stream().
                    map(CrusherClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.PULVERIZER_TYPE).stream().
                    map(PulverizerClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.PULVERIZER_TYPE).stream().
                    map(AdvancedPulverizerClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.SAWMILL_TYPE).stream().
                    map(SawmillClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.COMPRESSOR_TYPE).stream().
                    map(CompressorClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.METAL_PRESS_TYPE).stream().
                    map(MetalPressClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.ASSEMBLING_MACHINE_TYPE).stream().
                    map(AssemblingMachineClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.PLANT_GROWTH_CHAMBER_TYPE).stream().
                    map(recipe -> new PlantGrowthChamberClientRecipe(
                            recipe, ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.PLANT_GROWTH_CHAMBER_SOIL_TYPE))
                    ).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_TYPE).stream().
                    map(PlantGrowthChamberFertilizerClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.PLANT_GROWTH_CHAMBER_SOIL_TYPE).stream().
                    map(PlantGrowthChamberSoilClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.ENERGIZER_TYPE).stream().
                    map(EnergizerClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.CRYSTAL_GROWTH_CHAMBER_TYPE).stream().
                    map(CrystalGrowthChamberClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.PRESS_MOLD_MAKER_TYPE).stream().
                    map(PressMoldMakerClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.ALLOY_FURNACE_TYPE).stream().
                    map(AlloyFurnaceClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.FLUID_FREEZER_TYPE).stream().
                    map(FluidFreezerClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.STONE_LIQUEFIER_TYPE).stream().
                    map(StoneLiquefierClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.STONE_SOLIDIFIER_TYPE).stream().
                    map(StoneSolidifierClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.FILTRATION_PLANT_TYPE).stream().
                    map(FiltrationPlantClientRecipe::new).forEach(recipeList::add);
            ClientRecipeManager.INSTANCE.getRecipesForType(EPRecipes.FLUID_TRANSPOSER_TYPE).stream().
                    map(FluidTransposerClientRecipe::new).forEach(recipeList::add);

            recipeList.add(new InWorldClientRecipe(new InWorldClientRecipe.InWorldRecipe(
                    EPAPI.id("/in_world_crafting/cable_insulator"),
                    Ingredient.of(ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(ConventionalItemTags.SHEAR_TOOLS)),
                    Ingredient.of(ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(ItemTags.WOOL)),
                    new ItemStack(EPItems.CABLE_INSULATOR, 18))
            ));

            recipeList.add(new DispenserClientRecipe(new DispenserClientRecipe.DispenserRecipe(
                    EPAPI.id("/dispenser/cable_insulator"),
                    Ingredient.of(ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(ConventionalItemTags.SHEAR_TOOLS)),
                    Ingredient.of(ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(ItemTags.WOOL)),
                    new ItemStack(EPItems.CABLE_INSULATOR, 18))
            ));

            //Add farmland special crafting recipes
            for(RecipeHolder<CraftingRecipe> craftingRecipe:ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.CRAFTING)) {
                if(!(craftingRecipe.value() instanceof FarmlandCraftingRecipe farmlandCraftingRecipe))
                    continue;

                ShapelessRecipe recipe = new ShapelessRecipe(new Recipe.CommonInfo(true),
                        new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""), farmlandCraftingRecipe.getFarmland(),
                        NonNullList.of(null, new Ingredient[] {
                                Ingredient.of(ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(ItemTags.HOES)),
                                farmlandCraftingRecipe.getDirt()
                        }));

                recipeList.add(new CraftingClientRecipe.Builder(craftingRecipe.id().identifier(), recipe).build());
            }
        });
    }
}
