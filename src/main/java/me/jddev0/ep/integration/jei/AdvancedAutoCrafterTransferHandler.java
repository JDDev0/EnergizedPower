package me.jddev0.ep.integration.jei;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetAdvancedAutoCrafterPatternInputSlotsC2SPacket;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import me.jddev0.ep.screen.EPMenuTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdvancedAutoCrafterTransferHandler implements IRecipeTransferHandler<AdvancedAutoCrafterMenu, RecipeHolder<CraftingRecipe>> {
    private final IRecipeTransferHandlerHelper helper;

    public AdvancedAutoCrafterTransferHandler(IRecipeTransferHandlerHelper helper) {
        this.helper = helper;
    }

    @Override
    public Class<? extends AdvancedAutoCrafterMenu> getContainerClass() {
        return AdvancedAutoCrafterMenu.class;
    }

    @Override
    public Optional<MenuType<AdvancedAutoCrafterMenu>> getMenuType() {
        return Optional.of(EPMenuTypes.ADVANCED_AUTO_CRAFTER_MENU.get());
    }

    @Override
    public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(AdvancedAutoCrafterMenu container, RecipeHolder<CraftingRecipe> recipe, IRecipeSlotsView recipeSlots, Player player,
                                                         boolean maxTransfer, boolean doTransfer) {
        /*TODO fix
        if(!recipe.value().canCraftInDimensions(3, 3))
            return helper.createUserErrorWithTooltip(Component.translatable("recipes.energizedpower.transfer.too_large"));*/

        if(!doTransfer)
            return null;

        List<ItemStack> itemStacks = new ArrayList<>(9);

        List<IRecipeSlotView> inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
        int len = Math.min(inputSlots.size(), 9);
        for(int i = 0;i < len;i++)
            itemStacks.add(inputSlots.get(i).getDisplayedItemStack().orElse(ItemStack.EMPTY).copy());

        ModMessages.sendToServer(new SetAdvancedAutoCrafterPatternInputSlotsC2SPacket(container.getBlockEntity().getBlockPos(), itemStacks, recipe.id().location()));

        return null;
    }
}
