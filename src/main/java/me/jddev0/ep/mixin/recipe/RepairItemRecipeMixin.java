package me.jddev0.ep.mixin.recipe;

import me.jddev0.ep.component.EPDataComponentTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RepairItemRecipe.class)
public abstract class RepairItemRecipeMixin {
    @Inject(method = "canCombine", at = @At("HEAD"), cancellable = true)
    private static void canCombine(ItemStack first, ItemStack second, CallbackInfoReturnable<Boolean> cir) {
        if(first.has(EPDataComponentTypes.NO_REPAIR) || second.has(EPDataComponentTypes.NO_REPAIR))
            cir.setReturnValue(false);
    }
}
