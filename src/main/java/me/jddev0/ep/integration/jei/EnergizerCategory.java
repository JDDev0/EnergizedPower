package me.jddev0.ep.integration.jei;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class EnergizerCategory implements IRecipeCategory<EnergizerRecipe> {
    public static final Identifier UID = EPAPI.id("energizer");
    public static final RecipeType<EnergizerRecipe> TYPE = new RecipeType<>(UID, EnergizerRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public EnergizerCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/container/energizer.png");
        background = helper.createDrawable(texture, 31, 18, 114, 50);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.ENERGIZER_ITEM));
    }

    @Override
    public RecipeType<EnergizerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.energizer");
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
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 17, 17).addIngredients(recipe.getInputItem());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 93, 17).addItemStack(recipe.getOutputItem());
    }

    @Override
    public void draw(EnergizerRecipe recipe, IRecipeSlotsView iRecipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int energyConsumption = (int)(recipe.getEnergyConsumption() * EnergizerBlockEntity.ENERGY_CONSUMPTION_MULTIPLIER);
        Text component = Text.literal(EnergyUtils.getEnergyWithPrefix(energyConsumption)).formatted(Formatting.YELLOW);
        int textWidth = font.getWidth(component);

        guiGraphics.drawText(MinecraftClient.getInstance().textRenderer, component, 114 - textWidth, 42, 0xFFFFFFFF, false);
    }
}
