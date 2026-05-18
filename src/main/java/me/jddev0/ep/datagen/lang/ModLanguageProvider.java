package me.jddev0.ep.datagen.lang;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.datagen.generators.ExtensionLanguageProvider;
import me.jddev0.ep.util.NumberUtils;
import net.minecraft.data.PackOutput;

import java.util.HashMap;
import java.util.Map;

public class ModLanguageProvider extends ExtensionLanguageProvider {
    public ModLanguageProvider(PackOutput output, String locale, String sourceLangFilePath) {
        super(output, EPAPI.MOD_ID, locale, sourceLangFilePath);
    }

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, EPAPI.MOD_ID, locale, "lang/" + locale + ".json");
    }

    @Override
    protected final void addExtendedTranslations() {
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

                add("item.energizedpower." + upgradeModuleType + "_upgrade_module_" + tier,
                        String.format(itemTemplateString, NumberUtils.convertToRoman(tier)));
                add("advancements.energizedpower." + upgradeModuleType + "_upgrade_module_" + tier + ".title",
                        String.format(advancementTitleTemplateString, NumberUtils.convertToRoman(tier)));
                add("advancements.energizedpower." + upgradeModuleType + "_upgrade_module_" + tier + ".description",
                        String.format(advancementDescriptionTemplateString, NumberUtils.convertToRoman(tier)));
            }
        });

        String batteryTemplateString = templateTranslations.get("_template.item.energizedpower.battery");
        for(int i = 0;i < 8;i++) {
            int tier = i + 1;

            add("item.energizedpower.battery_" + tier, String.format(batteryTemplateString, NumberUtils.convertToRoman(tier)));
        }

        String solarPanelTemplateString = templateTranslations.get("_template.block.energizedpower.solar_panel");
        for(int i = 0;i < 6;i++) {
            int tier = i + 1;

            String value = String.format(solarPanelTemplateString, NumberUtils.convertToRoman(tier));
            add("block.energizedpower.solar_panel_" + tier, value);
            add("item.energizedpower.solar_panel_" + tier, value);
            add("container.energizedpower.solar_panel_" + tier, value);
        }
    }
}
