package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.PulverizerRecipe;
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

import java.util.List;
import java.util.Locale;

public class PulverizerCategory implements IRecipeCategory<RecipeHolder<PulverizerRecipe>> {
    public static final RecipeType<RecipeHolder<PulverizerRecipe>> TYPE = RecipeType.createFromVanilla(PulverizerRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public PulverizerCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/container/pulverizer.png");
        background = helper.createDrawable(texture, 42, 30, 109, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.PULVERIZER_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<PulverizerRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.pulverizer");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeHolder<PulverizerRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.value().getInput());

        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts(false);

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 65, 5).addItemStack(outputEntries[0]).
                addTooltipCallback((view, tooltip) -> {
                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.value().getOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 92, 5).
                addItemStacks(outputEntries[1].isEmpty()?List.of():List.of(outputEntries[1])).
                addTooltipCallback((view, tooltip) -> {
                    if(view.isEmpty())
                        return;

                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.value().getSecondaryOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });
    }
}
