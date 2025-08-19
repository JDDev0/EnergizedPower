package me.jddev0.ep.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.util.math.random.Random;

public class HammerItem extends ToolItem implements Vanishable {
    private final Random random = Random.create();

    public HammerItem(ToolMaterial tier, FabricItemSettings props) {
        super(tier, props);
    }

    @Override
    public boolean canRepair(ItemStack itemStack, ItemStack ingredient) {
        //TODO improve [Equivalent to setNoRepair needed]
        return false;
    }

    @Override
    public boolean isDamageable() {
        //TODO improve [Equivalent to setNoRepair needed]
        return false;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        if(copy.damage(1, random, null))
            return ItemStack.EMPTY;

        return copy;
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }
}
