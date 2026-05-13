package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import me.jddev0.ep.util.ItemStackUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class FluidFreezerCategory implements IRecipeCategory<RecipeHolder<FluidFreezerRecipe>> {
    public static final IRecipeHolderType<FluidFreezerRecipe> TYPE = IRecipeHolderType.create(FluidFreezerRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public FluidFreezerCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 77, 85, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.FLUID_FREEZER_ITEM));
    }

    @Override
    public IRecipeHolderType<FluidFreezerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.fluid_freezer");
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeHolder<FluidFreezerRecipe> recipe, IFocusGroup iFocusGroup) {
        FluidStack input = recipe.value().getInput();

        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).add(input.getFluid(),
                input.getDropletsAmount(), input.getFluidVariant().getComponentsPatch());

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).add(ItemStackUtils.fromNullableItemStackTemplate(recipe.value().getOutput()));
    }

    @Override
    public void draw(RecipeHolder<FluidFreezerRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }
}
