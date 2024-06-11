package me.jddev0.ep.integration.jei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class FluidTransposerCategory implements IRecipeCategory<RecipeHolder<FluidTransposerRecipe>> {
    public static final RecipeType<RecipeHolder<FluidTransposerRecipe>> TYPE = RecipeType.createFromVanilla(FluidTransposerRecipe.Type.INSTANCE);

    private final IDrawable backgroundEmptying;
    private final IDrawable backgroundFilling;
    private final IDrawable icon;

    public FluidTransposerCategory(IGuiHelper helper) {
        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/recipe/misc_gui.png");
        backgroundEmptying = helper.createDrawable(texture, 1, 133, 143, 26);
        backgroundFilling = helper.createDrawable(texture, 1, 161, 143, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FLUID_TRANSPOSER_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<FluidTransposerRecipe>> getRecipeType() {
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
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.value().getInput());

            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addItemStack(recipe.value().getOutput());
            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 90, 5).addFluidStack(fluid.getFluid(),
                    fluid.getAmount(), fluid.getComponentsPatch());
        }else {
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.value().getInput());
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 19, 5).addFluidStack(fluid.getFluid(),
                    fluid.getAmount(), fluid.getComponentsPatch());

            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 90, 5).addItemStack(recipe.value().getOutput());
        }
    }

    @Override
    public void draw(RecipeHolder<FluidTransposerRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.FILLING) {
            backgroundFilling.draw(guiGraphics, 0, 0);
        }

        ItemStack output = new ItemStack(recipe.value().getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?Items.BUCKET:Items.WATER_BUCKET);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.f, 0.f, 100.f);

        guiGraphics.renderItem(output, 120, 5, 120 + 5 * 143);

        guiGraphics.pose().popPose();
    }

    @Override
    public List<Component> getTooltipStrings(RecipeHolder<FluidTransposerRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> components = new ArrayList<>();

        int tooltipX = 119;
        int tooltipY = 4;
        if(mouseX >= (double)(tooltipX - 1) && mouseX < (double)(tooltipX + 20 + 1) &&
                mouseY >= (double)(tooltipY - 1) && mouseY < (double)(tooltipY + 20 + 1)) {
            components.add(Component.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                    recipe.value().getMode().getSerializedName()));
        }

        return components;
    }
}
