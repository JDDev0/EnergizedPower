package me.jddev0.ep.recipe;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.HolderLookup;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FarmlandCraftingRecipe extends CustomRecipe {
    private final Ingredient dirt;
    private final ItemStack farmland;

    public FarmlandCraftingRecipe(Ingredient dirt, ItemStack farmland) {
        super(CraftingBookCategory.MISC);

        this.dirt = dirt;
        this.farmland = farmland;
    }

    public Ingredient getDirt() {
        return dirt;
    }

    public ItemStack getFarmland() {
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
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
        return farmland.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return farmland.copy();
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

    public static final class Serializer implements RecipeSerializer<FarmlandCraftingRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = EPAPI.id("farmland_crafting");

        private static final MapCodec<FarmlandCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Ingredient.CODEC.fieldOf("dirt").forGetter((recipe) -> {
                return recipe.dirt;
            }), ItemStack.CODEC.fieldOf("farmland").forGetter((recipe) -> {
                return recipe.farmland;
            })).apply(instance, FarmlandCraftingRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, FarmlandCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, FarmlandCraftingRecipe::getDirt,
                ItemStack.STREAM_CODEC, FarmlandCraftingRecipe::getFarmland,
                FarmlandCraftingRecipe::new
        );

        @Override
        public MapCodec<FarmlandCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FarmlandCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
