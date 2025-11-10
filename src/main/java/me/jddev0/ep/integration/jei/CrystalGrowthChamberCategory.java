package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.CrystalGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.CrystalGrowthChamberRecipe;
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

import java.util.Locale;
import java.util.stream.Collectors;

public class CrystalGrowthChamberCategory implements IRecipeCategory<RecipeEntry<CrystalGrowthChamberRecipe>> {
    public static final IRecipeHolderType<CrystalGrowthChamberRecipe> TYPE = IRecipeHolderType.create(CrystalGrowthChamberRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public CrystalGrowthChamberCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/container/crystal_growth_chamber.png");
        background = helper.createDrawable(texture, 47, 30, 98, 38);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM));
    }

    @Override
    public IRecipeType<RecipeEntry<CrystalGrowthChamberRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.crystal_growth_chamber");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeEntry<CrystalGrowthChamberRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStacks(
                recipe.value().getInput().input().getMatchingItems().
                        map(RegistryEntry::getKeyOrValue).
                        map(registryKeyItemEither -> registryKeyItemEither.map(
                                l -> new ItemStack(MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(RegistryKeys.ITEM).getOrThrow(l)),
                                ItemStack::new
                        )).
                        map(itemStack -> itemStack.copyWithCount(recipe.value().getInput().count())).
                        collect(Collectors.toList()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 77, 5).add(recipe.value().getMaxOutputCount()).
                addRichTooltipCallback((view, tooltip) -> {
                    tooltip.add(Text.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.value().getOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });
    }

    @Override
    public void draw(RecipeEntry<CrystalGrowthChamberRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int ticks = (int)(recipe.value().getTicks() * CrystalGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
        Text component = Text.translatable("recipes.energizedpower.info.ticks", ticks);
        int textWidth = font.getWidth(component);

        guiGraphics.drawText(MinecraftClient.getInstance().textRenderer, component, 98 - textWidth, 30, 0xFFFFFFFF, false);
    }
}
