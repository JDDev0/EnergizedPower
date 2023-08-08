package me.jddev0.ep.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlantGrowthChamberCategory implements IRecipeCategory<PlantGrowthChamberRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnergizedPowerMod.MODID, "plant_growth_chamber");
    public static final RecipeType<PlantGrowthChamberRecipe> TYPE = new RecipeType<>(UID, PlantGrowthChamberRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public PlantGrowthChamberCategory(IGuiHelper helper) {
        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/plant_growth_chamber.png");
        background = helper.createDrawable(texture, 61, 25, 108, 48);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()));
    }

    @Override
    public RecipeType<PlantGrowthChamberRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, PlantGrowthChamberRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 10).addIngredients(recipe.getInput());

        List<List<ItemStack>> outputSlotEntries = new ArrayList<>(4);
        for(int i = 0;i < 4;i++)
            outputSlotEntries.add(new LinkedList<>());

        ItemStack[] outputEntries = recipe.getMaxOutputCounts();
        for(int i = 0;i < outputEntries.length;i++)
            outputSlotEntries.get(i % 4).add(outputEntries[i]);

        if(!outputSlotEntries.get(0).isEmpty())
            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 1).addItemStacks(outputSlotEntries.get(0));
        if(!outputSlotEntries.get(1).isEmpty())
            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 91, 1).addItemStacks(outputSlotEntries.get(1));
        if(!outputSlotEntries.get(2).isEmpty())
            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 19).addItemStacks(outputSlotEntries.get(2));
        if(!outputSlotEntries.get(3).isEmpty())
            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 91, 19).addItemStacks(outputSlotEntries.get(3));
    }

    @Override
    public void draw(PlantGrowthChamberRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        int ticks = (int)(recipe.getTicks() * PlantGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
        Component component = Component.translatable("recipes.energizedpower.plant_growth_chamber.ticks", ticks);
        int textWidth = font.width(component);

        Minecraft.getInstance().font.draw(matrixStack, component, 108 - textWidth, 40, 0xFFFFFFFF);
    }
}
