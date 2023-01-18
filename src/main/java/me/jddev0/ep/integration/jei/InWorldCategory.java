package me.jddev0.ep.integration.jei;

import me.jddev0.ep.EnergizedPowerMod;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class InWorldCategory implements IRecipeCategory<InWorldCategory.InWorldRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnergizedPowerMod.MODID, "in_world");
    public static final RecipeType<InWorldRecipe> TYPE = new RecipeType<>(UID, InWorldRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public InWorldCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(100, 25);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.GRASS_BLOCK));
    }

    @Override
    public RecipeType<InWorldRecipe> getRecipeType() {
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
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 0, 5).addIngredients(recipe.tool());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 19, 5).addIngredients(recipe.block());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 81, 5).addItemStack(recipe.output());
    }

    //TODO draw slots

    record InWorldRecipe(Ingredient tool, Ingredient block, ItemStack output) {}
}
