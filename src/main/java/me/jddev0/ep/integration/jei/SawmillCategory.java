package me.jddev0.ep.integration.jei;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.SawmillRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class SawmillCategory implements IRecipeCategory<SawmillRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnergizedPowerMod.MODID, "sawmill");

    private final IDrawable background;
    private final IDrawable icon;

    public SawmillCategory(IGuiHelper helper) {
        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/sawmill.png");
        background = helper.createDrawable(texture, 42, 30, 109, 26);

        icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.CRUSHER_ITEM.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends SawmillRecipe> getRecipeClass() {
        return SawmillRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("container.energizedpower.sawmill");
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
    public void setIngredients(SawmillRecipe recipe, IIngredients iIngredients) {
        iIngredients.setInputIngredients(recipe.getIngredients());
        if(recipe.getSawdustAmount() == 0)
            iIngredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.getOutput()));
        else
            iIngredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.getOutput(), new ItemStack(ModItems.SAWDUST.get(), recipe.getSawdustAmount())));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, SawmillRecipe recipe, IIngredients iIngredients) {
        iRecipeLayout.getItemStacks().init(0, true, 0, 4);

        iRecipeLayout.getItemStacks().init(1, false, 64, 4);
        iRecipeLayout.getItemStacks().init(2, false, 91, 4);

        iRecipeLayout.getItemStacks().set(iIngredients);

        if(recipe.getSawdustAmount() > 0)
            iRecipeLayout.getItemStacks().set(2, new ItemStack(ModItems.SAWDUST.get(), recipe.getSawdustAmount()));
    }
}
