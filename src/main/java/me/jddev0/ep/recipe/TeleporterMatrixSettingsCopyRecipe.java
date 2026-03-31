package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TeleporterMatrixSettingsCopyRecipe extends CustomRecipe {
    @Override
    public boolean matches(CraftingInput container, Level level) {
        ItemStack linkedTransportMatrix = ItemStack.EMPTY;
        int count = 0;

        for(int i = 0;i < container.size();i++) {
            ItemStack itemStack = container.getItem(i);
            if(!itemStack.isEmpty()) {
                if(!itemStack.is(EPItems.TELEPORTER_MATRIX))
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
    public ItemStack assemble(CraftingInput container) {
        ItemStack linkedTransportMatrix = ItemStack.EMPTY;
        int count = 0;

        for(int i = 0;i < container.size();i++) {
            ItemStack itemStack = container.getItem(i);
            if(!itemStack.isEmpty()) {
                if(!itemStack.is(EPItems.TELEPORTER_MATRIX))
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
    public NonNullList<ItemStack> getRemainingItems(CraftingInput container) {
        NonNullList<ItemStack> remainders = NonNullList.withSize(container.size(), ItemStack.EMPTY);

        for(int i = 0; i < remainders.size(); ++i) {
            ItemStack itemstack = container.getItem(i);
            if(!itemstack.isEmpty()) {
                if(itemstack.getCraftingRemainder() != null) {
                    remainders.set(i, itemstack.getCraftingRemainder().create());
                }else if(itemstack.is(EPItems.TELEPORTER_MATRIX) && TeleporterMatrixItem.isLinked(itemstack)) {
                    remainders.set(i, itemstack.copyWithCount(1));
                }
            }
        }

        return remainders;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }

    private static final MapCodec<TeleporterMatrixSettingsCopyRecipe> CODEC = MapCodec.unit(new TeleporterMatrixSettingsCopyRecipe());
    private static final StreamCodec<RegistryFriendlyByteBuf, TeleporterMatrixSettingsCopyRecipe> STREAM_CODEC = StreamCodec.of(
            (buffer, recipe) -> {},
            buffer -> new TeleporterMatrixSettingsCopyRecipe()
    );
    public static final RecipeSerializer<TeleporterMatrixSettingsCopyRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
