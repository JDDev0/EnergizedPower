package me.jddev0.ep.item;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Vanishable;

public class CutterItem extends TieredItem implements Vanishable {
    private final RandomSource random = RandomSource.create();

    public CutterItem(Tier tier, Properties props) {
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
    public boolean hasCraftingRemainingItem(ItemStack itemStack) {
        return true;
    }
}
