package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FluidTransposerCategory implements IRecipeCategory<FluidTransposerRecipe> {
    public static final Identifier UID = EPAPI.id("fluid_transposer");
    public static final RecipeType<FluidTransposerRecipe> TYPE = new RecipeType<>(UID, FluidTransposerRecipe.class);

    private final IDrawable backgroundEmptying;
    private final IDrawable backgroundFilling;
    private final IDrawable icon;

    public FluidTransposerCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        backgroundEmptying = helper.createDrawable(texture, 1, 133, 143, 26);
        backgroundFilling = helper.createDrawable(texture, 1, 161, 143, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.FLUID_TRANSPOSER_ITEM));
    }

    @Override
    public RecipeType<FluidTransposerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.fluid_transposer");
    }

    @Override
    public IDrawable getBackground() {
        return backgroundEmptying;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, FluidTransposerRecipe recipe, IFocusGroup iFocusGroup) {
        FluidStack fluid = recipe.getFluid();

        if(recipe.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.getInput());

            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addItemStack(recipe.getOutputItem());
            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 90, 5).addFluidStack(fluid.getFluid(),
                    fluid.getDropletsAmount(), fluid.getFluidVariant().getNbt());
        }else {
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.getInput());
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 19, 5).addFluidStack(fluid.getFluid(),
                    fluid.getDropletsAmount(), fluid.getFluidVariant().getNbt());

            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 90, 5).addItemStack(recipe.getOutputItem());
        }
    }

    @Override
    public void draw(FluidTransposerRecipe recipe, IRecipeSlotsView iRecipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        if(recipe.getMode() == FluidTransposerBlockEntity.Mode.FILLING) {
            backgroundFilling.draw(guiGraphics, 0, 0);
        }

        ItemStack output = new ItemStack(recipe.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?Items.BUCKET:Items.WATER_BUCKET);
        guiGraphics.getMatrices().push();
        guiGraphics.getMatrices().translate(0.f, 0.f, 100.f);

        guiGraphics.drawItem(output, 120, 5, 120 + 5 * 143);

        guiGraphics.getMatrices().pop();
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FluidTransposerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        int tooltipX = 119;
        int tooltipY = 4;
        if(mouseX >= (double)(tooltipX - 1) && mouseX < (double)(tooltipX + 20 + 1) &&
                mouseY >= (double)(tooltipY - 1) && mouseY < (double)(tooltipY + 20 + 1)) {
            tooltip.add(Text.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                    recipe.getMode().asString()));
        }
    }
}
