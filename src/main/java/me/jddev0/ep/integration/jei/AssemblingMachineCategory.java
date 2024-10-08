package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.jddev0.ep.recipe.IngredientWithCount;
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

public class AssemblingMachineCategory implements IRecipeCategory<RecipeHolder<AssemblingMachineRecipe>> {
    public static final RecipeType<RecipeHolder<AssemblingMachineRecipe>> TYPE = RecipeType.createFromVanilla(AssemblingMachineRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public AssemblingMachineCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/container/assembling_machine.png");
        background = helper.createDrawable(texture, 43, 18, 115, 54);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.ASSEMBLING_MACHINE_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<AssemblingMachineRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.assembling_machine");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<AssemblingMachineRecipe> recipe, IFocusGroup iFocusGroup) {
        int len = Math.min(recipe.value().getInputs().length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = recipe.value().getInputs()[i];

            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, i == 1?1:(i == 2?37:19), i == 0?1:(i == 3?37:19)).
                    addItemStacks(Arrays.stream(input.input().getItems()).
                            map(itemStack -> itemStack.copyWithCount(input.count())).
                            collect(Collectors.toList()));
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 91, 19).addItemStack(recipe.value().getOutput());
    }
}
