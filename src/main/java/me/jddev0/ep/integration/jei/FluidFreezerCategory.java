package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public class FluidFreezerCategory implements IRecipeCategory<RecipeHolder<FluidFreezerRecipe>> {
    public static final RecipeType<RecipeHolder<FluidFreezerRecipe>> TYPE = RecipeType.createFromVanilla(FluidFreezerRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public FluidFreezerCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 77, 85, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.FLUID_FREEZER_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<FluidFreezerRecipe>> getRecipeType() {
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
        IRecipeSlotBuilder inputSlot = iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5);
        recipe.value().getInput().map(fluid -> {
            inputSlot.addFluidStack(fluid.getFluid(), fluid.getAmount(), fluid.getComponentsPatch());

            return null;
        }, f -> {
            int amount = f.amount();
            List<Fluid> fluids = f.fluid().getFluid().map(
                    fluid -> fluid,
                    fluid -> Minecraft.getInstance().level.registryAccess().lookupOrThrow(BuiltInRegistries.FLUID.key()).
                            getOrThrow(fluid).stream().map(Holder::value).toList()
            );

            for(Fluid fluid:fluids)
                inputSlot.addFluidStack(fluid, amount);

            return null;
        });

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addItemStack(recipe.value().getOutput());
    }

    @Override
    public void draw(RecipeHolder<FluidFreezerRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }
}
