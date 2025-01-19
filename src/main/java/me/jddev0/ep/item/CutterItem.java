package me.jddev0.ep.item;

import me.jddev0.ep.component.EPDataComponentTypes;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Unit;
import net.minecraft.util.math.random.Random;
import me.jddev0.ep.block.CableBlock;

public class CutterItem extends Item {
    private final Random random = Random.create();

    public CutterItem(ToolMaterial tier, Item.Settings props) {
        super(props.component(EPDataComponentTypes.NO_REPAIR, Unit.INSTANCE).
                maxDamage(tier.durability()).repairable(tier.repairItems()).enchantable(tier.enchantmentValue()));
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        //TODO fix for durability enchantment -> Get ServerWorld somehow and use instead of if:
        //     "copy.damage(1, null, null, item -> copy.setCount(0));"
        if(copy.isDamageable()) {
            int i = copy.getDamage() + 1;
            copy.setDamage(i);
            if(i >= copy.getMaxDamage())
                copy.setCount(0);
        }

        return copy;
    }

    @Override
    public float getMiningSpeed(ItemStack itemStack, BlockState blockState) {
        if(blockState.getBlock() instanceof CableBlock)
            return 15.f;

        return super.getMiningSpeed(itemStack, blockState);
    }
}
