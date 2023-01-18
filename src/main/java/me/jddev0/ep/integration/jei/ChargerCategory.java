package me.jddev0.ep.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.util.EnergyUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ChargerCategory implements IRecipeCategory<ChargerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnergizedPowerMod.MODID, "charger");
    public static final RecipeType<ChargerRecipe> TYPE = new RecipeType<>(UID, ChargerRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ChargerCategory(IGuiHelper helper) {
        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/charger.png");
        background = helper.createDrawable(texture, 65, 20, 86, 46);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CHARGER_ITEM.get()));
    }

    @Override
    public RecipeType<ChargerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.charger");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ChargerRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 15, 15).addIngredients(recipe.getInput());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 61, 15).addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(ChargerRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        //TODO add progress arrow

        Font font = Minecraft.getInstance().font;
        Component component = Component.literal(EnergyUtils.getEnergyWithPrefix(recipe.getEnergyConsumption())).withStyle(ChatFormatting.YELLOW);
        int textWidth = font.width(component);

        Minecraft.getInstance().font.draw(matrixStack, component, 86 - textWidth, 38, 0xFFFFFFFF);
    }
}
