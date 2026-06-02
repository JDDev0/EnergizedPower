package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ItemPullingUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.ITEM_PULLING
    };

    private static final double ITEM_PULLING_1_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ITEM_PULLING_1_EFFECT.getValue();
    private static final double ITEM_PULLING_2_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ITEM_PULLING_2_EFFECT.getValue();
    private static final double ITEM_PULLING_3_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ITEM_PULLING_3_EFFECT.getValue();
    private static final double ITEM_PULLING_4_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ITEM_PULLING_4_EFFECT.getValue();
    private static final double ITEM_PULLING_5_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ITEM_PULLING_5_EFFECT.getValue();
    private static final double ITEM_PULLING_6_EFFECT = ModConfigs.COMMON_UPGRADE_MODULE_ITEM_PULLING_6_EFFECT.getValue();

    public ItemPullingUpgradeModuleItem(Properties props, int tier) {
        super(props, UpgradeModuleModifier.ITEM_PULLING, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case ITEM_PULLING -> switch(tier) {
                case 1 -> ITEM_PULLING_1_EFFECT;
                case 2 -> ITEM_PULLING_2_EFFECT;
                case 3 -> ITEM_PULLING_3_EFFECT;
                case 4 -> ITEM_PULLING_4_EFFECT;
                case 5 -> ITEM_PULLING_5_EFFECT;
                case 6 -> ITEM_PULLING_6_EFFECT;

                default -> -1;
            };

            default -> -1;
        };
    }

    @Override
    public Component getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case ITEM_PULLING -> Component.literal(String.format(Locale.US, "%.2f", value)).
                    withStyle(ChatFormatting.GREEN);

            default -> Component.empty();
        };
    }
}
