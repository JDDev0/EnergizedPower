package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class EnergyCapacityUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.ENERGY_CAPACITY, UpgradeModuleModifier.ENERGY_TRANSFER_RATE
    };

    private static final double ENERGY_CAPACITY_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_1_EFFECT.getValue();
    private static final double ENERGY_CAPACITY_1_ENERGY_TRANSFER_RATE_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_1_ENERGY_TRANSFER_RATE_EFFECT.getValue();

    private static final double ENERGY_CAPACITY_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_2_EFFECT.getValue();
    private static final double ENERGY_CAPACITY_2_ENERGY_TRANSFER_RATE_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_2_ENERGY_TRANSFER_RATE_EFFECT.getValue();

    private static final double ENERGY_CAPACITY_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_3_EFFECT.getValue();
    private static final double ENERGY_CAPACITY_3_ENERGY_TRANSFER_RATE_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_3_ENERGY_TRANSFER_RATE_EFFECT.getValue();

    private static final double ENERGY_CAPACITY_4_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_4_EFFECT.getValue();
    private static final double ENERGY_CAPACITY_4_ENERGY_TRANSFER_RATE_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_4_ENERGY_TRANSFER_RATE_EFFECT.getValue();

    private static final double ENERGY_CAPACITY_5_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_5_EFFECT.getValue();
    private static final double ENERGY_CAPACITY_5_ENERGY_TRANSFER_RATE_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_5_ENERGY_TRANSFER_RATE_EFFECT.getValue();

    public EnergyCapacityUpgradeModuleItem(Properties props, int tier) {
        super(props, UpgradeModuleModifier.ENERGY_CAPACITY, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case ENERGY_CAPACITY -> switch(tier) {
                case 1 -> ENERGY_CAPACITY_1_EFFECT;
                case 2 -> ENERGY_CAPACITY_2_EFFECT;
                case 3 -> ENERGY_CAPACITY_3_EFFECT;
                case 4 -> ENERGY_CAPACITY_4_EFFECT;
                case 5 -> ENERGY_CAPACITY_5_EFFECT;

                default -> -1;
            };
            case ENERGY_TRANSFER_RATE -> switch(tier) {
                case 1 -> ENERGY_CAPACITY_1_ENERGY_TRANSFER_RATE_EFFECT;
                case 2 -> ENERGY_CAPACITY_2_ENERGY_TRANSFER_RATE_EFFECT;
                case 3 -> ENERGY_CAPACITY_3_ENERGY_TRANSFER_RATE_EFFECT;
                case 4 -> ENERGY_CAPACITY_4_ENERGY_TRANSFER_RATE_EFFECT;
                case 5 -> ENERGY_CAPACITY_5_ENERGY_TRANSFER_RATE_EFFECT;

                default -> -1;
            };

            default -> -1;
        };
    }

    @Override
    public Component getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case ENERGY_CAPACITY, ENERGY_TRANSFER_RATE -> Component.literal(String.format(Locale.US, "%+.2f %%", 100 * value - 100)).
                    withStyle(ChatFormatting.GREEN);

            default -> Component.empty();
        };
    }
}
