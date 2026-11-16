package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.StoneSolidifierScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class StoneSolidifierClientRecipe implements EPExtendedReliableClientRecipe {
    private final Identifier id;
    private final SlotContent waterInput, lavaInput, output;

    public StoneSolidifierClientRecipe(RecipeHolder<StoneSolidifierRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.waterInput = SlotContent.of(new FluidStack(Fluids.WATER, (int)recipe.value().getWaterAmount()));
        this.lavaInput = SlotContent.of(new FluidStack(Fluids.LAVA, (int)recipe.value().getLavaAmount()));
        this.output = SlotContent.of(recipe.value().getOutput());
    }

    @Override
    public ReliableClientRecipeType getType() {
        return StoneSolidifierClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.waterInput);
        slotFillContext.bindSlot(1, this.lavaInput);
        slotFillContext.bindSlot(2, this.output);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.waterInput, this.lavaInput);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.output);
    }

    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public List<Class<? extends AbstractContainerScreen<?>>> getTransferClasses() {
        return List.of(StoneSolidifierScreen.class);
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {
        //TODO this method is intended to do something else, but it is called right time to send a SetCurrentRecipeIdC2SPacket
        if(screen instanceof StoneSolidifierScreen epScreen) {
            ModMessages.sendToServer(new SetCurrentRecipeIdC2SPacket(epScreen.getMenu().getBlockEntity().getBlockPos(), this.getId()));
        }
    }

    @Override
    public boolean energizedpower$ignoreRecipeRequirementsForTransfer(Screen screen) {
        return true;
    }
}
