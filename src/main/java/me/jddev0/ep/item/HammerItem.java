package me.jddev0.ep.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.math.random.Random;

public class HammerItem extends ToolItem {
    private final Random random = Random.create();

    public HammerItem(ToolMaterials tier, Item.Settings props) {
        super(tier, props);
    }

    @Override
    public boolean canRepair(ItemStack itemStack, ItemStack ingredient) {
        //TODO improve [Equivalent to setNoRepair needed]
        return false;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        copy.damage(1, random, null, () -> copy.setCount(0));

        return copy;
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }
}
