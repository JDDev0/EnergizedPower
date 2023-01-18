package me.jddev0.ep.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.ChargerRecipe;
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

import java.util.ArrayList;

public class ChargerCategory implements IRecipeCategory<ChargerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnergizedPowerMod.MODID, "charger");

    private final IDrawable background;
    private final IDrawable icon;

    public ChargerCategory(IGuiHelper helper) {
        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/charger.png");
        background = helper.createDrawable(texture, 65, 20, 86, 46);

        icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.CHARGER_ITEM.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends ChargerRecipe> getRecipeClass() {
        return ChargerRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("container.energizedpower.charger");
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
    public void setIngredients(ChargerRecipe recipe, IIngredients iIngredients) {
        iIngredients.setInputIngredients(recipe.getIngredients());
        iIngredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, ChargerRecipe recipe, IIngredients iIngredients) {
        iRecipeLayout.getItemStacks().init(0, true, 14, 14);

        iRecipeLayout.getItemStacks().init(1, false, 60, 14);

        iRecipeLayout.getItemStacks().set(iIngredients);
    }

    @Override
    public void draw(ChargerRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        //TODO add progress arrow

        Font font = Minecraft.getInstance().font;
        Component component = new TextComponent(EnergyUtils.getEnergyWithPrefix(recipe.getEnergyConsumption())).withStyle(ChatFormatting.YELLOW);
        int textWidth = font.width(component);

        Minecraft.getInstance().font.draw(matrixStack, component, 86 - textWidth, 38, 0xFFFFFFFF);
    }
}
