package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.jddev0.ep.util.EnergyUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class DurationUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.DURATION
    };

    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS_TIER_6 = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.DURATION,
            UpgradeModuleModifier.ENERGY_CONSUMPTION,
            UpgradeModuleModifier.ENERGY_TRANSFER_RATE
    };

    private static final double DURATION_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_DURATION_1_EFFECT.getValue();

    private static final double DURATION_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_DURATION_2_EFFECT.getValue();

    private static final double DURATION_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_DURATION_3_EFFECT.getValue();

    private static final double DURATION_4_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_DURATION_4_EFFECT.getValue();

    private static final double DURATION_5_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_DURATION_5_EFFECT.getValue();

    private static final double DURATION_6_ENERGY_CONSUMPTION_PER_TICK =
            ModConfigs.COMMON_UPGRADE_MODULE_DURATION_6_ENERGY_CONSUMPTION_PER_TICK.getValue();

    private static final double DURATION_6_ENERGY_TRANSFER_RATE =
            ModConfigs.COMMON_UPGRADE_MODULE_DURATION_6_ENERGY_TRANSFER_RATE.getValue();

    public DurationUpgradeModuleItem(FabricItemSettings props, int tier) {
        super(props, UpgradeModuleModifier.DURATION, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        if(tier == 6)
            return UPGRADE_MODULE_MODIFIERS_TIER_6;

        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case DURATION -> switch(tier) {
                case 1 -> DURATION_1_EFFECT;
                case 2 -> DURATION_2_EFFECT;
                case 3 -> DURATION_3_EFFECT;
                case 4 -> DURATION_4_EFFECT;
                case 5 -> DURATION_5_EFFECT;
                case 6 -> Double.POSITIVE_INFINITY;

                default -> -1;
            };
            case ENERGY_CONSUMPTION -> tier == 6?DURATION_6_ENERGY_CONSUMPTION_PER_TICK:-1;
            case ENERGY_TRANSFER_RATE -> tier == 6?DURATION_6_ENERGY_TRANSFER_RATE:-1;

            default -> -1;
        };
    }

    @Override
    public Text getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case DURATION -> {
                if(value == Double.POSITIVE_INFINITY)
                    yield Text.translatable("tooltip.energizedpower.infinite.txt").
                            formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC);

                yield Text.literal(String.format(Locale.US, "• %.2f", value)).
                        formatted(Formatting.GREEN);
            }
            case ENERGY_CONSUMPTION -> Text.translatable(EnergyUtils.getEnergyWithPrefix((int)value) + "/t").
                    formatted(Formatting.RED);
            case ENERGY_TRANSFER_RATE -> Text.literal(String.format(Locale.US, "%+.2f %%", 100 * value - 100)).
                    formatted(Formatting.GREEN);

            default -> Text.empty();
        };
    }
}
