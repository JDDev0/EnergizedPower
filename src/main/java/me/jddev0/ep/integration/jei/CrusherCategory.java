package me.jddev0.ep.integration.jei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.CrusherRecipe;
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

public class CrusherCategory implements IRecipeCategory<RecipeHolder<CrusherRecipe>> {
    public static final RecipeType<RecipeHolder<CrusherRecipe>> TYPE = RecipeType.createFromVanilla(CrusherRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public CrusherCategory(IGuiHelper helper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "textures/gui/container/crusher.png");
        background = helper.createDrawable(texture, 47, 30, 98, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CRUSHER_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<CrusherRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.crusher");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<CrusherRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.value().getInput());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 77, 5).addItemStack(recipe.value().getOutput());
    }
}
