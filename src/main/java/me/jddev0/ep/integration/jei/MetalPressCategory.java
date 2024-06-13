package me.jddev0.ep.integration.jei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.MetalPressRecipe;
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

public class MetalPressCategory implements IRecipeCategory<RecipeHolder<MetalPressRecipe>> {
    public static final RecipeType<RecipeHolder<MetalPressRecipe>> TYPE = RecipeType.createFromVanilla(MetalPressRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public MetalPressCategory(IGuiHelper helper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(EnergizedPowerMod.MODID, "textures/gui/container/metal_press.png");
        background = helper.createDrawable(texture, 47, 22, 98, 34);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CRUSHER_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<MetalPressRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.metal_press");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<MetalPressRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 13).addItemStacks(
                Arrays.stream(recipe.value().getInput().getItems()).
                        map(itemStack -> itemStack.copyWithCount(recipe.value().getInputCount())).
                        collect(Collectors.toList()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.CATALYST, 37, 1).addItemStack(recipe.value().getPressMold());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 77, 13).addItemStack(recipe.value().getOutput());
    }
}
