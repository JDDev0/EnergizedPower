package me.jddev0.ep.mixin.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.gui.VersionedScreen;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import me.jddev0.ep.integration.rrv.EPExtendedReliableClientRecipe;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeViewScreen.class)
public abstract class RecipeViewScreenMixin extends VersionedScreen {
    @Shadow
    protected RecipeViewMenu menu;
    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;

    protected RecipeViewScreenMixin(Component title, Screen parent) {
        super(title, parent);
    }

    @Inject(method = "extractContents", at = @At(value = "INVOKE",
            target = "Lcc/cassian/rrv/common/recipe/inventory/RecipeViewScreen;extractSlots(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V",
            shift = At.Shift.AFTER))
    private void injectExtractContents(final GuiGraphicsExtractor guiGraphics, final int mouseX, final int mouseY, final float partialTicks, CallbackInfo ci) {
        ReliableClientRecipeType recipeType = this.menu.getClientRecipeType();

        int guiLeft = this.leftPos + this.menu.guiOffsetLeft();
        for (int i = 0; i < this.menu.getCurrentDisplay().size(); i++) {

            int guiTop = this.topPos + this.menu.guiOffsetTop(i);

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(guiLeft - this.leftPos, guiTop - this.topPos);

            ReliableClientRecipe reliableClientRecipe = this.menu.getCurrentDisplay().get(i);
            if(reliableClientRecipe instanceof EPExtendedReliableClientRecipe epExtendedReliableClientRecipe) {
                epExtendedReliableClientRecipe.energizedpower$renderRecipeOverlay(
                        new RecipeScreenContext((RecipeViewScreen)(Object)this, (RecipeViewScreen)(Object)this, this.font,
                                new ReliableClientRecipe.RecipePosition(guiLeft, guiTop, recipeType.getDisplayWidth(), recipeType.getDisplayHeight()),
                                guiGraphics, mouseX, mouseY, partialTicks));
            }
            guiGraphics.pose().popMatrix();
        }
    }
}
