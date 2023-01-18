package me.jddev0.ep.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.EnergizerRecipe;
import me.jddev0.ep.util.EnergyUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class EnergizerCategory implements IRecipeCategory<EnergizerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnergizedPowerMod.MODID, "energizer");

    private final IDrawable background;
    private final IDrawable icon;

    public EnergizerCategory(IGuiHelper helper) {
        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/energizer.png");
        background = helper.createDrawable(texture, 31, 18, 114, 50);

        icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.ENERGIZER_ITEM.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends EnergizerRecipe> getRecipeClass() {
        return EnergizerRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("container.energizedpower.energizer");
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
    public void setIngredients(EnergizerRecipe recipe, IIngredients iIngredients) {
        iIngredients.setInputIngredients(recipe.getIngredients());
        iIngredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, EnergizerRecipe recipe, IIngredients iIngredients) {
        iRecipeLayout.getItemStacks().init(0, true, 16, 16);

        iRecipeLayout.getItemStacks().init(1, false, 92, 16);

        iRecipeLayout.getItemStacks().set(iIngredients);
    }

    @Override
    public void draw(EnergizerRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        Component component = new TextComponent(EnergyUtils.getEnergyWithPrefix(recipe.getEnergyConsumption())).withStyle(ChatFormatting.YELLOW);
        int textWidth = font.width(component);

        Minecraft.getInstance().font.draw(matrixStack, component, 114 - textWidth, 42, 0xFFFFFFFF);
    }
}
