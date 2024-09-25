package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;

public class PressMoldMakerCategory implements IRecipeCategory<RecipeHolder<PressMoldMakerRecipe>> {
    public static final ResourceLocation UID = EPAPI.id("press_mold_maker");
    public static final RecipeType<RecipeHolder<PressMoldMakerRecipe>> TYPE = RecipeType.createFromVanilla(PressMoldMakerRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public PressMoldMakerCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 77, 85, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.PRESS_MOLD_MAKER_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<PressMoldMakerRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.press_mold_maker");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<PressMoldMakerRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStack(new ItemStack(Items.CLAY_BALL, recipe.value().getClayCount()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addItemStack(recipe.value().getOutput());
    }
}
