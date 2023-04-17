package me.jddev0.ep.item;

import net.minecraft.world.item.*;

import java.util.Random;

public class HammerItem extends TieredItem implements Vanishable {
    private final Random random = new Random();

    public HammerItem(Tier tier, Item.Properties props) {
        super(tier, props.setNoRepair());
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        if(copy.hurt(1, random, null))
            return ItemStack.EMPTY;

        return copy;
    }

    @Override
    public boolean hasContainerItem(ItemStack itemStack) {
        return true;
    }
}
