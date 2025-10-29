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
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AssemblingMachineCategory implements IRecipeCategory<RecipeEntry<AssemblingMachineRecipe>> {
    public static final RecipeType<RecipeEntry<AssemblingMachineRecipe>> TYPE = RecipeType.createFromVanilla(AssemblingMachineRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public AssemblingMachineCategory(IGuiHelper helper) {
        Identifier texture = EPAPI.id("textures/gui/container/assembling_machine.png");
        background = helper.createDrawable(texture, 43, 18, 115, 54);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.ASSEMBLING_MACHINE_ITEM));
    }

    @Override
    public RecipeType<RecipeEntry<AssemblingMachineRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("container.energizedpower.assembling_machine");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeEntry<AssemblingMachineRecipe> recipe, IFocusGroup iFocusGroup) {
        int len = Math.min(recipe.value().getInputs().length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = recipe.value().getInputs()[i];

            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, i == 1?1:(i == 2?37:19), i == 0?1:(i == 3?37:19)).
                    addItemStacks(Arrays.stream(input.input().getMatchingStacks()).
                            map(itemStack -> itemStack.copyWithCount(input.count())).
                            collect(Collectors.toList()));
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 91, 19).addItemStack(recipe.value().getOutput());
    }
}
