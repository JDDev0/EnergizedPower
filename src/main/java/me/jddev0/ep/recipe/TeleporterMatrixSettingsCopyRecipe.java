package me.jddev0.ep.recipe;

import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class TeleporterMatrixSettingsCopyRecipe extends SpecialCraftingRecipe {
    public TeleporterMatrixSettingsCopyRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput container, World level) {
        ItemStack linkedTransportMatrix = ItemStack.EMPTY;
        int count = 0;

        for(int i = 0;i < container.getSize();i++) {
            ItemStack itemStack = container.getStackInSlot(i);
            if(!itemStack.isEmpty()) {
                if(!itemStack.isOf(EPItems.TELEPORTER_MATRIX))
                    return false;

                if(TeleporterMatrixItem.isLinked(itemStack)) {
                    if(!linkedTransportMatrix.isEmpty())
                        return false;

                    linkedTransportMatrix = itemStack;
                }else {
                    count++;

                    //Do not allow more than 64 copies
                    if(count > 64)
                        return false;
                }
            }
        }

        return !linkedTransportMatrix.isEmpty() && count > 0;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput container, RegistryWrapper.WrapperLookup registries) {
        ItemStack linkedTransportMatrix = ItemStack.EMPTY;
        int count = 0;

        for(int i = 0;i < container.getSize();i++) {
            ItemStack itemStack = container.getStackInSlot(i);
            if(!itemStack.isEmpty()) {
                if(!itemStack.isOf(EPItems.TELEPORTER_MATRIX))
                    return ItemStack.EMPTY;

                if(TeleporterMatrixItem.isLinked(itemStack)) {
                    if(!linkedTransportMatrix.isEmpty())
                        return ItemStack.EMPTY;

                    linkedTransportMatrix = itemStack;
                }else {
                    count++;

                    //Do not allow more than 64 copies
                    if(count > 64)
                        return ItemStack.EMPTY;
                }
            }
        }

        return (!linkedTransportMatrix.isEmpty() && count > 0)?linkedTransportMatrix.copyWithCount(count):
                ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return new ItemStack(EPItems.TELEPORTER_MATRIX, 2);
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingRecipeInput container) {
        DefaultedList<ItemStack> remainders = DefaultedList.ofSize(container.getSize(), ItemStack.EMPTY);

        for(int i = 0; i < remainders.size(); ++i) {
            ItemStack itemstack = container.getStackInSlot(i);
            if(!itemstack.isEmpty()) {
                if(!itemstack.getRecipeRemainder().isEmpty()) {
                    remainders.set(i, itemstack.getRecipeRemainder());
                }else if(itemstack.isOf(EPItems.TELEPORTER_MATRIX) && TeleporterMatrixItem.isLinked(itemstack)) {
                    remainders.set(i, itemstack.copyWithCount(1));
                }
            }
        }

        return remainders;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(EPItems.TELEPORTER_MATRIX);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EPRecipes.TELEPORTER_MATRIX_SETTINGS_COPY_SERIALIZER;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }
}
