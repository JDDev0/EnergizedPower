package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.recipe.CrusherRecipe;
import me.jddev0.ep.recipe.MetalPressRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.stream.Collectors;

public class MetalPressClientRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final SlotContent input, pressMold, output;

    public MetalPressClientRecipe(RecipeHolder<MetalPressRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.input = SlotContent.of(recipe.value().getInput().input().items().
                map(Holder::unwrap).
                map(registryKeyItemEither -> registryKeyItemEither.map(
                        l -> new ItemStack(ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(l)),
                        ItemStack::new
                )).
                map(itemStack -> itemStack.copyWithCount(recipe.value().getInput().count())).
                collect(Collectors.toList()));
        this.pressMold = SlotContent.of(recipe.value().getPressMold());
        this.output = SlotContent.of(recipe.value().getOutput());
    }

    @Override
    public ReliableClientRecipeType getType() {
        return MetalPressClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.pressMold);
        slotFillContext.bindSlot(2, this.output);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input, this.pressMold);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.output);
    }
}
