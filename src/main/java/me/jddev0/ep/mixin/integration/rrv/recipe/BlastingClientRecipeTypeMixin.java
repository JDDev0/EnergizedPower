package me.jddev0.ep.mixin.integration.rrv.recipe;

import cc.cassian.rrv.common.builtin.blasting.BlastingClientRecipeType;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(BlastingClientRecipeType.class)
public abstract class BlastingClientRecipeTypeMixin {
    @ModifyReturnValue(method = "getCraftReferences", at = @At("RETURN"))
    public List<ItemStack> getCraftReferencesModifyReturnValue(List<ItemStack> original) {
        List<ItemStack> extendedList = new ArrayList<>(original);

        extendedList.add(new ItemStack(EPBlocks.POWERED_FURNACE_ITEM.get()));
        extendedList.add(new ItemStack(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM.get()));
        extendedList.add(new ItemStack(EPBlocks.ELITE_POWERED_FURNACE_ITEM.get()));

        return List.copyOf(extendedList);
    }
}
