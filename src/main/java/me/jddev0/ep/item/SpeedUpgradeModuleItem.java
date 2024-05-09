package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SpeedUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.SPEED, UpgradeModuleModifier.ENERGY_CONSUMPTION
    };

    private static final double SPEED_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_1_EFFECT.getValue();
    private static final double SPEED_1_ENERGY_CONSUMPTION_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_1_ENERGY_CONSUMPTION_EFFECT.getValue();

    private static final double SPEED_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_2_EFFECT.getValue();
    private static final double SPEED_2_ENERGY_CONSUMPTION_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_2_ENERGY_CONSUMPTION_EFFECT.getValue();

    private static final double SPEED_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_3_EFFECT.getValue();
    private static final double SPEED_3_ENERGY_CONSUMPTION_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_3_ENERGY_CONSUMPTION_EFFECT.getValue();

    private static final double SPEED_4_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_4_EFFECT.getValue();
    private static final double SPEED_4_ENERGY_CONSUMPTION_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_4_ENERGY_CONSUMPTION_EFFECT.getValue();

    private static final double SPEED_5_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_5_EFFECT.getValue();
    private static final double SPEED_5_ENERGY_CONSUMPTION_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_SPEED_5_ENERGY_CONSUMPTION_EFFECT.getValue();

    public SpeedUpgradeModuleItem(FabricItemSettings props, int tier) {
        super(props, UpgradeModuleModifier.SPEED, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case SPEED -> switch(tier) {
                case 1 -> SPEED_1_EFFECT;
                case 2 -> SPEED_2_EFFECT;
                case 3 -> SPEED_3_EFFECT;
                case 4 -> SPEED_4_EFFECT;
                case 5 -> SPEED_5_EFFECT;

                default -> -1;
            };
            case ENERGY_CONSUMPTION -> switch(tier) {
                case 1 -> SPEED_1_ENERGY_CONSUMPTION_EFFECT;
                case 2 -> SPEED_2_ENERGY_CONSUMPTION_EFFECT;
                case 3 -> SPEED_3_ENERGY_CONSUMPTION_EFFECT;
                case 4 -> SPEED_4_ENERGY_CONSUMPTION_EFFECT;
                case 5 -> SPEED_5_ENERGY_CONSUMPTION_EFFECT;

                default -> -1;
            };

            default -> -1;
        };
    }

    @Override
    public Text getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case SPEED -> Text.literal(String.format(Locale.US, "• %.2f", value)).
                    formatted(Formatting.GREEN);
            case ENERGY_CONSUMPTION -> Text.literal(String.format(Locale.US, "%+.2f %%", 100 * value - 100)).
                    formatted(Formatting.RED);

            default -> Text.empty();
        };
    }
}
