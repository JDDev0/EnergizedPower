package me.jddev0.ep.mixin.integration.rrv;

import cc.cassian.rrv.common.recipe.inventory.RecipeTransferData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.HashMap;

@Mixin(RecipeTransferData.class)
public interface RecipeTransferDataAccessor {
    @Invoker("<init>")
    static RecipeTransferData newRecipeTransferData(HashMap<Integer, Boolean> slotResults, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) {
        throw new AssertionError();
    }
}
