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
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.Arrays;

@JeiPlugin
@REIPluginCompatIgnore
public class EnergizedPowerJEIPlugin implements IModPlugin {
    public static RecipeMap recipeMap = null;

    @Override
    public Identifier getPluginUid() {
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
                new StoneLiquefierCategory(registration.getJeiHelpers().getGuiHelper()),
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
        if(recipeMap != null) {
            registration.addRecipes(ChargerCategory.TYPE, new ArrayList<>(recipeMap.byType(ChargerRecipe.Type.INSTANCE)));
            registration.addRecipes(CrusherCategory.TYPE, new ArrayList<>(recipeMap.byType(CrusherRecipe.Type.INSTANCE)));
            registration.addRecipes(PulverizerCategory.TYPE, new ArrayList<>(recipeMap.byType(PulverizerRecipe.Type.INSTANCE)));
            registration.addRecipes(AdvancedPulverizerCategory.TYPE, new ArrayList<>(recipeMap.byType(PulverizerRecipe.Type.INSTANCE)));
            registration.addRecipes(SawmillCategory.TYPE, new ArrayList<>(recipeMap.byType(SawmillRecipe.Type.INSTANCE)));
            registration.addRecipes(CompressorCategory.TYPE, new ArrayList<>(recipeMap.byType(CompressorRecipe.Type.INSTANCE)));
            registration.addRecipes(MetalPressCategory.TYPE, new ArrayList<>(recipeMap.byType(MetalPressRecipe.Type.INSTANCE)));
            registration.addRecipes(AssemblingMachineCategory.TYPE, new ArrayList<>(recipeMap.byType(AssemblingMachineRecipe.Type.INSTANCE)));
            registration.addRecipes(PlantGrowthChamberCategory.TYPE, new ArrayList<>(recipeMap.byType(PlantGrowthChamberRecipe.Type.INSTANCE)));
            registration.addRecipes(PlantGrowthChamberFertilizerCategory.TYPE, new ArrayList<>(recipeMap.byType(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE)));
            registration.addRecipes(EnergizerCategory.TYPE, new ArrayList<>(recipeMap.byType(EnergizerRecipe.Type.INSTANCE)));
            registration.addRecipes(CrystalGrowthChamberCategory.TYPE, new ArrayList<>(recipeMap.byType(CrystalGrowthChamberRecipe.Type.INSTANCE)));
            registration.addRecipes(PressMoldMakerCategory.TYPE, new ArrayList<>(recipeMap.byType(PressMoldMakerRecipe.Type.INSTANCE)));
            registration.addRecipes(AlloyFurnaceCategory.TYPE, new ArrayList<>(recipeMap.byType(AlloyFurnaceRecipe.Type.INSTANCE)));
            registration.addRecipes(StoneLiquefierCategory.TYPE, new ArrayList<>(recipeMap.byType(StoneLiquefierRecipe.Type.INSTANCE)));
            registration.addRecipes(StoneSolidifierCategory.TYPE, new ArrayList<>(recipeMap.byType(StoneSolidifierRecipe.Type.INSTANCE)));
            registration.addRecipes(FiltrationPlantCategory.TYPE, new ArrayList<>(recipeMap.byType(FiltrationPlantRecipe.Type.INSTANCE)));
            registration.addRecipes(FluidTransposerCategory.TYPE, new ArrayList<>(recipeMap.byType(FluidTransposerRecipe.Type.INSTANCE)));

            registration.addRecipes(InWorldCategory.TYPE, Arrays.asList(
                    new InWorldCategory.InWorldRecipe(
                            Ingredient.of(Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(Tags.Items.TOOLS_SHEAR)),
                            Ingredient.of(Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(ItemTags.WOOL)),
                            new ItemStack(EPItems.CABLE_INSULATOR.get(), 18))
            ));

            registration.addRecipes(DispenserCategory.TYPE, Arrays.asList(
                    new DispenserCategory.DispenserRecipe(
                            Ingredient.of(Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(Tags.Items.TOOLS_SHEAR)),
                            Ingredient.of(Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(ItemTags.WOOL)),
                            new ItemStack(EPItems.CABLE_INSULATOR.get(), 18))
            ));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(RecipeTypes.CRAFTING, new ItemStack(EPBlocks.AUTO_CRAFTER_ITEM.get()));

        registration.addCraftingStation(RecipeTypes.CRAFTING, new ItemStack(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM.get()));

        registration.addCraftingStation(RecipeTypes.SMELTING, new ItemStack(EPBlocks.POWERED_FURNACE_ITEM.get()));
        registration.addCraftingStation(RecipeTypes.BLASTING, new ItemStack(EPBlocks.POWERED_FURNACE_ITEM.get()));
        registration.addCraftingStation(RecipeTypes.SMOKING, new ItemStack(EPBlocks.POWERED_FURNACE_ITEM.get()));

        registration.addCraftingStation(RecipeTypes.SMELTING, new ItemStack(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()));
        registration.addCraftingStation(RecipeTypes.BLASTING, new ItemStack(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()));
        registration.addCraftingStation(RecipeTypes.SMOKING, new ItemStack(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()));

        registration.addCraftingStation(RecipeTypes.STONECUTTING, new ItemStack(EPBlocks.AUTO_STONECUTTER_ITEM.get()));

        registration.addCraftingStation(RecipeTypes.SMELTING_FUEL, new ItemStack(EPBlocks.COAL_ENGINE_ITEM.get()));

        registration.addCraftingStation(RecipeTypes.SMELTING_FUEL, new ItemStack(EPItems.INVENTORY_COAL_ENGINE.get()));

        registration.addCraftingStation(ChargerCategory.TYPE, new ItemStack(EPBlocks.CHARGER_ITEM.get()));
        registration.addCraftingStation(ChargerCategory.TYPE, new ItemStack(EPBlocks.ADVANCED_CHARGER_ITEM.get()));
        registration.addCraftingStation(CrusherCategory.TYPE, new ItemStack(EPBlocks.CRUSHER_ITEM.get()));
        registration.addCraftingStation(CrusherCategory.TYPE, new ItemStack(EPBlocks.ADVANCED_CRUSHER_ITEM.get()));
        registration.addCraftingStation(PulverizerCategory.TYPE, new ItemStack(EPBlocks.PULVERIZER_ITEM.get()));
        registration.addCraftingStation(AdvancedPulverizerCategory.TYPE, new ItemStack(EPBlocks.ADVANCED_PULVERIZER_ITEM.get()));
        registration.addCraftingStation(SawmillCategory.TYPE, new ItemStack(EPBlocks.SAWMILL_ITEM.get()));
        registration.addCraftingStation(CompressorCategory.TYPE, new ItemStack(EPBlocks.COMPRESSOR_ITEM.get()));
        registration.addCraftingStation(MetalPressCategory.TYPE, new ItemStack(EPBlocks.METAL_PRESS_ITEM.get()));
        registration.addCraftingStation(AssemblingMachineCategory.TYPE, new ItemStack(EPBlocks.ASSEMBLING_MACHINE_ITEM.get()));
        registration.addCraftingStation(PlantGrowthChamberCategory.TYPE, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()));
        registration.addCraftingStation(PlantGrowthChamberFertilizerCategory.TYPE, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()));
        registration.addCraftingStation(EnergizerCategory.TYPE, new ItemStack(EPBlocks.ENERGIZER_ITEM.get()));
        registration.addCraftingStation(CrystalGrowthChamberCategory.TYPE, new ItemStack(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM.get()));
        registration.addCraftingStation(PressMoldMakerCategory.TYPE, new ItemStack(EPBlocks.PRESS_MOLD_MAKER_ITEM.get()));
        registration.addCraftingStation(PressMoldMakerCategory.TYPE, new ItemStack(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM.get()));
        registration.addCraftingStation(AlloyFurnaceCategory.TYPE, new ItemStack(EPBlocks.ALLOY_FURNACE_ITEM.get()));
        registration.addCraftingStation(AlloyFurnaceCategory.TYPE, new ItemStack(EPBlocks.INDUCTION_SMELTER_ITEM.get()));
        registration.addCraftingStation(StoneLiquefierCategory.TYPE, new ItemStack(EPBlocks.STONE_LIQUEFIER_ITEM.get()));
        registration.addCraftingStation(StoneSolidifierCategory.TYPE, new ItemStack(EPBlocks.STONE_SOLIDIFIER_ITEM.get()));
        registration.addCraftingStation(FiltrationPlantCategory.TYPE, new ItemStack(EPBlocks.FILTRATION_PLANT_ITEM.get()));
        registration.addCraftingStation(FluidTransposerCategory.TYPE, new ItemStack(EPBlocks.FLUID_TRANSPOSER_ITEM.get()));

        registration.addCraftingStation(InWorldCategory.TYPE, new ItemStack(Items.SHEARS));
        registration.addCraftingStation(DispenserCategory.TYPE, new ItemStack(Items.DISPENSER));
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

        registerRecipeClickArea(registration, CoalEngineScreen.class, 79, 25, 18, 17, RecipeTypes.SMELTING_FUEL);

        registration.addRecipeClickArea(AlloyFurnaceScreen.class, 35, 36, 15, 15, RecipeTypes.SMELTING_FUEL);

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
        registerRecipeClickArea(registration, StoneLiquefierScreen.class, 112, 34, 24, 17, StoneLiquefierCategory.TYPE);
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
                            final IRecipeType<?>... recipeTypes) {
        registration.addGuiContainerHandler(containerScreenClass, UpgradeModuleScreenClickArea.createRecipeClickArea(
                containerScreenClass, xPos, yPos, width, height, recipeTypes
        ));
    }
}
