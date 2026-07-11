package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class XPExtractionUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.XP_YIELD
    };

    private static final double XP_EXTRACTION_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_XP_EXTRACTION_1_EFFECT.getValue();
    private static final double XP_EXTRACTION_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_XP_EXTRACTION_2_EFFECT.getValue();
    private static final double XP_EXTRACTION_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_XP_EXTRACTION_3_EFFECT.getValue();
    private static final double XP_EXTRACTION_4_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_XP_EXTRACTION_4_EFFECT.getValue();
    private static final double XP_EXTRACTION_5_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_XP_EXTRACTION_5_EFFECT.getValue();
    private static final double XP_EXTRACTION_6_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_XP_EXTRACTION_6_EFFECT.getValue();
    private static final double XP_EXTRACTION_7_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_XP_EXTRACTION_7_EFFECT.getValue();
    private static final double XP_EXTRACTION_8_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_XP_EXTRACTION_8_EFFECT.getValue();

    public XPExtractionUpgradeModuleItem(Properties props, int tier) {
        super(props, UpgradeModuleModifier.XP_YIELD, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case XP_YIELD -> switch(tier) {
                case 1 -> XP_EXTRACTION_1_EFFECT;
                case 2 -> XP_EXTRACTION_2_EFFECT;
                case 3 -> XP_EXTRACTION_3_EFFECT;
                case 4 -> XP_EXTRACTION_4_EFFECT;
                case 5 -> XP_EXTRACTION_5_EFFECT;
                case 6 -> XP_EXTRACTION_6_EFFECT;
                case 7 -> XP_EXTRACTION_7_EFFECT;
                case 8 -> XP_EXTRACTION_8_EFFECT;

                default -> -1;
            };

            default -> -1;
        };
    }

    @Override
    public Component getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case XP_YIELD -> Component.literal(String.format(Locale.US, "%.2f %%", 100 * value)).
                    withStyle(ChatFormatting.GREEN);

            default -> Component.empty();
        };
    }
}
