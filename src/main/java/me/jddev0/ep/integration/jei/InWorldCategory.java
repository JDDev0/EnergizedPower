package me.jddev0.ep.integration.jei;

import me.jddev0.ep.EnergizedPowerMod;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

public class InWorldCategory implements IRecipeCategory<InWorldCategory.InWorldRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnergizedPowerMod.MODID, "in_world");

    private final IDrawable background;
    private final IDrawable icon;

    public InWorldCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(100, 25);

        icon = helper.createDrawableIngredient(new ItemStack(Items.GRASS_BLOCK));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends InWorldRecipe> getRecipeClass() {
        return InWorldRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("recipes.energizedpower.in_world_crafting");
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
    public void setIngredients(InWorldRecipe recipe, IIngredients iIngredients) {
        iIngredients.setInputIngredients(Arrays.asList(recipe.tool(), recipe.block()));
        iIngredients.setOutput(VanillaTypes.ITEM, recipe.output());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, InWorldRecipe recipe, IIngredients iIngredients) {
        iRecipeLayout.getItemStacks().init(0, true, 0, 4);
        iRecipeLayout.getItemStacks().init(1, true, 18, 4);

        iRecipeLayout.getItemStacks().init(2, false, 80, 4);

        iRecipeLayout.getItemStacks().set(iIngredients);
    }

    //TODO draw slots

    record InWorldRecipe(Ingredient tool, Ingredient block, ItemStack output) {}
}
