package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.PlantGrowthChamberFertilizerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PlantGrowthChamberFertilizerCategory implements IRecipeCategory<RecipeEntry<PlantGrowthChamberFertilizerRecipe>> {
    public static final IRecipeHolderType<PlantGrowthChamberFertilizerRecipe> TYPE = IRecipeHolderType.create(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable fertlizerSlot;
    private final IDrawable icon;

    public PlantGrowthChamberFertilizerCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/container/plant_growth_chamber.png");
        fertlizerSlot = helper.createDrawable(texture, 34, 34, 18, 18);
        background = helper.createBlankDrawable(144, 30);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM));
    }

    @Override
    public IRecipeType<RecipeEntry<PlantGrowthChamberFertilizerRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeEntry<PlantGrowthChamberFertilizerRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(recipe.value().getInput());
    }

    @Override
    public void draw(RecipeEntry<PlantGrowthChamberFertilizerRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        fertlizerSlot.draw(guiGraphics, 0, 0);

        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        Text component = Text.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer.speed_multiplier", recipe.value().getSpeedMultiplier());
        int textWidth = font.getWidth(component);

        guiGraphics.drawText(MinecraftClient.getInstance().textRenderer, component, 144 - textWidth, 5, 0xFFFFFFFF, false);

        component = Text.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer.energy_consumption_multiplier", recipe.value().getEnergyConsumptionMultiplier());
        textWidth = font.getWidth(component);

        guiGraphics.drawText(MinecraftClient.getInstance().textRenderer, component, 144 - textWidth, 22, 0xFFFFFFFF, false);
    }
}
