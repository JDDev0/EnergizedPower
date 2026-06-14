package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FarmlandCraftingRecipe extends CustomRecipe {
    private final Ingredient dirt;
    private final ItemStackTemplate farmland;

    public FarmlandCraftingRecipe(Ingredient dirt, ItemStackTemplate farmland) {
        this.dirt = dirt;
        this.farmland = farmland;
    }

    public Ingredient getDirt() {
        return dirt;
    }

    public ItemStackTemplate getFarmland() {
        return farmland;
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        int dirtCount = 0;
        int hoeCount = 0;

        for(int i = 0;i < container.size();i++) {
            ItemStack itemStack = container.getItem(i);
            if(!itemStack.isEmpty()) {
                if(dirt.test(itemStack)) {
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
    public ItemStack assemble(CraftingInput container) {
        return ItemStackUtils.fromNullableItemStackTemplate(farmland);
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
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }

    private static final MapCodec<FarmlandCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Ingredient.CODEC.fieldOf("dirt").forGetter((recipe) -> {
            return recipe.dirt;
        }), ItemStackTemplate.CODEC.fieldOf("farmland").forGetter((recipe) -> {
            return recipe.farmland;
        })).apply(instance, FarmlandCraftingRecipe::new);
    });
    private static final StreamCodec<RegistryFriendlyByteBuf, FarmlandCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, FarmlandCraftingRecipe::getDirt,
            ItemStackTemplate.STREAM_CODEC, FarmlandCraftingRecipe::getFarmland,
            FarmlandCraftingRecipe::new
    );
    public static final RecipeSerializer<FarmlandCraftingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
