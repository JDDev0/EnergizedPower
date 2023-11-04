package me.jddev0.ep.recipe;

import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TeleporterMatrixSettingsCopyRecipe extends CustomRecipe {
    public TeleporterMatrixSettingsCopyRecipe(ResourceLocation id) {
        super(id);
    }

    public boolean matches(CraftingContainer container, Level level) {
        ItemStack linkedTransportMatrix = ItemStack.EMPTY;
        int count = 0;

        for(int i = 0;i < container.getContainerSize();i++) {
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

    public ItemStack assemble(CraftingContainer container) {
        ItemStack linkedTransportMatrix = ItemStack.EMPTY;
        int count = 0;

        for(int i = 0;i < container.getContainerSize();i++) {
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

        return (!linkedTransportMatrix.isEmpty() && count > 0)? ItemStackUtils.copyWithCount(linkedTransportMatrix, count):
                ItemStack.EMPTY;
    }

    public ItemStack getResultItem() {
        return new ItemStack(ModItems.TELEPORTER_MATRIX.get(), 2);
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remainders = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < remainders.size(); ++i) {
            ItemStack itemstack = container.getItem(i);
            if(!itemstack.isEmpty()) {
                if(itemstack.hasCraftingRemainingItem()) {
                    remainders.set(i, itemstack.getCraftingRemainingItem());
                }else if(itemstack.is(ModItems.TELEPORTER_MATRIX.get()) && TeleporterMatrixItem.isLinked(itemstack)) {
                    remainders.set(i, ItemStackUtils.copyWithCount(itemstack, 1));
                }
            }
        }

        return remainders;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModItems.TELEPORTER_MATRIX.get());
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TELEPORTER_MATRIX_SETTINGS_COPY_SERIALIZER.get();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }
}
