package me.jddev0.ep.mixin.integration.rrv.recipe;

import cc.cassian.rrv.common.builtin.stonecutting.StonecutterClientRecipe;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.jddev0.ep.integration.rrv.EPExtendedReliableClientRecipe;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.jddev0.ep.screen.AutoStonecutterScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(StonecutterClientRecipe.class)
public abstract class StonecutterClientRecipeMixin implements EPExtendedReliableClientRecipe {
    @ModifyReturnValue(method = "supportsItemTransfer", at = @At("RETURN"))
    public boolean supportsItemTransfer(boolean original) {
        return true;
    }

    @ModifyReturnValue(method = "getTransferClasses", at = @At("RETURN"))
    public List<Class<? extends AbstractContainerScreen<?>>> getTransferClassesModifyReturnValue(List<Class<? extends AbstractContainerScreen<?>>> original) {
        List<Class<? extends AbstractContainerScreen<?>>> extendedList = new ArrayList<>(original);

        extendedList.add(AutoStonecutterScreen.class);

        return List.copyOf(extendedList);
    }

    @Inject(method = "mapRecipeItems", at = @At(value = "HEAD"), cancellable = true)
    private void injectMapRecipeItems(RecipeTransferMap map, AbstractContainerScreen<?> screen, CallbackInfo ci) {
        //TODO this method is intended to do something else, but it is called right time to send a SetCurrentRecipeIdC2SPacket
        if(screen instanceof AutoStonecutterScreen epScreen) {
            ModMessages.sendToServer(new SetCurrentRecipeIdC2SPacket(epScreen.getMenu().getBlockEntity().getBlockPos(), this.getId()));

            ci.cancel();
        }
    }

    @Override
    public boolean energizedpower$ignoreRecipeRequirementsForTransfer(Screen screen) {
        return screen instanceof AutoStonecutterScreen;
    }
}
