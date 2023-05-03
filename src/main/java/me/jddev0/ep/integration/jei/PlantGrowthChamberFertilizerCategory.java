package me.jddev0.ep.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.PlantGrowthChamberFertilizerRecipe;
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
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class PlantGrowthChamberFertilizerCategory implements IRecipeCategory<PlantGrowthChamberFertilizerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnergizedPowerMod.MODID, "plant_growth_chamber_fertilizer");
    public static final RecipeType<PlantGrowthChamberFertilizerRecipe> TYPE = new RecipeType<>(UID, PlantGrowthChamberFertilizerRecipe.class);

    private final IDrawable background;
    private final IDrawable fertlizerSlot;
    private final IDrawable icon;

    public PlantGrowthChamberFertilizerCategory(IGuiHelper helper) {
        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/plant_growth_chamber.png");
        fertlizerSlot = helper.createDrawable(texture, 34, 34, 18, 18);
        background = helper.createBlankDrawable(144, 30);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()));
    }

    @Override
    public RecipeType<PlantGrowthChamberFertilizerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends PlantGrowthChamberFertilizerRecipe> getRecipeClass() {
        return PlantGrowthChamberFertilizerRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("recipes.energizedpower.plant_growth_chamber_fertilizer");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, PlantGrowthChamberFertilizerRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getInput());
    }

    @Override
    public void draw(PlantGrowthChamberFertilizerRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        fertlizerSlot.draw(matrixStack, 0, 0);

        Font font = Minecraft.getInstance().font;
        Component component = new TranslatableComponent("recipes.energizedpower.plant_growth_chamber_fertilizer.speed_multiplier", recipe.getSpeedMultiplier());
        int textWidth = font.width(component);

        Minecraft.getInstance().font.draw(matrixStack, component, 144 - textWidth, 5, 0xFFFFFFFF);

        component = new TranslatableComponent("recipes.energizedpower.plant_growth_chamber_fertilizer.energy_consumption_multiplier", recipe.getEnergyConsumptionMultiplier());
        textWidth = font.width(component);

        Minecraft.getInstance().font.draw(matrixStack, component, 144 - textWidth, 22, 0xFFFFFFFF);
    }
}
