package me.jddev0.ep.item;

import me.jddev0.ep.component.EPDataComponentTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.item.*;

public class HammerItem extends Item {
    private final RandomSource random = RandomSource.create();

    public HammerItem(ToolMaterial tier, Properties props) {
        super(props.component(EPDataComponentTypes.NO_REPAIR, Unit.INSTANCE).
                durability(tier.durability()).repairable(tier.repairItems()).enchantable(tier.enchantmentValue()));
    }

    @Override
    public ItemStack getCraftingRemainder(ItemStack itemStack) {
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
}
