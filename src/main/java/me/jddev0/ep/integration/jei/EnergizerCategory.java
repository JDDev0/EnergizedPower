package me.jddev0.ep.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EnergizerBlockEntity;
import me.jddev0.ep.recipe.EnergizerRecipe;
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

public class EnergizerCategory implements IRecipeCategory<EnergizerRecipe> {
    public static final ResourceLocation UID = EPAPI.id("energizer");
    public static final RecipeType<EnergizerRecipe> TYPE = new RecipeType<>(UID, EnergizerRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public EnergizerCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/container/energizer.png");
        background = helper.createDrawable(texture, 31, 18, 114, 50);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.ENERGIZER_ITEM.get()));
    }

    @Override
    public RecipeType<EnergizerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.energizer");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, EnergizerRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 17, 17).addIngredients(recipe.getInput());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 93, 17).addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(EnergizerRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        int energyConsumption = (int)(recipe.getEnergyConsumption() * EnergizerBlockEntity.ENERGY_CONSUMPTION_MULTIPLIER);
        Component component = Component.literal(EnergyUtils.getEnergyWithPrefix(energyConsumption)).withStyle(ChatFormatting.YELLOW);
        int textWidth = font.width(component);

        Minecraft.getInstance().font.draw(matrixStack, component, 114 - textWidth, 42, 0xFFFFFFFF);
    }
}
