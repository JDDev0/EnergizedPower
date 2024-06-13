package me.jddev0.ep.item;

import me.jddev0.ep.block.CableBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.state.BlockState;

public class CutterItem extends TieredItem {
    private final RandomSource random = RandomSource.create();

    public CutterItem(Tier tier, Properties props) {
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

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        if(blockState.getBlock() instanceof CableBlock)
            return 15.f;

        return super.getDestroySpeed(itemStack, blockState);
    }
}
