package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AlloyFurnaceBlockEntity;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.recipe.IngredientWithCount;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AlloyFurnaceCategory implements IRecipeCategory<RecipeHolder<AlloyFurnaceRecipe>> {
    public static final RecipeType<RecipeHolder<AlloyFurnaceRecipe>> TYPE = RecipeType.createFromVanilla(AlloyFurnaceRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public AlloyFurnaceCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 189, 147, 37);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.ALLOY_FURNACE_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<AlloyFurnaceRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.alloy_furnace");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<AlloyFurnaceRecipe> recipe, IFocusGroup iFocusGroup) {
        int len = Math.min(recipe.value().getInputs().length, 3);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = recipe.value().getInputs()[i];

            /*TODO fix
            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1 + 18 * i, 5).
                    addItemStacks(Arrays.stream(input.input().getItems()).
                            map(itemStack -> itemStack.copyWithCount(input.count())).
                            collect(Collectors.toList()));*/
        }

        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts();

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 100, 5).addItemStack(outputEntries[0]);
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 126, 5).
                addItemStacks(outputEntries[1].isEmpty()?List.of():List.of(outputEntries[1])).
                addRichTooltipCallback((view, tooltip) -> {
                    if(view.isEmpty())
                        return;

                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.value().getSecondaryOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });
    }

    @Override
    public void draw(RecipeHolder<AlloyFurnaceRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        int ticks = (int)(recipe.value().getTicks() * AlloyFurnaceBlockEntity.RECIPE_DURATION_MULTIPLIER);
        Component component = Component.translatable("recipes.energizedpower.info.ticks", ticks);
        int textWidth = font.width(component);

        guiGraphics.drawString(Minecraft.getInstance().font, component, 147 - textWidth, 29, 0xFFFFFFFF, false);
    }
}
