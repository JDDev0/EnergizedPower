package me.jddev0.ep.integration.jei;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetAutoCrafterPatternInputSlotsC2SPacket;
import me.jddev0.ep.screen.AutoCrafterMenu;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoCrafterTransferHandler implements IRecipeTransferHandler<AutoCrafterMenu, CraftingRecipe> {
    private final IRecipeTransferHandlerHelper helper;

    public AutoCrafterTransferHandler(IRecipeTransferHandlerHelper helper) {
        this.helper = helper;
    }

    @Override
    public Class<AutoCrafterMenu> getContainerClass() {
        return AutoCrafterMenu.class;
    }

    @Override
    public Class<CraftingRecipe> getRecipeClass() {
        return CraftingRecipe.class;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(AutoCrafterMenu container, CraftingRecipe recipe, IRecipeLayout recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if(!recipe.canCraftInDimensions(3, 3))
            return helper.createUserErrorWithTooltip(new TranslatableComponent("recipes.energizedpower.transfer.too_large"));

        if(!doTransfer)
            return null;

        List<ItemStack> itemStacks = new ArrayList<>(9);

        Map<Integer, ? extends IGuiIngredient<ItemStack>> inputSlots = recipeSlots.getIngredientsGroup(VanillaTypes.ITEM).getGuiIngredients();
        for(int i:inputSlots.keySet().stream().sorted().toList()) {
            ItemStack displayedItemStack = null;
            if(inputSlots.containsKey(i)) {
                if(!inputSlots.get(i).isInput())
                    continue;

                displayedItemStack = inputSlots.get(i).getDisplayedIngredient();
            }

            if(displayedItemStack != null)
                itemStacks.add(displayedItemStack.copy());
            else
                itemStacks.add(ItemStack.EMPTY);
        }

        ModMessages.sendToServer(new SetAutoCrafterPatternInputSlotsC2SPacket(container.getBlockEntity().getBlockPos(), itemStacks));

        return null;
    }
}
