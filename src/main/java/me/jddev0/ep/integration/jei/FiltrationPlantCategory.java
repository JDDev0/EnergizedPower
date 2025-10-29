package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FiltrationPlantBlockEntity;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Locale;

public class FiltrationPlantCategory implements IRecipeCategory<RecipeEntry<FiltrationPlantRecipe>> {
    public static final RecipeType<RecipeEntry<FiltrationPlantRecipe>> TYPE = RecipeType.createFromVanilla(FiltrationPlantRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public FiltrationPlantCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 105, 112, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.FILTRATION_PLANT_ITEM));
    }

    @Override
    public RecipeType<RecipeEntry<FiltrationPlantRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.filtration_plant");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeEntry<FiltrationPlantRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addFluidStack(EPFluids.DIRTY_WATER,
                FiltrationPlantBlockEntity.DIRTY_WATER_CONSUMPTION_PER_RECIPE);

        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts();

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addItemStack(outputEntries[0]).
                addRichTooltipCallback((view, tooltip) -> {
                    tooltip.add(Text.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.value().getOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 92, 5).
                addItemStacks(outputEntries[1].isEmpty()? List.of():List.of(outputEntries[1])).
                addRichTooltipCallback((view, tooltip) -> {
                    if(view.isEmpty())
                        return;

                    tooltip.add(Text.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.value().getSecondaryOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });
    }
}
