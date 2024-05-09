package me.jddev0.ep.item.upgrade;

import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class UpgradeModuleItem extends Item {
    protected final UpgradeModuleModifier mainModifier;
    protected final int tier;

    public UpgradeModuleItem(FabricItemSettings props, UpgradeModuleModifier mainModifier, int tier) {
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.energizedpower.upgrade.values"));

        for(UpgradeModuleModifier modifier:getUpgradeModuleModifiers())
            tooltip.add(Text.translatable("tooltip.energizedpower.upgrade.value",
                    Text.translatable("tooltip.energizedpower.upgrade_module_modifier." + modifier.asString()),
                    getUpgradeModuleModifierText(modifier, getUpgradeModuleModifierValue(modifier))));
    }
}
