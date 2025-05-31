package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class InWorldCategory implements IRecipeCategory<InWorldCategory.InWorldRecipe> {
    public static final IRecipeType<InWorldRecipe> TYPE = IRecipeType.create(EPAPI.id("in_world"), InWorldRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public InWorldCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 1, 103, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.GRASS_BLOCK));
    }

    @Override
    public IRecipeType<InWorldRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipes.energizedpower.in_world_crafting");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, InWorldRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 1, 5).add(recipe.tool());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 19, 5).add(recipe.block());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 82, 5).add(recipe.output());
    }

    record InWorldRecipe(Ingredient tool, Ingredient block, ItemStack output) {}
}
