package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
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

import java.util.List;
import java.util.Locale;

public class FiltrationPlantCategory implements IRecipeCategory<FiltrationPlantRecipe> {
    public static final ResourceLocation UID = EPAPI.id("filtration_plant");
    public static final RecipeType<FiltrationPlantRecipe> TYPE = new RecipeType<>(UID, FiltrationPlantRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public FiltrationPlantCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 105, 112, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FILTRATION_PLANT_ITEM.get()));
    }

    @Override
    public RecipeType<FiltrationPlantRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.filtration_plant");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, FiltrationPlantRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addFluidStack(ModFluids.DIRTY_WATER.get(),
                FiltrationPlantBlockEntity.DIRTY_WATER_CONSUMPTION_PER_RECIPE);

        ItemStack[] outputEntries = recipe.getMaxOutputCounts();

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addItemStack(outputEntries[0]).
                addTooltipCallback((view, tooltip) -> {
                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.getOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 92, 5).
                addItemStacks(outputEntries[1].isEmpty()? List.of():List.of(outputEntries[1])).
                addTooltipCallback((view, tooltip) -> {
                    if(view.isEmpty())
                        return;

                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.getSecondaryOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });
    }
}
