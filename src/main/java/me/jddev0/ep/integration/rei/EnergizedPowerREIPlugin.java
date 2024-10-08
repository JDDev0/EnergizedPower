package me.jddev0.ep.integration.rei;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.screen.*;
import me.jddev0.ep.screen.base.EnergyStorageContainerScreen;
import me.jddev0.ep.screen.base.IUpgradeModuleMenu;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.neoforged.neoforge.common.Tags;

@REIPluginClient
@REIPluginCompatIgnore
public class EnergizedPowerREIPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return "EnergizedPower";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(EPBlocks.AUTO_CRAFTER_ITEM.get()));

        registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM.get()));

        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(EPBlocks.POWERED_FURNACE_ITEM.get()));
        registry.addWorkstations(BuiltinPlugin.BLASTING, EntryStacks.of(EPBlocks.POWERED_FURNACE_ITEM.get()));
        registry.addWorkstations(BuiltinPlugin.SMOKING, EntryStacks.of(EPBlocks.POWERED_FURNACE_ITEM.get()));

        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()));
        registry.addWorkstations(BuiltinPlugin.BLASTING, EntryStacks.of(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()));
        registry.addWorkstations(BuiltinPlugin.SMOKING, EntryStacks.of(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()));

        registry.addWorkstations(BuiltinPlugin.STONE_CUTTING, EntryStacks.of(EPBlocks.AUTO_STONECUTTER_ITEM.get()));

        registry.addWorkstations(BuiltinPlugin.FUEL, EntryStacks.of(EPBlocks.COAL_ENGINE_ITEM.get()));

        registry.addWorkstations(BuiltinPlugin.FUEL, EntryStacks.of(EPItems.INVENTORY_COAL_ENGINE.get()));

        registry.add(new ChargerCategory());
        registry.addWorkstations(ChargerCategory.CATEGORY, EntryStacks.of(EPBlocks.CHARGER_ITEM.get()),
                EntryStacks.of(EPBlocks.ADVANCED_CHARGER_ITEM.get()));

        registry.add(new CrusherCategory());
        registry.addWorkstations(CrusherCategory.CATEGORY, EntryStacks.of(EPBlocks.CRUSHER_ITEM.get()),
                EntryStacks.of(EPBlocks.ADVANCED_CRUSHER_ITEM.get()));

        registry.add(new PulverizerCategory());
        registry.addWorkstations(PulverizerCategory.CATEGORY, EntryStacks.of(EPBlocks.PULVERIZER_ITEM.get()));

        registry.add(new AdvancedPulverizerCategory());
        registry.addWorkstations(AdvancedPulverizerCategory.CATEGORY, EntryStacks.of(EPBlocks.ADVANCED_PULVERIZER_ITEM.get()));

        registry.add(new SawmillCategory());
        registry.addWorkstations(SawmillCategory.CATEGORY, EntryStacks.of(EPBlocks.SAWMILL_ITEM.get()));

        registry.add(new CompressorCategory());
        registry.addWorkstations(CompressorCategory.CATEGORY, EntryStacks.of(EPBlocks.COMPRESSOR_ITEM.get()));

        registry.add(new MetalPressCategory());
        registry.addWorkstations(MetalPressCategory.CATEGORY, EntryStacks.of(EPBlocks.METAL_PRESS_ITEM.get()));

        registry.add(new AssemblingMachineCategory());
        registry.addWorkstations(AssemblingMachineCategory.CATEGORY, EntryStacks.of(EPBlocks.ASSEMBLING_MACHINE_ITEM.get()));

        registry.add(new PlantGrowthChamberCategory());
        registry.addWorkstations(PlantGrowthChamberCategory.CATEGORY, EntryStacks.of(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()));

        registry.add(new PlantGrowthChamberFertilizerCategory());
        registry.addWorkstations(PlantGrowthChamberFertilizerCategory.CATEGORY, EntryStacks.of(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()));

        registry.add(new EnergizerCategory());
        registry.addWorkstations(EnergizerCategory.CATEGORY, EntryStacks.of(EPBlocks.ENERGIZER_ITEM.get()));

        registry.add(new CrystalGrowthChamberCategory());
        registry.addWorkstations(CrystalGrowthChamberCategory.CATEGORY, EntryStacks.of(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM.get()));

        registry.add(new PressMoldMakerCategory());
        registry.addWorkstations(PressMoldMakerCategory.CATEGORY, EntryStacks.of(EPBlocks.PRESS_MOLD_MAKER_ITEM.get()));
        registry.addWorkstations(PressMoldMakerCategory.CATEGORY, EntryStacks.of(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM.get()));

        registry.add(new AlloyFurnaceCategory());
        registry.addWorkstations(AlloyFurnaceCategory.CATEGORY, EntryStacks.of(EPBlocks.ALLOY_FURNACE_ITEM.get()));
        registry.addWorkstations(AlloyFurnaceCategory.CATEGORY, EntryStacks.of(EPBlocks.INDUCTION_SMELTER_ITEM.get()));

        registry.add(new StoneSolidifierCategory());
        registry.addWorkstations(StoneSolidifierCategory.CATEGORY, EntryStacks.of(EPBlocks.STONE_SOLIDIFIER_ITEM.get()));

        registry.add(new FiltrationPlantCategory());
        registry.addWorkstations(FiltrationPlantCategory.CATEGORY, EntryStacks.of(EPBlocks.FILTRATION_PLANT_ITEM.get()));

        registry.add(new FluidTransposerCategory());
        registry.addWorkstations(FluidTransposerCategory.CATEGORY, EntryStacks.of(EPBlocks.FLUID_TRANSPOSER_ITEM.get()));


        registry.add(new InWorldCategory());
        registry.addWorkstations(InWorldCategory.CATEGORY, EntryIngredients.ofItemTag(Tags.Items.SHEARS));

        registry.add(new DispenserCategory());
        registry.addWorkstations(DispenserCategory.CATEGORY, EntryIngredients.of(Items.DISPENSER));
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new AutoCrafterTransferHandler());

        registry.register(new AdvancedAutoCrafterTransferHandler());

        registry.register(new SelectableRecipeMachineTransferHandler<>(AutoPressMoldMakerMenu.class,
                PressMoldMakerRecipe.class));

        registry.register(new SelectableRecipeMachineTransferHandler<>(AutoStonecutterMenu.class,
                StonecutterRecipe.class));

        registry.register(new SelectableRecipeMachineTransferHandler<>(StoneSolidifierMenu.class,
                StoneSolidifierRecipe.class));

        registry.register(new SelectableRecipeMachineTransferHandler<>(FiltrationPlantMenu.class,
                FiltrationPlantRecipe.class));
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
        registry.registerRecipeFiller(AlloyFurnaceRecipe.class, AlloyFurnaceRecipe.Type.INSTANCE, AlloyFurnaceDisplay::new);
        registry.registerRecipeFiller(StoneSolidifierRecipe.class, StoneSolidifierRecipe.Type.INSTANCE, StoneSolidifierDisplay::new);
        registry.registerRecipeFiller(FiltrationPlantRecipe.class, FiltrationPlantRecipe.Type.INSTANCE, FiltrationPlantDisplay::new);
        registry.registerRecipeFiller(FluidTransposerRecipe.class, FluidTransposerRecipe.Type.INSTANCE, FluidTransposerDisplay::new);

        registry.add(new InWorldDisplay());
        registry.add(new DispenserDisplay());
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

        registry.registerContainerClickArea(new Rectangle(35, 36, 15, 15),
                AlloyFurnaceScreen.class, BuiltinPlugin.FUEL);

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
        registry.registerContainerClickArea(new Rectangle(79, 34, 24, 17),
                AlloyFurnaceScreen.class, AlloyFurnaceCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(104, 34, 24, 17),
                InductionSmelterScreen.class, AlloyFurnaceCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(69, 45, 20, 14),
                StoneSolidifierScreen.class, StoneSolidifierCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(123, 45, 20, 14),
                StoneSolidifierScreen.class, StoneSolidifierCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(67, 35, 78, 8),
                FiltrationPlantScreen.class, FiltrationPlantCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(67, 62, 78, 8),
                FiltrationPlantScreen.class, FiltrationPlantCategory.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(114, 19, 20, 14),
                FluidTransposerScreen.class, FluidTransposerCategory.CATEGORY);


        registry.registerContainerClickArea(new Rectangle(7, 16, 54, 54),
                DispenserScreen.class, DispenserCategory.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(115, 16, 54, 54),
                DispenserScreen.class, DispenserCategory.CATEGORY);
    }

    private <T extends EnergyStorageContainerScreen<? extends IUpgradeModuleMenu>> void
    registerRecipeClickArea(ScreenRegistry registry, final Rectangle area, final Class<? extends T> containerScreenClass,
                            final CategoryIdentifier<?>... recipeTypes) {
        registry.registerClickArea(containerScreenClass, UpgradeModuleScreenClickArea.createRecipeClickArea(
                containerScreenClass, area, recipeTypes
        ));
    }
}
