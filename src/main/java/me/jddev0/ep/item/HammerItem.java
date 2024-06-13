package me.jddev0.ep.item;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;

public class HammerItem extends TieredItem {
    private final RandomSource random = RandomSource.create();

    public HammerItem(Tier tier, Item.Properties props) {
        super(tier, props.setNoRepair());
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        //TODO fix for durability enchantment -> Get ServerWorld somehow and use instead of if:
        //     "copy.hurtAndBreak(1, null, null, item -> copy.setCount(0));"
        if(copy.isDamageableItem()) {
            int i = copy.getDamageValue() + 1;
            copy.setDamageValue(i);
            if(i >= copy.getMaxDamage())
                copy.setCount(0);
        }

        return copy;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack itemStack) {
        return true;
    }
}
