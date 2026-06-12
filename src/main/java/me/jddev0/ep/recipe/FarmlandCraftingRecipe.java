package me.jddev0.ep.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FarmlandCraftingRecipe extends CustomRecipe {
    public FarmlandCraftingRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        int dirtCount = 0;
        int hoeCount = 0;

        for(int i = 0;i < container.size();i++) {
            ItemStack itemStack = container.getItem(i);
            if(!itemStack.isEmpty()) {
                if(itemStack.is(Items.DIRT)) {
                    dirtCount++;
                }else if(itemStack.is(ItemTags.HOES)) {
                    hoeCount++;
                }else {
                    return false;
                }
            }
        }

        return dirtCount == 1 && hoeCount == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
        return new ItemStack(Items.FARMLAND);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(Items.FARMLAND);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput container) {
        NonNullList<ItemStack> remainders = NonNullList.withSize(container.size(), ItemStack.EMPTY);

        for(int i = 0; i < remainders.size(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if(!itemStack.isEmpty()) {
                if(itemStack.is(ItemTags.HOES)) {
                    ItemStack copy = itemStack.copy();

                    //TODO fix for durability enchantment -> Get ServerWorld somehow and use instead of if:
                    //     "copy.hurtAndBreak(1, null, null, item -> copy.setCount(0));"
                    if(copy.isDamageableItem()) {
                        int d = copy.getDamageValue() + 1;
                        copy.setDamageValue(d);
                        if(d >= copy.getMaxDamage())
                            copy.setCount(0);
                    }

                    if(!copy.isEmpty())
                        remainders.set(i, copy);
                }
            }
        }

        return remainders;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Items.FARMLAND);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EPRecipes.FARMLAND_CRAFTING_RECIPE_SERIALIZER;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 || height >= 2;
    }
}
