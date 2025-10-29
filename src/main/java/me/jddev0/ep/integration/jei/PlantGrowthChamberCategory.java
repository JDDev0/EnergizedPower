package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public class PlantGrowthChamberCategory implements IRecipeCategory<PlantGrowthChamberRecipe> {
    public static final Identifier UID = EPAPI.id("plant_growth_chamber");
    public static final RecipeType<PlantGrowthChamberRecipe> TYPE = new RecipeType<>(UID, PlantGrowthChamberRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public PlantGrowthChamberCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/container/plant_growth_chamber.png");
        background = helper.createDrawable(texture, 61, 25, 108, 48);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM));
    }

    @Override
    public RecipeType<PlantGrowthChamberRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.plant_growth_chamber");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, PlantGrowthChamberRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 10).addIngredients(recipe.getInput());

        List<List<ItemStack>> outputSlotEntries = new ArrayList<>(4);
        for(int i = 0;i < 4;i++)
            outputSlotEntries.add(new ArrayList<>());

        ItemStack[] outputEntries = recipe.getMaxOutputCounts();
        for(int i = 0;i < outputEntries.length;i++)
            outputSlotEntries.get(i % 4).add(outputEntries[i]);

        IRecipeSlotRichTooltipCallback callback = (view, tooltip) -> {
            if(view.isEmpty())
                return;

            Optional<ItemStack> optionalItemStack = view.getDisplayedItemStack();
            if(optionalItemStack.isEmpty())
                return;

            tooltip.add(Text.translatable("recipes.energizedpower.transfer.output_percentages"));

            OutputItemStackWithPercentages[] outputs = recipe.getOutputs();
            for(int i = 0;i < outputs.length;i++) {
                if(ItemStack.canCombine(optionalItemStack.get(), outputs[i].output())) {
                    double[] percentages = outputs[i].percentages();
                    for(int j = 0;j < percentages.length;j++)
                        tooltip.add(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

                    return;
                }
            }
        };

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 1).addItemStacks(outputSlotEntries.get(0)).
                addRichTooltipCallback(callback);
        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 91, 1).addItemStacks(outputSlotEntries.get(1)).
                addRichTooltipCallback(callback);
        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 19).addItemStacks(outputSlotEntries.get(2)).
                addRichTooltipCallback(callback);
        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 91, 19).addItemStacks(outputSlotEntries.get(3)).
                addRichTooltipCallback(callback);
    }

    @Override
    public void draw(PlantGrowthChamberRecipe recipe, IRecipeSlotsView iRecipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int ticks = (int)(recipe.getTicks() * PlantGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
        Text component = Text.translatable("recipes.energizedpower.info.ticks", ticks);
        int textWidth = font.getWidth(component);

        guiGraphics.drawText(MinecraftClient.getInstance().textRenderer, component, 108 - textWidth, 40, 0xFFFFFFFF, false);
    }
}
