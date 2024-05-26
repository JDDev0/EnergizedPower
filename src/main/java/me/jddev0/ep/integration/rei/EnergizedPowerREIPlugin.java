package me.jddev0.ep.integration.rei;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.screen.*;
import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.item.Items;

public class EnergizedPowerREIPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return "EnergizedPower";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(ModBlocks.AUTO_CRAFTER_ITEM));

        registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(ModBlocks.ADVANCED_AUTO_CRAFTER_ITEM));

        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(ModBlocks.POWERED_FURNACE_ITEM));
        registry.addWorkstations(BuiltinPlugin.BLASTING, EntryStacks.of(ModBlocks.POWERED_FURNACE_ITEM));
        registry.addWorkstations(BuiltinPlugin.SMOKING, EntryStacks.of(ModBlocks.POWERED_FURNACE_ITEM));

        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(ModBlocks.ADVANCED_POWERED_FURNACE_ITEM));
        registry.addWorkstations(BuiltinPlugin.BLASTING, EntryStacks.of(ModBlocks.ADVANCED_POWERED_FURNACE_ITEM));
        registry.addWorkstations(BuiltinPlugin.SMOKING, EntryStacks.of(ModBlocks.ADVANCED_POWERED_FURNACE_ITEM));

        registry.addWorkstations(BuiltinPlugin.STONE_CUTTING, EntryStacks.of(ModBlocks.AUTO_STONECUTTER_ITEM));

        registry.addWorkstations(BuiltinPlugin.FUEL, EntryStacks.of(ModBlocks.COAL_ENGINE_ITEM));

        registry.addWorkstations(BuiltinPlugin.FUEL, EntryStacks.of(ModItems.INVENTORY_COAL_ENGINE));

        registry.add(new ChargerCategory());
        registry.addWorkstations(ChargerCategory.CATEGORY, EntryStacks.of(ModBlocks.CHARGER_ITEM),
                EntryStacks.of(ModBlocks.ADVANCED_CHARGER_ITEM));

        registry.add(new CrusherCategory());
        registry.addWorkstations(CrusherCategory.CATEGORY, EntryStacks.of(ModBlocks.CRUSHER_ITEM),
                EntryStacks.of(ModBlocks.ADVANCED_CRUSHER_ITEM));

        registry.add(new PulverizerCategory());
        registry.addWorkstations(PulverizerCategory.CATEGORY, EntryStacks.of(ModBlocks.PULVERIZER_ITEM));

        registry.add(new AdvancedPulverizerCategory());
        registry.addWorkstations(AdvancedPulverizerCategory.CATEGORY, EntryStacks.of(ModBlocks.ADVANCED_PULVERIZER_ITEM));

        registry.add(new SawmillCategory());
        registry.addWorkstations(SawmillCategory.CATEGORY, EntryStacks.of(ModBlocks.SAWMILL_ITEM));

        registry.add(new CompressorCategory());
        registry.addWorkstations(CompressorCategory.CATEGORY, EntryStacks.of(ModBlocks.COMPRESSOR_ITEM));

        registry.add(new MetalPressCategory());
        registry.addWorkstations(MetalPressCategory.CATEGORY, EntryStacks.of(ModBlocks.METAL_PRESS_ITEM));

        registry.add(new AssemblingMachineCategory());
        registry.addWorkstations(AssemblingMachineCategory.CATEGORY, EntryStacks.of(ModBlocks.ASSEMBLING_MACHINE_ITEM));

        registry.add(new PlantGrowthChamberCategory());
        registry.addWorkstations(PlantGrowthChamberCategory.CATEGORY, EntryStacks.of(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM));

        registry.add(new PlantGrowthChamberFertilizerCategory());
        registry.addWorkstations(PlantGrowthChamberFertilizerCategory.CATEGORY, EntryStacks.of(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM));

        registry.add(new EnergizerCategory());
        registry.addWorkstations(EnergizerCategory.CATEGORY, EntryStacks.of(ModBlocks.ENERGIZER_ITEM));

        registry.add(new CrystalGrowthChamberCategory());
        registry.addWorkstations(CrystalGrowthChamberCategory.CATEGORY, EntryStacks.of(ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM));

        registry.add(new PressMoldMakerCategory());
        registry.addWorkstations(PressMoldMakerCategory.CATEGORY, EntryStacks.of(ModBlocks.PRESS_MOLD_MAKER_ITEM));
        registry.addWorkstations(PressMoldMakerCategory.CATEGORY, EntryStacks.of(ModBlocks.AUTO_PRESS_MOLD_MAKER_ITEM));

        registry.add(new StoneSolidifierCategory());
        registry.addWorkstations(StoneSolidifierCategory.CATEGORY, EntryStacks.of(ModBlocks.STONE_SOLIDIFIER_ITEM));

        registry.add(new FiltrationPlantCategory());
        registry.addWorkstations(FiltrationPlantCategory.CATEGORY, EntryStacks.of(ModBlocks.FILTRATION_PLANT_ITEM));


        registry.add(new InWorldCategory());
        registry.addWorkstations(InWorldCategory.CATEGORY, EntryIngredients.ofItemTag(CommonItemTags.SHEARS));

        registry.add(new DispenserCategory());
        registry.addWorkstations(DispenserCategory.CATEGORY, EntryIngredients.of(Items.DISPENSER));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(ChargerRecipe.class, ChargerRecipe.Type.INSTANCE, ChargerDisplay::new);
        registry.registerRecipeFiller(CrusherRecipe.class, CrusherRecipe.Type.INSTANCE, CrusherDisplay::new);
        registry.registerRecipeFiller(PulverizerRecipe.class, PulverizerRecipe.Type.INSTANCE, PulverizerDisplay::new);
        registry.registerRecipeFiller(PulverizerRecipe.class, PulverizerRecipe.Type.INSTANCE, AdvancedPulverizerDisplay::new);
        registry.registerRecipeFiller(SawmillRecipe.class, SawmillRecipe.Type.INSTANCE, SawmillDisplay::new);
        registry.registerRecipeFiller(CompressorRecipe.class, CompressorRecipe.Type.INSTANCE, CompressorDisplay::new);
        registry.registerRecipeFiller(MetalPressRecipe.class, MetalPressRecipe.Type.INSTANCE, MetalPressDisplay::new);
        registry.registerRecipeFiller(AssemblingMachineRecipe.class, AssemblingMachineRecipe.Type.INSTANCE, AssemblingMachineDisplay::new);
        registry.registerRecipeFiller(PlantGrowthChamberRecipe.class, PlantGrowthChamberRecipe.Type.INSTANCE,
                PlantGrowthChamberDisplay::new);
        registry.registerRecipeFiller(PlantGrowthChamberFertilizerRecipe.class, PlantGrowthChamberFertilizerRecipe.Type.INSTANCE,
                PlantGrowthChamberFertilizerDisplay::new);
        registry.registerRecipeFiller(EnergizerRecipe.class, EnergizerRecipe.Type.INSTANCE, EnergizerDisplay::new);
        registry.registerRecipeFiller(CrystalGrowthChamberRecipe.class, CrystalGrowthChamberRecipe.Type.INSTANCE, CrystalGrowthChamberDisplay::new);
        registry.registerRecipeFiller(PressMoldMakerRecipe.class, PressMoldMakerRecipe.Type.INSTANCE, PressMoldMakerDisplay::new);
        registry.registerRecipeFiller(StoneSolidifierRecipe.class, StoneSolidifierRecipe.Type.INSTANCE, StoneSolidifierDisplay::new);
        registry.registerRecipeFiller(FiltrationPlantRecipe.class, FiltrationPlantRecipe.Type.INSTANCE, FiltrationPlantDisplay::new);

        registry.add(new InWorldDisplay());
        registry.add(new DispenserDisplay());
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new AutoCrafterTransferHandler());
        registry.register(new AdvancedAutoCrafterTransferHandler());
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registerRecipeClickArea(registry, new Rectangle(89, 34, 24, 17),
                AutoCrafterScreen.class, BuiltinPlugin.CRAFTING);

        registerRecipeClickArea(registry, new Rectangle(89, 34, 24, 17),
                AdvancedAutoCrafterScreen.class, BuiltinPlugin.CRAFTING);

        registerRecipeClickArea(registry, new Rectangle(80, 34, 24, 17),
                PoweredFurnaceScreen.class, BuiltinPlugin.SMELTING, BuiltinPlugin.BLASTING, BuiltinPlugin.SMOKING);

        registerRecipeClickArea(registry, new Rectangle(43, 34, 18, 18),
                AdvancedPoweredFurnaceScreen.class, BuiltinPlugin.SMELTING, BuiltinPlugin.BLASTING, BuiltinPlugin.SMOKING);
        registerRecipeClickArea(registry, new Rectangle(97, 34, 18, 18),
                AdvancedPoweredFurnaceScreen.class, BuiltinPlugin.SMELTING, BuiltinPlugin.BLASTING, BuiltinPlugin.SMOKING);
        registerRecipeClickArea(registry, new Rectangle(151, 34, 18, 18),
                AdvancedPoweredFurnaceScreen.class, BuiltinPlugin.SMELTING, BuiltinPlugin.BLASTING, BuiltinPlugin.SMOKING);

        registerRecipeClickArea(registry, new Rectangle(84, 43, 20, 17),
                AutoStonecutterScreen.class, BuiltinPlugin.STONE_CUTTING);

        registerRecipeClickArea(registry, new Rectangle(79, 25, 18, 17),
                CoalEngineScreen.class, BuiltinPlugin.FUEL);

        registerRecipeClickArea(registry, new Rectangle(25, 16, 40, 54),
                ChargerScreen.class, ChargerCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(111, 16, 58, 54),
                ChargerScreen.class, ChargerCategory.CATEGORY);

        registerRecipeClickArea(registry, new Rectangle(80, 34, 24, 17),
                CrusherScreen.class, CrusherCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(90, 34, 24, 17),
                AdvancedCrusherScreen.class, CrusherCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(68, 34, 24, 17),
                PulverizerScreen.class, PulverizerCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(90, 34, 24, 17),
                AdvancedPulverizerScreen.class, AdvancedPulverizerCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(68, 34, 24, 17),
                SawmillScreen.class, SawmillCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(79, 30, 26, 25),
                CompressorScreen.class, CompressorCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(80, 41, 24, 10),
                MetalPressScreen.class, MetalPressCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(100, 36, 24, 17),
                AssemblingMachineScreen.class, AssemblingMachineCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(94, 34, 24, 17),
                PlantGrowthChamberScreen.class, PlantGrowthChamberCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(34, 16, 18, 17),
                PlantGrowthChamberScreen.class, PlantGrowthChamberFertilizerCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(34, 53, 18, 17),
                PlantGrowthChamberScreen.class, PlantGrowthChamberFertilizerCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(89, 34, 24, 17),
                EnergizerScreen.class, EnergizerCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(80, 34, 24, 17),
                CrystalGrowthChamberScreen.class, CrystalGrowthChamberCategory.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(7, 34, 18, 18),
                PressMoldMakerScreen.class, PressMoldMakerCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(84, 43, 20, 17),
                AutoPressMoldMakerScreen.class, PressMoldMakerCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(69, 45, 20, 14),
                StoneSolidifierScreen.class, StoneSolidifierCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(123, 45, 20, 14),
                StoneSolidifierScreen.class, StoneSolidifierCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(67, 35, 78, 8),
                FiltrationPlantScreen.class, FiltrationPlantCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(67, 62, 78, 8),
                FiltrationPlantScreen.class, FiltrationPlantCategory.CATEGORY);

        registry.registerContainerClickArea(new Rectangle(7, 16, 54, 54),
                Generic3x3ContainerScreen.class, DispenserCategory.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(115, 16, 54, 54),
                Generic3x3ContainerScreen.class, DispenserCategory.CATEGORY);
    }

    private <T extends EnergyStorageContainerScreen<? extends UpgradeModuleMenu>> void
    registerRecipeClickArea(ScreenRegistry registry, final Rectangle area, final Class<? extends T> containerScreenClass,
                            final CategoryIdentifier<?>... recipeTypes) {
        registry.registerClickArea(containerScreenClass, UpgradeModuleScreenClickArea.createRecipeClickArea(
                containerScreenClass, area, recipeTypes
        ));
    }
}
