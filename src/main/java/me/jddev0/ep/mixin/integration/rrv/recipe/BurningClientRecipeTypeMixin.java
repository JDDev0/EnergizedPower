package me.jddev0.ep.mixin.integration.rrv.recipe;

import cc.cassian.rrv.common.builtin.burning.BurningClientRecipeType;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(BurningClientRecipeType.class)
public abstract class BurningClientRecipeTypeMixin {
    @ModifyReturnValue(method = "getCraftReferences", at = @At("RETURN"))
    public List<ItemStack> getCraftReferencesModifyReturnValue(List<ItemStack> original) {
        List<ItemStack> extendedList = new ArrayList<>(original);

        extendedList.add(new ItemStack(EPBlocks.COAL_ENGINE_ITEM.get()));
        extendedList.add(new ItemStack(EPItems.INVENTORY_COAL_ENGINE.get()));

        return List.copyOf(extendedList);
    }
}
