package me.jddev0.ep.integration.jei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.CompressorRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CompressorCategory implements IRecipeCategory<RecipeHolder<CompressorRecipe>> {
    public static final RecipeType<RecipeHolder<CompressorRecipe>> TYPE = RecipeType.createFromVanilla(CompressorRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public CompressorCategory(IGuiHelper helper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "textures/gui/container/compressor.png");
        background = helper.createDrawable(texture, 47, 30, 98, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.COMPRESSOR_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<CompressorRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.compressor");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<CompressorRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStacks(
                Arrays.stream(recipe.value().getInput().getItems()).
                        map(itemStack -> itemStack.copyWithCount(recipe.value().getInputCount())).
                        collect(Collectors.toList()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 77, 5).addItemStack(recipe.value().getOutput());
    }
}
