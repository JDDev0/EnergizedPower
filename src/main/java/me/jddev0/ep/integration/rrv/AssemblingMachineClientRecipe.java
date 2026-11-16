package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.jddev0.ep.recipe.CompressorRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AssemblingMachineClientRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final List<SlotContent> inputs;
    private final SlotContent output;

    public AssemblingMachineClientRecipe(RecipeHolder<AssemblingMachineRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.inputs = Arrays.stream(recipe.value().getInputs()).map(input ->
                SlotContent.of(input.input().items().
                        map(Holder::unwrap).
                        map(registryKeyItemEither -> registryKeyItemEither.map(
                                l -> new ItemStack(ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(l)),
                                ItemStack::new
                        )).
                        map(itemStack -> itemStack.copyWithCount(input.count())).
                        collect(Collectors.toList()))).collect(Collectors.toList());
        this.output = SlotContent.of(recipe.value().getOutput());
    }

    @Override
    public ReliableClientRecipeType getType() {
        return AssemblingMachineClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        for(int i = 0;i < this.inputs.size();i++)
            slotFillContext.bindSlot(i, this.inputs.get(i));
        slotFillContext.bindSlot(4, this.output);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.copyOf(this.inputs);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.output);
    }
}
