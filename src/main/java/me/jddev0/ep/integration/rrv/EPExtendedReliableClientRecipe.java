package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;

//TODO remove class once feature introduced in RRV
//TODO !ALSO REMOVE me.jddev0.mixin.integration.rrv.RecipeViewMenuMixin and me.jddev0.mixin.integration.rrv.RecipeTransferDataAccessor
public interface EPExtendedReliableClientRecipe extends ReliableClientRecipe {
    default boolean energizedpower$ignoreRecipeRequirementsForTransfer() {
        return false;
    }

    default void energizedpower$renderRecipeOverlay(RecipeScreenContext context) {}
}
