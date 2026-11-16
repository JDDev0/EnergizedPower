package me.jddev0.ep.mixin.integration.rrv.recipe;

import cc.cassian.rrv.common.builtin.crafting.CraftingClientRecipeType;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(CraftingClientRecipeType.class)
public abstract class CraftingClientRecipeTypeMixin {
    @ModifyReturnValue(method = "getCraftReferences", at = @At("RETURN"))
    public List<ItemStack> getCraftReferencesModifyReturnValue(List<ItemStack> original) {
        List<ItemStack> extendedList = new ArrayList<>(original);

        extendedList.add(new ItemStack(EPBlocks.AUTO_CRAFTER_ITEM.get()));
        extendedList.add(new ItemStack(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM.get()));
        extendedList.add(new ItemStack(EPBlocks.ELITE_AUTO_CRAFTER_ITEM.get()));

        return List.copyOf(extendedList);
    }
}
