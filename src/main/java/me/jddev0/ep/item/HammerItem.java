package me.jddev0.ep.item;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class HammerItem extends TieredItem implements Vanishable {
    private final RandomSource random = RandomSource.create();

    public HammerItem(Tier tier, Item.Properties props) {
        super(tier, props.setNoRepair());
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        if(copy.hurt(1, random, null))
            return ItemStack.EMPTY;

        return copy;
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        if(getTier() == Tiers.WOOD)
            return 200;

        return super.getBurnTime(itemStack, recipeType);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack itemStack) {
        return true;
    }
}
