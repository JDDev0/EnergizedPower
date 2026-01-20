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
public class EnergizedPowerREIClientPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return "EnergizedPowerClient";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(EPBlocks.AUTO_CRAFTER_ITEM));

        registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM));

        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(EPBlocks.POWERED_FURNACE_ITEM));
        registry.addWorkstations(BuiltinPlugin.BLASTING, EntryStacks.of(EPBlocks.POWERED_FURNACE_ITEM));
        registry.addWorkstations(BuiltinPlugin.SMOKING, EntryStacks.of(EPBlocks.POWERED_FURNACE_ITEM));

        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM));
        registry.addWorkstations(BuiltinPlugin.BLASTING, EntryStacks.of(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM));
        registry.addWorkstations(BuiltinPlugin.SMOKING, EntryStacks.of(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM));

        registry.addWorkstations(BuiltinPlugin.STONE_CUTTING, EntryStacks.of(EPBlocks.AUTO_STONECUTTER_ITEM));

        registry.addWorkstations(BuiltinPlugin.FUEL, EntryStacks.of(EPBlocks.COAL_ENGINE_ITEM));

        registry.addWorkstations(BuiltinPlugin.FUEL, EntryStacks.of(EPItems.INVENTORY_COAL_ENGINE));

        registry.add(new ChargerCategory());
        registry.addWorkstations(ChargerDisplay.CATEGORY, EntryStacks.of(EPBlocks.CHARGER_ITEM),
                EntryStacks.of(EPBlocks.ADVANCED_CHARGER_ITEM));

        registry.add(new CrusherCategory());
        registry.addWorkstations(CrusherDisplay.CATEGORY, EntryStacks.of(EPBlocks.CRUSHER_ITEM),
                EntryStacks.of(EPBlocks.ADVANCED_CRUSHER_ITEM));

        registry.add(new PulverizerCategory());
        registry.addWorkstations(PulverizerDisplay.CATEGORY, EntryStacks.of(EPBlocks.PULVERIZER_ITEM));

        registry.add(new AdvancedPulverizerCategory());
        registry.addWorkstations(AdvancedPulverizerDisplay.CATEGORY, EntryStacks.of(EPBlocks.ADVANCED_PULVERIZER_ITEM));

        registry.add(new SawmillCategory());
        registry.addWorkstations(SawmillDisplay.CATEGORY, EntryStacks.of(EPBlocks.SAWMILL_ITEM));

        registry.add(new CompressorCategory());
        registry.addWorkstations(CompressorDisplay.CATEGORY, EntryStacks.of(EPBlocks.COMPRESSOR_ITEM));

        registry.add(new MetalPressCategory());
        registry.addWorkstations(MetalPressDisplay.CATEGORY, EntryStacks.of(EPBlocks.METAL_PRESS_ITEM));

        registry.add(new AssemblingMachineCategory());
        registry.addWorkstations(AssemblingMachineDisplay.CATEGORY, EntryStacks.of(EPBlocks.ASSEMBLING_MACHINE_ITEM));

        registry.add(new PlantGrowthChamberCategory());
        registry.addWorkstations(PlantGrowthChamberDisplay.CATEGORY, EntryStacks.of(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM));

        registry.add(new PlantGrowthChamberFertilizerCategory());
        registry.addWorkstations(PlantGrowthChamberFertilizerDisplay.CATEGORY, EntryStacks.of(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM));

        registry.add(new EnergizerCategory());
        registry.addWorkstations(EnergizerDisplay.CATEGORY, EntryStacks.of(EPBlocks.ENERGIZER_ITEM));

        registry.add(new CrystalGrowthChamberCategory());
        registry.addWorkstations(CrystalGrowthChamberDisplay.CATEGORY, EntryStacks.of(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM));

        registry.add(new PressMoldMakerCategory());
        registry.addWorkstations(PressMoldMakerDisplay.CATEGORY, EntryStacks.of(EPBlocks.PRESS_MOLD_MAKER_ITEM));
        registry.addWorkstations(PressMoldMakerDisplay.CATEGORY, EntryStacks.of(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM));

        registry.add(new AlloyFurnaceCategory());
        registry.addWorkstations(AlloyFurnaceDisplay.CATEGORY, EntryStacks.of(EPBlocks.ALLOY_FURNACE_ITEM));
        registry.addWorkstations(AlloyFurnaceDisplay.CATEGORY, EntryStacks.of(EPBlocks.INDUCTION_SMELTER_ITEM));

        registry.add(new StoneLiquefierCategory());
        registry.addWorkstations(StoneLiquefierDisplay.CATEGORY, EntryStacks.of(EPBlocks.STONE_LIQUEFIER_ITEM));

        registry.add(new StoneSolidifierCategory());
        registry.addWorkstations(StoneSolidifierDisplay.CATEGORY, EntryStacks.of(EPBlocks.STONE_SOLIDIFIER_ITEM));

        registry.add(new FiltrationPlantCategory());
        registry.addWorkstations(FiltrationPlantDisplay.CATEGORY, EntryStacks.of(EPBlocks.FILTRATION_PLANT_ITEM));

        registry.add(new FluidTransposerCategory());
        registry.addWorkstations(FluidTransposerDisplay.CATEGORY, EntryStacks.of(EPBlocks.FLUID_TRANSPOSER_ITEM));


        registry.add(new InWorldCategory());
        registry.addWorkstations(InWorldDisplay.CATEGORY, EntryIngredients.ofItemTag(Tags.Items.TOOLS_SHEAR));

        registry.add(new DispenserCategory());
        registry.addWorkstations(DispenserDisplay.CATEGORY, EntryIngredients.of(Items.DISPENSER));
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
                ChargerScreen.class, ChargerDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(111, 16, 58, 54),
                ChargerScreen.class, ChargerDisplay.CATEGORY);

        registerRecipeClickArea(registry, new Rectangle(80, 34, 24, 17),
                CrusherScreen.class, CrusherDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(90, 34, 24, 17),
                AdvancedCrusherScreen.class, CrusherDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(68, 34, 24, 17),
                PulverizerScreen.class, PulverizerDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(90, 34, 24, 17),
                AdvancedPulverizerScreen.class, AdvancedPulverizerDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(68, 34, 24, 17),
                SawmillScreen.class, SawmillDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(79, 30, 26, 25),
                CompressorScreen.class, CompressorDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(80, 41, 24, 10),
                MetalPressScreen.class, MetalPressDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(100, 36, 24, 17),
                AssemblingMachineScreen.class, AssemblingMachineDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(94, 34, 24, 17),
                PlantGrowthChamberScreen.class, PlantGrowthChamberDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(34, 16, 18, 17),
                PlantGrowthChamberScreen.class, PlantGrowthChamberFertilizerDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(34, 53, 18, 17),
                PlantGrowthChamberScreen.class, PlantGrowthChamberFertilizerDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(89, 34, 24, 17),
                EnergizerScreen.class, EnergizerDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(80, 34, 24, 17),
                CrystalGrowthChamberScreen.class, CrystalGrowthChamberDisplay.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(7, 34, 18, 18),
                PressMoldMakerScreen.class, PressMoldMakerDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(84, 43, 20, 17),
                AutoPressMoldMakerScreen.class, PressMoldMakerDisplay.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(79, 34, 24, 17),
                AlloyFurnaceScreen.class, AlloyFurnaceDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(104, 34, 24, 17),
                InductionSmelterScreen.class, AlloyFurnaceDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(112, 34, 24, 17),
                StoneLiquefierScreen.class, StoneLiquefierDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(69, 45, 20, 14),
                StoneSolidifierScreen.class, StoneSolidifierDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(123, 45, 20, 14),
                StoneSolidifierScreen.class, StoneSolidifierDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(67, 35, 78, 8),
                FiltrationPlantScreen.class, FiltrationPlantDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(67, 62, 78, 8),
                FiltrationPlantScreen.class, FiltrationPlantDisplay.CATEGORY);
        registerRecipeClickArea(registry, new Rectangle(114, 19, 20, 14),
                FluidTransposerScreen.class, FluidTransposerDisplay.CATEGORY);

        registry.registerContainerClickArea(new Rectangle(7, 16, 54, 54),
                DispenserScreen.class, DispenserDisplay.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(115, 16, 54, 54),
                DispenserScreen.class, DispenserDisplay.CATEGORY);
    }

    private <T extends EnergyStorageContainerScreen<? extends IUpgradeModuleMenu>> void
    registerRecipeClickArea(ScreenRegistry registry, final Rectangle area, final Class<? extends T> containerScreenClass,
                            final CategoryIdentifier<?>... recipeTypes) {
        registry.registerClickArea(containerScreenClass, UpgradeModuleScreenClickArea.createRecipeClickArea(
                containerScreenClass, area, recipeTypes
        ));
    }
}
