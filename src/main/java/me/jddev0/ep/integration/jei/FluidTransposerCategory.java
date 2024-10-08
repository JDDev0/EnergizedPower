package me.jddev0.ep.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class FluidTransposerCategory implements IRecipeCategory<FluidTransposerRecipe> {
    public static final ResourceLocation UID = EPAPI.id("fluid_transposer");
    public static final RecipeType<FluidTransposerRecipe> TYPE = new RecipeType<>(UID, FluidTransposerRecipe.class);

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
    public RecipeType<FluidTransposerRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, FluidTransposerRecipe recipe, IFocusGroup iFocusGroup) {
        FluidStack fluid = recipe.getFluid();

        if(recipe.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING) {
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.getInput());

            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addItemStack(recipe.getOutput());
            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 90, 5).addFluidStack(fluid.getFluid(),
                    fluid.getAmount(), fluid.getTag());
        }else {
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.getInput());
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 19, 5).addFluidStack(fluid.getFluid(),
                    fluid.getAmount(), fluid.getTag());

            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 90, 5).addItemStack(recipe.getOutput());
        }
    }

    @Override
    public void draw(FluidTransposerRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        if(recipe.getMode() == FluidTransposerBlockEntity.Mode.FILLING) {
            backgroundFilling.draw(poseStack, 0, 0);
        }

        ItemStack output = new ItemStack(recipe.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?Items.BUCKET:Items.WATER_BUCKET);
        poseStack.pushPose();
        poseStack.translate(0.f, 0.f, 100.f);

        itemRenderer.renderAndDecorateItem(output, 120, 5, 120 + 5 * 143);

        poseStack.popPose();
    }

    @Override
    public List<Component> getTooltipStrings(FluidTransposerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> components = new ArrayList<>();

        int tooltipX = 119;
        int tooltipY = 4;
        if(mouseX >= (double)(tooltipX - 1) && mouseX < (double)(tooltipX + 20 + 1) &&
                mouseY >= (double)(tooltipY - 1) && mouseY < (double)(tooltipY + 20 + 1)) {
            components.add(Component.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                    recipe.getMode().getSerializedName()));
        }

        return components;
    }
}
