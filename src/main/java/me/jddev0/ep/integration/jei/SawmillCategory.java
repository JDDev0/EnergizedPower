package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.SawmillRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class SawmillCategory implements IRecipeCategory<RecipeHolder<SawmillRecipe>> {
    public static final RecipeType<RecipeHolder<SawmillRecipe>> TYPE = RecipeType.createFromVanilla(SawmillRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public SawmillCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/container/sawmill.png");
        background = helper.createDrawable(texture, 42, 30, 109, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.SAWMILL_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<SawmillRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.sawmill");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeHolder<SawmillRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.value().getInput());

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 65, 5).addItemStack(recipe.value().getOutput());

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 92, 5).
                addItemStacks(recipe.value().getSecondaryOutput().isEmpty()?new ArrayList<>(0):List.of(recipe.value().getSecondaryOutput()));
    }
}
