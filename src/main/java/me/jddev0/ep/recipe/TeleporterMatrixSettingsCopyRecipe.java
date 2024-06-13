package me.jddev0.ep.recipe;

import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TeleporterMatrixSettingsCopyRecipe extends CustomRecipe {
    public TeleporterMatrixSettingsCopyRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        ItemStack linkedTransportMatrix = ItemStack.EMPTY;
        int count = 0;

        for(int i = 0;i < container.size();i++) {
            ItemStack itemStack = container.getItem(i);
            if(!itemStack.isEmpty()) {
                if(!itemStack.is(ModItems.TELEPORTER_MATRIX.get()))
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
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
        ItemStack linkedTransportMatrix = ItemStack.EMPTY;
        int count = 0;

        for(int i = 0;i < container.size();i++) {
            ItemStack itemStack = container.getItem(i);
            if(!itemStack.isEmpty()) {
                if(!itemStack.is(ModItems.TELEPORTER_MATRIX.get()))
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
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(ModItems.TELEPORTER_MATRIX.get(), 2);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput container) {
        NonNullList<ItemStack> remainders = NonNullList.withSize(container.size(), ItemStack.EMPTY);

        for(int i = 0; i < remainders.size(); ++i) {
            ItemStack itemstack = container.getItem(i);
            if(!itemstack.isEmpty()) {
                if(itemstack.hasCraftingRemainingItem()) {
                    remainders.set(i, itemstack.getCraftingRemainingItem());
                }else if(itemstack.is(ModItems.TELEPORTER_MATRIX.get()) && TeleporterMatrixItem.isLinked(itemstack)) {
                    remainders.set(i, itemstack.copyWithCount(1));
                }
            }
        }

        return remainders;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModItems.TELEPORTER_MATRIX.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TELEPORTER_MATRIX_SETTINGS_COPY_SERIALIZER.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }
}
