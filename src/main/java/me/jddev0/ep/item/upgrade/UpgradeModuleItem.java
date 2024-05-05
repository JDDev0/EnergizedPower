package me.jddev0.ep.item.upgrade;

import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class UpgradeModuleItem extends Item {
    protected final UpgradeModuleModifier mainModifier;
    protected final int tier;

    public UpgradeModuleItem(Properties props, UpgradeModuleModifier mainModifier, int tier) {
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

    public abstract Component getUpgradeModuleModifierText(UpgradeModuleModifier modifier, double value);

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(Component.translatable("tooltip.energizedpower.upgrade.values"));

        for(UpgradeModuleModifier modifier:getUpgradeModuleModifiers())
            components.add(Component.translatable("tooltip.energizedpower.upgrade.value",
                    Component.translatable("tooltip.energizedpower.upgrade_module_modifier." + modifier.getSerializedName()),
                    getUpgradeModuleModifierText(modifier, getUpgradeModuleModifierValue(modifier))));
    }
}
