package me.jddev0.ep.item.upgrade;

import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public abstract class UpgradeModuleItem extends Item {
    protected final UpgradeModuleModifier mainModifier;
    protected final int tier;

    public UpgradeModuleItem(Settings props, UpgradeModuleModifier mainModifier, int tier) {
        super(props);

        this.mainModifier = mainModifier;
        this.tier = tier;
    }

    public UpgradeModuleModifier getMainUpgradeModuleModifier() {
        return mainModifier;
    }

    public int getUpgradeModuleTier() {
        return tier;
    }

    public abstract @NotNull UpgradeModuleModifier @NotNull [] getUpgradeModuleModifiers();

    public abstract double getUpgradeModuleModifierValue(UpgradeModuleModifier modifier);

    public abstract Text getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value);

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Text.translatable("tooltip.energizedpower.upgrade.values"));

        for(UpgradeModuleModifier modifier:getUpgradeModuleModifiers())
            tooltip.accept(Text.translatable("tooltip.energizedpower.upgrade.value",
                    Text.translatable("tooltip.energizedpower.upgrade_module_modifier." + modifier.asString()),
                    getUpgradeModuleModifierText(modifier, getUpgradeModuleModifierValue(modifier))));
    }
}
