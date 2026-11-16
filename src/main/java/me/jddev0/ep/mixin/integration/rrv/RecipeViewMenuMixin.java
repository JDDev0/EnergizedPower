package me.jddev0.ep.mixin.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.RecipeTransferData;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import com.llamalad7.mixinextras.sugar.Local;
import me.jddev0.ep.integration.rrv.EPExtendedReliableClientRecipe;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(RecipeViewMenu.class)
public abstract class RecipeViewMenuMixin {
    @Shadow
    private Screen parentScreen;

    @Inject(method = "checkMatchingContent", at = @At(value = "NEW",
            target = "Lcc/cassian/rrv/common/recipe/inventory/RecipeViewMenu$SlotFillContext;"), cancellable = true)
    private void injectCheckMatchingContent(int displayId, CallbackInfoReturnable<RecipeTransferData> cri, @Local ReliableClientRecipe currentLooking) {
        if(currentLooking instanceof EPExtendedReliableClientRecipe epExtendedReliableClientRecipe &&
                epExtendedReliableClientRecipe.energizedpower$ignoreRecipeRequirementsForTransfer(parentScreen)) {
            RecipeTransferData rtd = RecipeTransferDataAccessor.newRecipeTransferData(new HashMap<>(), new HashMap<>());
            cri.setReturnValue(rtd);
        }
    }
}
