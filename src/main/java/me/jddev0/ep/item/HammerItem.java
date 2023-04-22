package me.jddev0.ep.item;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;

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
    public boolean hasCraftingRemainingItem(ItemStack itemStack) {
        return true;
    }
}
