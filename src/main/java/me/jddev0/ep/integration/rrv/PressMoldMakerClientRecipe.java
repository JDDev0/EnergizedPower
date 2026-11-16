package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.screen.AutoPressMoldMakerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class PressMoldMakerClientRecipe implements EPExtendedReliableClientRecipe {
    private final Identifier id;
    private final SlotContent input, output;

    public PressMoldMakerClientRecipe(RecipeHolder<PressMoldMakerRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.input = SlotContent.of(new ItemStack(Items.CLAY_BALL, recipe.value().getClayCount()));
        this.output = SlotContent.of(recipe.value().getOutput());
    }

    @Override
    public ReliableClientRecipeType getType() {
        return PressMoldMakerClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.output);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input);
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
        return List.of(AutoPressMoldMakerScreen.class);
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {
        //TODO this method is intended to do something else, but it is called right time to send a SetCurrentRecipeIdC2SPacket
        if(screen instanceof AutoPressMoldMakerScreen epScreen) {
            ModMessages.sendToServer(new SetCurrentRecipeIdC2SPacket(epScreen.getMenu().getBlockEntity().getBlockPos(), this.getId()));
        }
    }

    @Override
    public boolean energizedpower$ignoreRecipeRequirementsForTransfer() {
        return true;
    }
}
