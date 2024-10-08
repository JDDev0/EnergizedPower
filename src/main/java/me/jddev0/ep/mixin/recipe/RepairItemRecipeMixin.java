package me.jddev0.ep.mixin.recipe;

import me.jddev0.ep.component.EPDataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RepairItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RepairItemRecipe.class)
public abstract class RepairItemRecipeMixin {
    @Inject(method = "canCombineStacks", at = @At("HEAD"), cancellable = true)
    private static void canCombineStacks(ItemStack first, ItemStack second, CallbackInfoReturnable<Boolean> cir) {
        if(first.contains(EPDataComponentTypes.NO_REPAIR) || second.contains(EPDataComponentTypes.NO_REPAIR))
            cir.setReturnValue(false);
    }
}
