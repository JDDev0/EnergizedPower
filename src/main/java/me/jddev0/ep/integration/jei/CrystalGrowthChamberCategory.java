package me.jddev0.ep.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.CrystalGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.CrystalGrowthChamberRecipe;
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

import java.util.Arrays;
import java.util.stream.Collectors;

public class CrystalGrowthChamberCategory implements IRecipeCategory<CrystalGrowthChamberRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnergizedPowerMod.MODID, "crystal_growth_chamber");
    public static final RecipeType<CrystalGrowthChamberRecipe> TYPE = new RecipeType<>(UID, CrystalGrowthChamberRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CrystalGrowthChamberCategory(IGuiHelper helper) {
        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/crystal_growth_chamber.png");
        background = helper.createDrawable(texture, 47, 30, 98, 38);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM.get()));
    }

    @Override
    public RecipeType<CrystalGrowthChamberRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.crystal_growth_chamber");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, CrystalGrowthChamberRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStacks(
                Arrays.stream(recipe.getInput().getItems()).
                        map(itemStack -> itemStack.copyWithCount(recipe.getInputCount())).
                        collect(Collectors.toList()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 77, 5).addItemStack(recipe.getMaxOutputCount());
    }

    @Override
    public void draw(CrystalGrowthChamberRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        int ticks = (int)(recipe.getTicks() * CrystalGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
        Component component = Component.translatable("recipes.energizedpower.info.ticks", ticks);
        int textWidth = font.width(component);

        Minecraft.getInstance().font.draw(matrixStack, component, 98 - textWidth, 30, 0xFFFFFFFF);
    }
}
