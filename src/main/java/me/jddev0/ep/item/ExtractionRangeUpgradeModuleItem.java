package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ExtractionRangeUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.EXTRACTION_RANGE
    };

    private static final double EXTRACTION_RANGE_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_EXTRACTION_RANGE_1_EFFECT.getValue();

    private static final double EXTRACTION_RANGE_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_EXTRACTION_RANGE_2_EFFECT.getValue();

    private static final double EXTRACTION_RANGE_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_EXTRACTION_RANGE_3_EFFECT.getValue();

    private static final double EXTRACTION_RANGE_4_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_EXTRACTION_RANGE_4_EFFECT.getValue();

    private static final double EXTRACTION_RANGE_5_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_EXTRACTION_RANGE_5_EFFECT.getValue();

    public ExtractionRangeUpgradeModuleItem(Properties props, int tier) {
        super(props, UpgradeModuleModifier.EXTRACTION_RANGE, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case EXTRACTION_RANGE -> switch(tier) {
                case 1 -> EXTRACTION_RANGE_1_EFFECT;
                case 2 -> EXTRACTION_RANGE_2_EFFECT;
                case 3 -> EXTRACTION_RANGE_3_EFFECT;
                case 4 -> EXTRACTION_RANGE_4_EFFECT;
                case 5 -> EXTRACTION_RANGE_5_EFFECT;

                default -> -1;
            };

            default -> -1;
        };
    }

    @Override
    public Component getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case EXTRACTION_RANGE -> Component.literal(String.format(Locale.US, "%+.2f %%", 100 * value - 100)).
                    withStyle(ChatFormatting.GREEN);

            default -> Component.empty();
        };
    }
}
