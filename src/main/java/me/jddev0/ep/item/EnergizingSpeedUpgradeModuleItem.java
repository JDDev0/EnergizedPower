package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class EnergizingSpeedUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.ENERGIZING_SPEED
    };

    private static final double ENERGIZING_SPEED_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGIZING_SPEED_1_EFFECT.getValue();
    private static final double ENERGIZING_SPEED_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGIZING_SPEED_2_EFFECT.getValue();
    private static final double ENERGIZING_SPEED_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGIZING_SPEED_3_EFFECT.getValue();
    private static final double ENERGIZING_SPEED_4_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGIZING_SPEED_4_EFFECT.getValue();
    private static final double ENERGIZING_SPEED_5_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGIZING_SPEED_5_EFFECT.getValue();
    private static final double ENERGIZING_SPEED_6_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGIZING_SPEED_6_EFFECT.getValue();
    private static final double ENERGIZING_SPEED_7_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGIZING_SPEED_7_EFFECT.getValue();
    private static final double ENERGIZING_SPEED_8_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGIZING_SPEED_8_EFFECT.getValue();

    public EnergizingSpeedUpgradeModuleItem(Properties props, int tier) {
        super(props, UpgradeModuleModifier.ENERGIZING_SPEED, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case ENERGIZING_SPEED -> switch(tier) {
                case 1 -> ENERGIZING_SPEED_1_EFFECT;
                case 2 -> ENERGIZING_SPEED_2_EFFECT;
                case 3 -> ENERGIZING_SPEED_3_EFFECT;
                case 4 -> ENERGIZING_SPEED_4_EFFECT;
                case 5 -> ENERGIZING_SPEED_5_EFFECT;
                case 6 -> ENERGIZING_SPEED_6_EFFECT;
                case 7 -> ENERGIZING_SPEED_7_EFFECT;
                case 8 -> ENERGIZING_SPEED_8_EFFECT;

                default -> -1;
            };

            default -> -1;
        };
    }

    @Override
    public Component getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case ENERGIZING_SPEED -> Component.literal(String.format(Locale.US, "• %.2f", value)).
                    withStyle(ChatFormatting.GREEN);

            default -> Component.empty();
        };
    }
}
