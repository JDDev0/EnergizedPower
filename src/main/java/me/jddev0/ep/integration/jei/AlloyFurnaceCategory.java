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
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AlloyFurnaceCategory implements IRecipeCategory<RecipeEntry<AlloyFurnaceRecipe>> {
    public static final IRecipeHolderType<AlloyFurnaceRecipe> TYPE = IRecipeHolderType.create(AlloyFurnaceRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public AlloyFurnaceCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 189, 147, 37);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.ALLOY_FURNACE_ITEM));
    }

    @Override
    public IRecipeType<RecipeEntry<AlloyFurnaceRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.alloy_furnace");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeEntry<AlloyFurnaceRecipe> recipe, IFocusGroup iFocusGroup) {
        int len = Math.min(recipe.value().getInputs().length, 3);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = recipe.value().getInputs()[i];

            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1 + 18 * i, 5).
                    addItemStacks(input.input().getMatchingItems().
                            map(RegistryEntry::getKeyOrValue).
                            map(registryKeyItemEither -> registryKeyItemEither.map(
                                    l -> new ItemStack(MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(RegistryKeys.ITEM).getOrThrow(l)),
                                    ItemStack::new
                            )).
                            map(itemStack -> itemStack.copyWithCount(input.count())).
                            collect(Collectors.toList()));
        }

        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts();

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 100, 5).add(outputEntries[0]);
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 126, 5).
                addItemStacks(outputEntries[1].isEmpty()?List.of():List.of(outputEntries[1])).
                addRichTooltipCallback((view, tooltip) -> {
                    if(view.isEmpty())
                        return;

                    tooltip.add(Text.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.value().getSecondaryOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });
    }

    @Override
    public void draw(RecipeEntry<AlloyFurnaceRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int ticks = (int)(recipe.value().getTicks() * AlloyFurnaceBlockEntity.RECIPE_DURATION_MULTIPLIER);
        Text component = Text.translatable("recipes.energizedpower.info.ticks", ticks);
        int textWidth = font.getWidth(component);

        guiGraphics.drawText(MinecraftClient.getInstance().textRenderer, component, 147 - textWidth, 29, 0xFFFFFFFF, false);
    }
}
