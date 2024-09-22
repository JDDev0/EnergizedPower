package me.jddev0.ep.integration.jei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.util.ItemStackUtils;
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

import java.util.Arrays;
import java.util.stream.Collectors;

public class AssemblingMachineCategory implements IRecipeCategory<AssemblingMachineRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnergizedPowerMod.MODID, "assembling_machine");
    public static final RecipeType<AssemblingMachineRecipe> TYPE = new RecipeType<>(UID, AssemblingMachineRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public AssemblingMachineCategory(IGuiHelper helper) {
        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/assembling_machine.png");
        background = helper.createDrawable(texture, 43, 18, 115, 54);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ASSEMBLING_MACHINE_ITEM.get()));
    }

    @Override
    public RecipeType<AssemblingMachineRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, AssemblingMachineRecipe recipe, IFocusGroup iFocusGroup) {
        int len = Math.min(recipe.getInputs().length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = recipe.getInputs()[i];

            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, i == 1?1:(i == 2?37:19), i == 0?1:(i == 3?37:19)).
                    addItemStacks(Arrays.stream(input.input().getItems()).
                            map(itemStack -> ItemStackUtils.copyWithCount(itemStack, input.count())).
                            collect(Collectors.toList()));
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 91, 19).addItemStack(recipe.getOutput());
    }
}
