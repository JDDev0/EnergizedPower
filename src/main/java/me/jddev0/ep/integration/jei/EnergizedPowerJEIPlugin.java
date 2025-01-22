package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.screen.*;
import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import me.jddev0.ep.screen.base.IUpgradeModuleMenu;
import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.Tags;

import java.util.Arrays;

@JeiPlugin
@REIPluginCompatIgnore
public class EnergizedPowerJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return EPAPI.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ChargerCategory(registration.getJeiHelpers().getGuiHelper()),
                new CrusherCategory(registration.getJeiHelpers().getGuiHelper()),
                new PulverizerCategory(registration.getJeiHelpers().getGuiHelper()),
                new AdvancedPulverizerCategory(registration.getJeiHelpers().getGuiHelper()),
                new SawmillCategory(registration.getJeiHelpers().getGuiHelper()),
                new CompressorCategory(registration.getJeiHelpers().getGuiHelper()),
                new MetalPressCategory(registration.getJeiHelpers().getGuiHelper()),
                new AssemblingMachineCategory(registration.getJeiHelpers().getGuiHelper()),
                new PlantGrowthChamberCategory(registration.getJeiHelpers().getGuiHelper()),
                new PlantGrowthChamberFertilizerCategory(registration.getJeiHelpers().getGuiHelper()),
                new EnergizerCategory(registration.getJeiHelpers().getGuiHelper()),
                new CrystalGrowthChamberCategory(registration.getJeiHelpers().getGuiHelper()),
                new PressMoldMakerCategory(registration.getJeiHelpers().getGuiHelper()),
                new AlloyFurnaceCategory(registration.getJeiHelpers().getGuiHelper()),
                new StoneSolidifierCategory(registration.getJeiHelpers().getGuiHelper()),
                new FiltrationPlantCategory(registration.getJeiHelpers().getGuiHelper()),
                new FluidTransposerCategory(registration.getJeiHelpers().getGuiHelper()),

                new InWorldCategory(registration.getJeiHelpers().getGuiHelper()),
                new DispenserCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new AutoCrafterTransferHandler(registration.getTransferHelper()),
                RecipeTypes.CRAFTING);

        registration.addRecipeTransferHandler(new AdvancedAutoCrafterTransferHandler(registration.getTransferHelper()),
                RecipeTypes.CRAFTING);

        registration.addRecipeTransferHandler(new SelectableRecipeMachineTransferHandler<>(
                registration.getTransferHelper(), AutoPressMoldMakerMenu.class,
                        EPMenuTypes.AUTO_PRESS_MOLD_MAKER_MENU.get()),
                PressMoldMakerCategory.TYPE);

        registration.addRecipeTransferHandler(new SelectableRecipeMachineTransferHandler<>(
                registration.getTransferHelper(), AutoStonecutterMenu.class,
                        EPMenuTypes.AUTO_STONECUTTER_MENU.get()),
                RecipeTypes.STONECUTTING);

        registration.addRecipeTransferHandler(new SelectableRecipeMachineTransferHandler<>(
                registration.getTransferHelper(), StoneSolidifierMenu.class,
                        EPMenuTypes.STONE_SOLIDIFIER_MENU.get()),
                StoneSolidifierCategory.TYPE);

        registration.addRecipeTransferHandler(new SelectableRecipeMachineTransferHandler<>(
                registration.getTransferHelper(), FiltrationPlantMenu.class,
                        EPMenuTypes.FILTRATION_PLANT_MENU.get()),
                FiltrationPlantCategory.TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        /*TODO fix
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(ChargerCategory.TYPE, recipeManager.getAllRecipesFor(ChargerRecipe.Type.INSTANCE));
        registration.addRecipes(CrusherCategory.TYPE, recipeManager.getAllRecipesFor(CrusherRecipe.Type.INSTANCE));
        registration.addRecipes(PulverizerCategory.TYPE, recipeManager.getAllRecipesFor(PulverizerRecipe.Type.INSTANCE));
        registration.addRecipes(AdvancedPulverizerCategory.TYPE, recipeManager.getAllRecipesFor(PulverizerRecipe.Type.INSTANCE));
        registration.addRecipes(SawmillCategory.TYPE, recipeManager.getAllRecipesFor(SawmillRecipe.Type.INSTANCE));
        registration.addRecipes(CompressorCategory.TYPE, recipeManager.getAllRecipesFor(CompressorRecipe.Type.INSTANCE));
        registration.addRecipes(MetalPressCategory.TYPE, recipeManager.getAllRecipesFor(MetalPressRecipe.Type.INSTANCE));
        registration.addRecipes(AssemblingMachineCategory.TYPE, recipeManager.getAllRecipesFor(AssemblingMachineRecipe.Type.INSTANCE));
        registration.addRecipes(PlantGrowthChamberCategory.TYPE, recipeManager.getAllRecipesFor(PlantGrowthChamberRecipe.Type.INSTANCE));
        registration.addRecipes(PlantGrowthChamberFertilizerCategory.TYPE, recipeManager.getAllRecipesFor(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE));
        registration.addRecipes(EnergizerCategory.TYPE, recipeManager.getAllRecipesFor(EnergizerRecipe.Type.INSTANCE));
        registration.addRecipes(CrystalGrowthChamberCategory.TYPE, recipeManager.getAllRecipesFor(CrystalGrowthChamberRecipe.Type.INSTANCE));
        registration.addRecipes(PressMoldMakerCategory.TYPE, recipeManager.getAllRecipesFor(PressMoldMakerRecipe.Type.INSTANCE));
        registration.addRecipes(AlloyFurnaceCategory.TYPE, recipeManager.getAllRecipesFor(AlloyFurnaceRecipe.Type.INSTANCE));
        registration.addRecipes(StoneSolidifierCategory.TYPE, recipeManager.getAllRecipesFor(StoneSolidifierRecipe.Type.INSTANCE));
        registration.addRecipes(FiltrationPlantCategory.TYPE, recipeManager.getAllRecipesFor(FiltrationPlantRecipe.Type.INSTANCE));
        registration.addRecipes(FluidTransposerCategory.TYPE, recipeManager.getAllRecipesFor(FluidTransposerRecipe.Type.INSTANCE));

        registration.addRecipes(InWorldCategory.TYPE, Arrays.asList(
                new InWorldCategory.InWorldRecipe(Ingredient.of(Tags.Items.TOOLS_SHEAR), Ingredient.of(ItemTags.WOOL),
                        new ItemStack(EPItems.CABLE_INSULATOR.get(), 18))
        ));

        registration.addRecipes(DispenserCategory.TYPE, Arrays.asList(
                new DispenserCategory.DispenserRecipe(Ingredient.of(Tags.Items.TOOLS_SHEAR), Ingredient.of(ItemTags.WOOL),
                        new ItemStack(EPItems.CABLE_INSULATOR.get(), 18))
        ));*/
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.AUTO_CRAFTER_ITEM.get()), RecipeTypes.CRAFTING);

        registration.addRecipeCatalyst(new ItemStack(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM.get()), RecipeTypes.CRAFTING);

        registration.addRecipeCatalyst(new ItemStack(EPBlocks.POWERED_FURNACE_ITEM.get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.POWERED_FURNACE_ITEM.get()), RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.POWERED_FURNACE_ITEM.get()), RecipeTypes.SMOKING);

        registration.addRecipeCatalyst(new ItemStack(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()), RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()), RecipeTypes.SMOKING);

        registration.addRecipeCatalyst(new ItemStack(EPBlocks.AUTO_STONECUTTER_ITEM.get()), RecipeTypes.STONECUTTING);

        registration.addRecipeCatalyst(new ItemStack(EPBlocks.COAL_ENGINE_ITEM.get()), RecipeTypes.FUELING);

        registration.addRecipeCatalyst(new ItemStack(EPItems.INVENTORY_COAL_ENGINE.get()), RecipeTypes.FUELING);

        registration.addRecipeCatalyst(new ItemStack(EPBlocks.CHARGER_ITEM.get()), ChargerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.ADVANCED_CHARGER_ITEM.get()), ChargerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.CRUSHER_ITEM.get()), CrusherCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.ADVANCED_CRUSHER_ITEM.get()), CrusherCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.PULVERIZER_ITEM.get()), PulverizerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.ADVANCED_PULVERIZER_ITEM.get()), AdvancedPulverizerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.SAWMILL_ITEM.get()), SawmillCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.COMPRESSOR_ITEM.get()), CompressorCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.METAL_PRESS_ITEM.get()), MetalPressCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.ASSEMBLING_MACHINE_ITEM.get()), AssemblingMachineCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()), PlantGrowthChamberCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()), PlantGrowthChamberFertilizerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.ENERGIZER_ITEM.get()), EnergizerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM.get()), CrystalGrowthChamberCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.PRESS_MOLD_MAKER_ITEM.get()), PressMoldMakerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM.get()), PressMoldMakerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.ALLOY_FURNACE_ITEM.get()), AlloyFurnaceCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.INDUCTION_SMELTER_ITEM.get()), AlloyFurnaceCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.STONE_SOLIDIFIER_ITEM.get()), StoneSolidifierCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.FILTRATION_PLANT_ITEM.get()), FiltrationPlantCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPBlocks.FLUID_TRANSPOSER_ITEM.get()), FluidTransposerCategory.TYPE);

        registration.addRecipeCatalyst(new ItemStack(Items.SHEARS), InWorldCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.DISPENSER), DispenserCategory.TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registerRecipeClickArea(registration, AutoCrafterScreen.class, 89, 34, 24, 17, RecipeTypes.CRAFTING);

        registerRecipeClickArea(registration, AdvancedAutoCrafterScreen.class, 89, 34, 24, 17, RecipeTypes.CRAFTING);

        registerRecipeClickArea(registration, PoweredFurnaceScreen.class, 80, 34, 24, 17, RecipeTypes.SMELTING,
                RecipeTypes.BLASTING, RecipeTypes.SMOKING);

        registerRecipeClickArea(registration, AdvancedPoweredFurnaceScreen.class, 43, 34, 18, 18, RecipeTypes.SMELTING,
                RecipeTypes.BLASTING, RecipeTypes.SMOKING);
        registerRecipeClickArea(registration, AdvancedPoweredFurnaceScreen.class, 97, 34, 18, 18, RecipeTypes.SMELTING,
                RecipeTypes.BLASTING, RecipeTypes.SMOKING);
        registerRecipeClickArea(registration, AdvancedPoweredFurnaceScreen.class, 151, 34, 18, 18, RecipeTypes.SMELTING,
                RecipeTypes.BLASTING, RecipeTypes.SMOKING);

        registerRecipeClickArea(registration, AutoStonecutterScreen.class, 84, 43, 24, 17, RecipeTypes.STONECUTTING);

        registerRecipeClickArea(registration, CoalEngineScreen.class, 79, 25, 18, 17, RecipeTypes.FUELING);

        registration.addRecipeClickArea(AlloyFurnaceScreen.class, 35, 36, 15, 15, RecipeTypes.FUELING);

        registerRecipeClickArea(registration, ChargerScreen.class, 25, 16, 40, 54, ChargerCategory.TYPE);
        registerRecipeClickArea(registration, ChargerScreen.class, 111, 16, 58, 54, ChargerCategory.TYPE);

        registerRecipeClickArea(registration, CrusherScreen.class, 80, 34, 24, 17, CrusherCategory.TYPE);
        registerRecipeClickArea(registration, AdvancedCrusherScreen.class, 90, 34, 24, 17, CrusherCategory.TYPE);
        registerRecipeClickArea(registration, PulverizerScreen.class, 68, 34, 24, 17, PulverizerCategory.TYPE);
        registerRecipeClickArea(registration, AdvancedPulverizerScreen.class, 90, 34, 24, 17, AdvancedPulverizerCategory.TYPE);
        registerRecipeClickArea(registration, SawmillScreen.class, 68, 34, 24, 17, SawmillCategory.TYPE);
        registerRecipeClickArea(registration, CompressorScreen.class, 79, 30, 26, 25, CompressorCategory.TYPE);
        registerRecipeClickArea(registration, MetalPressScreen.class, 80, 41, 24, 10, MetalPressCategory.TYPE);
        registerRecipeClickArea(registration, AssemblingMachineScreen.class, 100, 36, 24, 17, AssemblingMachineCategory.TYPE);
        registerRecipeClickArea(registration, PlantGrowthChamberScreen.class, 94, 34, 24, 17, PlantGrowthChamberCategory.TYPE);
        registerRecipeClickArea(registration, PlantGrowthChamberScreen.class, 34, 16, 18, 17, PlantGrowthChamberFertilizerCategory.TYPE);
        registerRecipeClickArea(registration, PlantGrowthChamberScreen.class, 34, 53, 18, 17, PlantGrowthChamberFertilizerCategory.TYPE);
        registerRecipeClickArea(registration, EnergizerScreen.class, 89, 34, 24, 17, EnergizerCategory.TYPE);
        registerRecipeClickArea(registration, CrystalGrowthChamberScreen.class, 80, 34, 24, 17, CrystalGrowthChamberCategory.TYPE);
        registration.addRecipeClickArea(PressMoldMakerScreen.class, 7, 34, 18, 18, PressMoldMakerCategory.TYPE);
        registerRecipeClickArea(registration, AutoPressMoldMakerScreen.class, 84, 43, 24, 17, PressMoldMakerCategory.TYPE);
        registration.addRecipeClickArea(AlloyFurnaceScreen.class, 79, 34, 24, 17, AlloyFurnaceCategory.TYPE);
        registerRecipeClickArea(registration, InductionSmelterScreen.class, 104, 34, 24, 17, AlloyFurnaceCategory.TYPE);
        registerRecipeClickArea(registration, StoneSolidifierScreen.class, 69, 45, 20, 14, StoneSolidifierCategory.TYPE);
        registerRecipeClickArea(registration, StoneSolidifierScreen.class, 123, 45, 20, 14, StoneSolidifierCategory.TYPE);
        registerRecipeClickArea(registration, FiltrationPlantScreen.class, 67, 35, 78, 8, FiltrationPlantCategory.TYPE);
        registerRecipeClickArea(registration, FiltrationPlantScreen.class, 67, 62, 78, 8, FiltrationPlantCategory.TYPE);
        registerRecipeClickArea(registration, FluidTransposerScreen.class, 114, 19, 20, 14, FluidTransposerCategory.TYPE);

        registration.addRecipeClickArea(DispenserScreen.class, 7, 16, 54, 54, DispenserCategory.TYPE);
        registration.addRecipeClickArea(DispenserScreen.class, 115, 16, 54, 54, DispenserCategory.TYPE);
    }

    private <T extends EnergyStorageContainerScreen<? extends IUpgradeModuleMenu>> void
    registerRecipeClickArea(IGuiHandlerRegistration registration, final Class<? extends T> containerScreenClass,
                            final int xPos, final int yPos, final int width, final int height,
                            final RecipeType<?>... recipeTypes) {
        registration.addGuiContainerHandler(containerScreenClass, UpgradeModuleScreenClickArea.createRecipeClickArea(
                containerScreenClass, xPos, yPos, width, height, recipeTypes
        ));
    }
}
