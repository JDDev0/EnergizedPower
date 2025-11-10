package me.jddev0.ep.integration.jei;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetAutoCrafterPatternInputSlotsC2SPacket;
import me.jddev0.ep.screen.AutoCrafterMenu;
import me.jddev0.ep.screen.EPMenuTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutoCrafterTransferHandler implements IRecipeTransferHandler<AutoCrafterMenu, RecipeEntry<CraftingRecipe>> {
    private final IRecipeTransferHandlerHelper helper;

    public AutoCrafterTransferHandler(IRecipeTransferHandlerHelper helper) {
        this.helper = helper;
    }

    @Override
    public Class<? extends AutoCrafterMenu> getContainerClass() {
        return AutoCrafterMenu.class;
    }

    @Override
    public Optional<ScreenHandlerType<AutoCrafterMenu>> getMenuType() {
        return Optional.of(EPMenuTypes.AUTO_CRAFTER_MENU);
    }

    @Override
    public IRecipeType<RecipeEntry<CraftingRecipe>> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(AutoCrafterMenu container, RecipeEntry<CraftingRecipe> recipe, IRecipeSlotsView recipeSlots, PlayerEntity player,
                                                         boolean maxTransfer, boolean doTransfer) {
        //TODO fix: Improve check for non shaped recipes
        if((recipe.value() instanceof ShapedRecipe shapedRecipe && (shapedRecipe.getWidth() > 3 || shapedRecipe.getHeight() > 3)) ||
                recipe.value().getDisplays().size() > 9)
            return helper.createUserErrorWithTooltip(Text.translatable("recipes.energizedpower.transfer.too_large"));

        if(!doTransfer)
            return null;

        List<ItemStack> itemStacks = new ArrayList<>(9);

        List<IRecipeSlotView> inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
        int len = Math.min(inputSlots.size(), 9);
        for(int i = 0;i < len;i++)
            itemStacks.add(inputSlots.get(i).getDisplayedItemStack().orElse(ItemStack.EMPTY).copy());

        ModMessages.sendClientPacketToServer(new SetAutoCrafterPatternInputSlotsC2SPacket(container.getBlockEntity().getPos(), itemStacks, recipe.id().getValue()));

        return null;
    }
}
