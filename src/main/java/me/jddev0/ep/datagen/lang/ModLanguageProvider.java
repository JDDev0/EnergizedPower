package me.jddev0.ep.datagen.lang;

import me.jddev0.ep.datagen.generators.ExtensionLanguageProvider;
import me.jddev0.ep.util.NumberUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends ExtensionLanguageProvider {
    public static FabricDataGenerator.Pack.RegistryDependentFactory<ModLanguageProvider> create(
            String locale) {
        return (output, lookupProvider) -> new ModLanguageProvider(output, lookupProvider, locale);
    }

    public static FabricDataGenerator.Pack.RegistryDependentFactory<ModLanguageProvider> create(
            String locale, String sourceLangFilePath) {
        return (output, lookupProvider) -> new ModLanguageProvider(output, lookupProvider, locale, sourceLangFilePath);
    }

    public ModLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               String locale, String sourceLangFilePath) {
        super(output, lookupProvider, locale, sourceLangFilePath);
    }

    public ModLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               String locale) {
        super(output, lookupProvider, locale, "lang/" + locale + ".json");
    }

    @Override
    protected final void addExtendedTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        Map<String, Integer> upgradeModules = new HashMap<>();
        {
            upgradeModules.put("speed", 6);
            upgradeModules.put("energy_efficiency", 6);
            upgradeModules.put("energy_production", 6);
            upgradeModules.put("energy_capacity", 6);
            upgradeModules.put("duration", 6);
            upgradeModules.put("range", 3);
            upgradeModules.put("extraction_depth", 6);
            upgradeModules.put("extraction_range", 6);
            upgradeModules.put("moon_light", 3);
            upgradeModules.put("item_ejector", 6);
        }

        upgradeModules.forEach((upgradeModuleType, count) -> {
            String itemTemplateString = templateTranslations.get("_template.item.energizedpower." +
                    upgradeModuleType + "_upgrade_module");
            String advancementTitleTemplateString = templateTranslations.get("_template.advancements.energizedpower." +
                    upgradeModuleType + "_upgrade_module.title");
            String advancementDescriptionTemplateString = templateTranslations.get("_template.advancements.energizedpower." +
                    upgradeModuleType + "_upgrade_module.description");
            for(int i = 0;i < count;i++) {
                int tier = i + 1;

                translationBuilder.add("item.energizedpower." + upgradeModuleType + "_upgrade_module_" + tier,
                        String.format(itemTemplateString, NumberUtils.convertToRoman(tier)));
                translationBuilder.add("advancements.energizedpower." + upgradeModuleType + "_upgrade_module_" + tier + ".title",
                        String.format(advancementTitleTemplateString, NumberUtils.convertToRoman(tier)));
                translationBuilder.add("advancements.energizedpower." + upgradeModuleType + "_upgrade_module_" + tier + ".description",
                        String.format(advancementDescriptionTemplateString, NumberUtils.convertToRoman(tier)));
            }
        });

        String batteryTemplateString = templateTranslations.get("_template.item.energizedpower.battery");
        for(int i = 0;i < 8;i++) {
            int tier = i + 1;

            translationBuilder.add("item.energizedpower.battery_" + tier, String.format(batteryTemplateString, NumberUtils.convertToRoman(tier)));
        }

        String solarPanelTemplateString = templateTranslations.get("_template.block.energizedpower.solar_panel");
        for(int i = 0;i < 6;i++) {
            int tier = i + 1;

            String value = String.format(solarPanelTemplateString, NumberUtils.convertToRoman(tier));
            translationBuilder.add("block.energizedpower.solar_panel_" + tier, value);
            translationBuilder.add("item.energizedpower.solar_panel_" + tier, value);
            translationBuilder.add("container.energizedpower.solar_panel_" + tier, value);
        }
    }
}
