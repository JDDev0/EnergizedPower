package me.jddev0.ep.mixin.recipe;

import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.ServerRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerRecipeManager.class)
public interface ServerRecipeManagerGetter {
    @Accessor("preparedRecipes")
    PreparedRecipes getPreparedRecipes();
}
