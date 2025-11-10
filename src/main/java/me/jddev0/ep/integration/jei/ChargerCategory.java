package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.ChargerBlockEntity;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.util.EnergyUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ChargerCategory implements IRecipeCategory<RecipeEntry<ChargerRecipe>> {
    public static final IRecipeHolderType<ChargerRecipe> TYPE = IRecipeHolderType.create(ChargerRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public ChargerCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 29, 113, 46);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.CHARGER_ITEM));
    }

    @Override
    public IRecipeType<RecipeEntry<ChargerRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.charger");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeEntry<ChargerRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 15, 15).add(recipe.value().getInputItem());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 92, 15).add(recipe.value().getOutputItem());
    }

    @Override
    public void draw(RecipeEntry<ChargerRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int energyConsumption = (int)(recipe.value().getEnergyConsumption() * ChargerBlockEntity.CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER);
        Text component = Text.literal(EnergyUtils.getEnergyWithPrefix(energyConsumption)).formatted(Formatting.YELLOW);
        int textWidth = font.getWidth(component);

        guiGraphics.drawText(MinecraftClient.getInstance().textRenderer, component, 113 - textWidth, 38, 0xFFFFFFFF, false);
    }
}
