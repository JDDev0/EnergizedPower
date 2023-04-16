package me.jddev0.ep.integration.rei;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.screen.*;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import me.shedaniel.rei.plugincompatibilities.api.REIPluginCompatIgnore;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

@REIPluginClient
@REIPluginCompatIgnore
public class EnergizedPowerREIPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return "EnergizedPower";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(ModBlocks.AUTO_CRAFTER_ITEM.get()));
        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(ModBlocks.POWERED_FURNACE_ITEM.get()));

        registry.add(new ChargerCategory());
        registry.addWorkstations(ChargerCategory.CATEGORY, EntryStacks.of(ModBlocks.CHARGER_ITEM.get()));

        registry.add(new CrusherCategory());
        registry.addWorkstations(CrusherCategory.CATEGORY, EntryStacks.of(ModBlocks.CRUSHER_ITEM.get()));

        registry.add(new SawmillCategory());
        registry.addWorkstations(SawmillCategory.CATEGORY, EntryStacks.of(ModBlocks.SAWMILL_ITEM.get()));

        registry.add(new CompressorCategory());
        registry.addWorkstations(CompressorCategory.CATEGORY, EntryStacks.of(ModBlocks.COMPRESSOR_ITEM.get()));

        registry.add(new EnergizerCategory());
        registry.addWorkstations(EnergizerCategory.CATEGORY, EntryStacks.of(ModBlocks.ENERGIZER_ITEM.get()));


        registry.add(new InWorldCategory());
        registry.addWorkstations(InWorldCategory.CATEGORY, EntryIngredients.ofItemTag(Tags.Items.SHEARS));

        registry.add(new DispenserCategory());
        registry.addWorkstations(DispenserCategory.CATEGORY, EntryIngredients.of(Items.DISPENSER));
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new AutoCrafterTransferHandler());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(ChargerRecipe.class, ChargerRecipe.Type.INSTANCE, ChargerDisplay::new);
        registry.registerRecipeFiller(CrusherRecipe.class, CrusherRecipe.Type.INSTANCE, CrusherDisplay::new);
        registry.registerRecipeFiller(SawmillRecipe.class, SawmillRecipe.Type.INSTANCE, SawmillDisplay::new);
        registry.registerRecipeFiller(CompressorRecipe.class, CompressorRecipe.Type.INSTANCE, CompressorDisplay::new);
        registry.registerRecipeFiller(EnergizerRecipe.class, EnergizerRecipe.Type.INSTANCE, EnergizerDisplay::new);

        registry.add(new InWorldDisplay());
        registry.add(new DispenserDisplay());
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(89, 34, 24, 17),
                AutoCrafterScreen.class, BuiltinPlugin.CRAFTING);
        registry.registerContainerClickArea(new Rectangle(80, 34, 24, 17),
                PoweredFurnaceScreen.class, BuiltinPlugin.SMELTING);

        registry.registerContainerClickArea(new Rectangle(25, 16, 40, 54),
                ChargerScreen.class, ChargerCategory.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(111, 16, 58, 54),
                ChargerScreen.class, ChargerCategory.CATEGORY);

        registry.registerContainerClickArea(new Rectangle(80, 34, 24, 17),
                CrusherScreen.class, CrusherCategory.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(68, 34, 24, 17),
                SawmillScreen.class, SawmillCategory.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(79, 30, 26, 25),
                CompressorScreen.class, CompressorCategory.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(89, 34, 24, 17),
                EnergizerScreen.class, EnergizerCategory.CATEGORY);


        registry.registerContainerClickArea(new Rectangle(7, 16, 54, 54),
                DispenserScreen.class, DispenserCategory.CATEGORY);
        registry.registerContainerClickArea(new Rectangle(115, 16, 54, 54),
                DispenserScreen.class, DispenserCategory.CATEGORY);
    }
}
