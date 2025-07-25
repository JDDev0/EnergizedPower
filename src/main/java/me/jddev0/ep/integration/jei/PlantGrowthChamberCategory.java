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
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;

public class PlantGrowthChamberCategory implements IRecipeCategory<RecipeHolder<PlantGrowthChamberRecipe>> {
    public static final IRecipeHolderType<PlantGrowthChamberRecipe> TYPE = IRecipeHolderType.create(PlantGrowthChamberRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public PlantGrowthChamberCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/container/plant_growth_chamber.png");
        background = helper.createDrawable(texture, 61, 25, 108, 48);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()));
    }

    @Override
    public IRecipeHolderType<PlantGrowthChamberRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.plant_growth_chamber");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeHolder<PlantGrowthChamberRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 10).add(recipe.value().getInput());

        List<List<ItemStack>> outputSlotEntries = new ArrayList<>(4);
        for(int i = 0;i < 4;i++)
            outputSlotEntries.add(new LinkedList<>());

        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts();
        for(int i = 0;i < outputEntries.length;i++)
            outputSlotEntries.get(i % 4).add(outputEntries[i]);

        IRecipeSlotRichTooltipCallback callback = (view, tooltip) -> {
            if(view.isEmpty())
                return;

            Optional<ItemStack> optionalItemStack = view.getDisplayedItemStack();
            if(optionalItemStack.isEmpty())
                return;

            tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

            OutputItemStackWithPercentages[] outputs = recipe.value().getOutputs();
            for(int i = 0;i < outputs.length;i++) {
                if(ItemStack.isSameItemSameComponents(optionalItemStack.get(), outputs[i].output())) {
                    double[] percentages = outputs[i].percentages();
                    for(int j = 0;j < percentages.length;j++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

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
    public void draw(RecipeHolder<PlantGrowthChamberRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        int ticks = (int)(recipe.value().getTicks() * PlantGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
        Component component = Component.translatable("recipes.energizedpower.info.ticks", ticks);
        int textWidth = font.width(component);

        guiGraphics.drawString(Minecraft.getInstance().font, component, 108 - textWidth, 40, 0xFFFFFFFF, false);
    }
}
