package me.jddev0.ep.mixin.integration.rrv.recipe;

import cc.cassian.rrv.common.builtin.crafting.CraftingClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.jddev0.ep.integration.rrv.EPExtendedReliableClientRecipe;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetAutoCrafterPatternInputSlotsC2SPacket;
import me.jddev0.ep.screen.AdvancedAutoCrafterScreen;
import me.jddev0.ep.screen.AutoCrafterScreen;
import me.jddev0.ep.screen.EliteAutoCrafterScreen;
import me.jddev0.ep.screen.base.ConfigurableIOUpgradableEnergyStorageContainerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mixin(CraftingClientRecipe.class)
public abstract class CraftingClientRecipeMixin implements EPExtendedReliableClientRecipe {
    @Shadow
    private HashMap<Integer, SlotContent> ingredients;

    @ModifyReturnValue(method = "getTransferClasses", at = @At("RETURN"))
    public List<Class<? extends AbstractContainerScreen<?>>> getTransferClassesModifyReturnValue(List<Class<? extends AbstractContainerScreen<?>>> original) {
        List<Class<? extends AbstractContainerScreen<?>>> extendedList = new ArrayList<>(original);

        extendedList.add(AutoCrafterScreen.class);
        extendedList.add(AdvancedAutoCrafterScreen.class);
        extendedList.add(EliteAutoCrafterScreen.class);

        return List.copyOf(extendedList);
    }

    @ModifyReturnValue(method = "canTransferToScreen", at = @At("RETURN"))
    public boolean canTransferToScreenModifyReturnValue(boolean original, AbstractContainerScreen<?> screen) {
        return original | screen instanceof AutoCrafterScreen | screen instanceof AdvancedAutoCrafterScreen | screen instanceof EliteAutoCrafterScreen;
    }

    @Inject(method = "mapRecipeItems", at = @At(value = "HEAD"), cancellable = true)
    private void injectMapRecipeItems(RecipeTransferMap map, AbstractContainerScreen<?> screen, CallbackInfo ci) {
        ConfigurableIOUpgradableEnergyStorageContainerScreen<?> autoCrafterScreen = null;
        if(screen instanceof AutoCrafterScreen epScreen)
            autoCrafterScreen = epScreen;
        else if(screen instanceof AdvancedAutoCrafterScreen epScreen)
            autoCrafterScreen = epScreen;
        else if(screen instanceof EliteAutoCrafterScreen epScreen)
            autoCrafterScreen = epScreen;

        if(autoCrafterScreen != null) {
            //TODO this method is intended to do something else, but it is called right time to send a SetCurrentRecipeIdC2SPacket
            List<ItemStack> itemStacks = new ArrayList<>(9);
            for(int i = 0;i < 9;i++)
                itemStacks.add(ItemStack.EMPTY);

            this.ingredients.forEach((index, slotContent) -> {
                if(index < 9)
                    itemStacks.set(index, slotContent.current());
            });

            ModMessages.sendToServer(new SetAutoCrafterPatternInputSlotsC2SPacket(autoCrafterScreen.getMenu().getBlockEntity().getBlockPos(), itemStacks, getId()));

            ci.cancel();
        }
    }

    @Override
    public boolean energizedpower$ignoreRecipeRequirementsForTransfer(Screen screen) {
        return screen instanceof AutoCrafterScreen | screen instanceof AdvancedAutoCrafterScreen | screen instanceof EliteAutoCrafterScreen;
    }
}
