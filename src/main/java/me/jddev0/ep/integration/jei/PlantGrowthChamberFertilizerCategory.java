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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class PlantGrowthChamberFertilizerCategory implements IRecipeCategory<RecipeHolder<PlantGrowthChamberFertilizerRecipe>> {
    public static final IRecipeHolderType<PlantGrowthChamberFertilizerRecipe> TYPE = IRecipeHolderType.create(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable fertilizerSlot;
    private final IDrawable icon;

    public PlantGrowthChamberFertilizerCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/container/plant_growth_chamber.png");
        fertilizerSlot = helper.createDrawable(texture, 34, 34, 18, 18);
        background = helper.createBlankDrawable(144, 30);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()));
    }

    @Override
    public IRecipeHolderType<PlantGrowthChamberFertilizerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<PlantGrowthChamberFertilizerRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(recipe.value().getInput());
    }

    @Override
    public void draw(RecipeHolder<PlantGrowthChamberFertilizerRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        fertilizerSlot.draw(guiGraphics, 0, 0);

        Font font = Minecraft.getInstance().font;
        Component component = Component.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer.speed_multiplier", recipe.value().getSpeedMultiplier());
        int textWidth = font.width(component);

        guiGraphics.drawString(Minecraft.getInstance().font, component, 144 - textWidth, 5, 0xFFFFFFFF, false);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer.energy_consumption_multiplier", recipe.value().getEnergyConsumptionMultiplier());
        textWidth = font.width(component);

        guiGraphics.drawString(Minecraft.getInstance().font, component, 144 - textWidth, 22, 0xFFFFFFFF, false);
    }
}
