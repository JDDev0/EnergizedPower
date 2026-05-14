package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class RangeUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.RANGE
    };

    private static final double RANGE_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_RANGE_1_EFFECT.getValue();

    private static final double RANGE_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_RANGE_2_EFFECT.getValue();

    private static final double RANGE_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_RANGE_3_EFFECT.getValue();

    public RangeUpgradeModuleItem(Properties props, int tier) {
        super(props, UpgradeModuleModifier.RANGE, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case RANGE -> switch(tier) {
                case 1 -> RANGE_1_EFFECT;
                case 2 -> RANGE_2_EFFECT;
                case 3 -> RANGE_3_EFFECT;

                default -> -1;
            };

            default -> -1;
        };
    }

    @Override
    public Component getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case RANGE -> Component.literal(String.format(Locale.US, "%+.2f %%", 100 * value - 100)).
                    withStyle(ChatFormatting.GREEN);

            default -> Component.empty();
        };
    }
}
