package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTransposerCategory implements IRecipeCategory<RecipeHolder<FluidTransposerRecipe>> {
    public static final IRecipeHolderType<FluidTransposerRecipe> TYPE = IRecipeHolderType.create(FluidTransposerRecipe.Type.INSTANCE);

    private final IDrawable backgroundEmptying;
    private final IDrawable backgroundFilling;
    private final IDrawable icon;

    public FluidTransposerCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        backgroundEmptying = helper.createDrawable(texture, 1, 133, 143, 26);
        backgroundFilling = helper.createDrawable(texture, 1, 161, 143, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.FLUID_TRANSPOSER_ITEM.get()));
    }

    @Override
    public IRecipeHolderType<FluidTransposerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.fluid_transposer");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeHolder<FluidTransposerRecipe> recipe, IFocusGroup iFocusGroup) {
        FluidStack fluid = recipe.value().getFluid();

        if(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).add(recipe.value().getInput());

            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).add(recipe.value().getOutput());
            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 90, 5).add(fluid.getFluid(),
                    fluid.getAmount(), fluid.getComponentsPatch());
        }else {
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).add(recipe.value().getInput());
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 19, 5).add(fluid.getFluid(),
                    fluid.getAmount(), fluid.getComponentsPatch());

            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 90, 5).add(recipe.value().getOutput());
        }
    }

    @Override
    public void draw(RecipeHolder<FluidTransposerRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.FILLING) {
            backgroundFilling.draw(guiGraphics, 0, 0);
        }

        ItemStack output = new ItemStack(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?Items.BUCKET:Items.WATER_BUCKET);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0.f, 0.f);

        guiGraphics.renderItem(output, 120, 5, 120 + 5 * 143);

        guiGraphics.pose().popMatrix();
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<FluidTransposerRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        int tooltipX = 119;
        int tooltipY = 4;
        if(mouseX >= (double)(tooltipX - 1) && mouseX < (double)(tooltipX + 20 + 1) &&
                mouseY >= (double)(tooltipY - 1) && mouseY < (double)(tooltipY + 20 + 1)) {
            tooltip.add(Component.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                    recipe.value().getMode().getSerializedName()));
        }
    }
}
