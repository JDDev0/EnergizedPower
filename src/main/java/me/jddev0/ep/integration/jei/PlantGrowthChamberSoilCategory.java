package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.PlantGrowthChamberSoilRecipe;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class PlantGrowthChamberSoilCategory implements IRecipeCategory<RecipeHolder<PlantGrowthChamberSoilRecipe>> {
    public static final IRecipeHolderType<PlantGrowthChamberSoilRecipe> TYPE = IRecipeHolderType.create(PlantGrowthChamberSoilRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable soilSlot;
    private final IDrawable icon;

    public PlantGrowthChamberSoilCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        soilSlot = helper.createDrawable(texture, 147, 10, 18, 18);
        background = helper.createBlankDrawable(144, 80);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM));
    }

    @Override
    public IRecipeHolderType<PlantGrowthChamberSoilRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipes.energizedpower.plant_growth_chamber_soil");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<PlantGrowthChamberSoilRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(recipe.value().getInput());
    }

    @Override
    public void draw(RecipeHolder<PlantGrowthChamberSoilRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        soilSlot.draw(guiGraphics, 0, 0);

        Font font = Minecraft.getInstance().font;


        SoilType soilType = Minecraft.getInstance().level.registryAccess().
                lookupOrThrow(EPRegistries.SOIL_TYPE).getOrThrow(recipe.value().getSoilType()).value();

        Component component = Component.translatable("recipes.energizedpower.plant_growth_chamber.soil_type", "");
        int textWidth = font.width(component);

        guiGraphics.text(font, component, 1, 22, 0xFFFFFFFF, true);
        guiGraphics.text(font, soilType.displayName(), 1 + textWidth, 23, 0xFFFFFFFF, false);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.speed_multiplier", recipe.value().getSpeedMultiplier());
        guiGraphics.text(font, component, 1, 37, 0xFFFFFFFF, true);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.fluid_consumption_multiplier", recipe.value().getFluidConsumptionMultiplier());
        guiGraphics.text(font, component, 1, 52, 0xFFFFFFFF, true);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.energy_consumption_multiplier", recipe.value().getEnergyConsumptionMultiplier());
        guiGraphics.text(font, component, 1, 67, 0xFFFFFFFF, true);
    }
}
