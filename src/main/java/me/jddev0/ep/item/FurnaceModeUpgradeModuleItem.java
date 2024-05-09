package me.jddev0.ep.item;

import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

public class FurnaceModeUpgradeModuleItem extends UpgradeModuleItem {
    private static final UpgradeModuleModifier[] UPGRADE_MODULE_MODIFIERS = new UpgradeModuleModifier[] {
            UpgradeModuleModifier.FURNACE_MODE
    };

    public FurnaceModeUpgradeModuleItem(Settings props, int tier) {
        super(props, UpgradeModuleModifier.FURNACE_MODE, tier);
    }

    @Override
    public @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers() {
        return UPGRADE_MODULE_MODIFIERS;
    }

    @Override
    public double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier) {
        return switch(modifier) {
            case FURNACE_MODE -> tier;

            default -> -1;
        };
    }

    @Override
    public Text getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value) {
        return switch(modifier) {
            case FURNACE_MODE -> {
                if(value == 1)
                    yield Text.translatable("item.energizedpower.blast_furnace_upgrade_module.effect_text").formatted(Formatting.YELLOW);
                else if(value == 2)
                    yield Text.translatable("item.energizedpower.smoker_upgrade_module.effect_text").formatted(Formatting.YELLOW);

                yield Text.empty();
            }

            default -> Text.empty();
        };
    }
}
