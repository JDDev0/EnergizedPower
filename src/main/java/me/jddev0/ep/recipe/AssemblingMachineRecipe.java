package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.ArrayCodec;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.Arrays;
import java.util.List;

public class AssemblingMachineRecipe implements EnergizedPowerBaseRecipe<RecipeInput> {
    private final ItemStackTemplate output;
    private final IngredientWithCount[] inputs;

    public AssemblingMachineRecipe(ItemStackTemplate output, IngredientWithCount[] inputs) {
        this.output = output;
        this.inputs = inputs;
    }

    public ItemStackTemplate getOutput() {
        return output;
    }

    public IngredientWithCount[] getInputs() {
        return inputs;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        if(level.isClientSide())
            return false;

        boolean[] usedIndices = new boolean[4];
        for(int i = 0;i < 4;i++)
            usedIndices[i] = container.getItem(i).isEmpty();

        int len = Math.min(inputs.length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 4;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = container.getItem(j);

                if((indexMinCount == -1 || item.getCount() < minCount) && input.input().test(item) &&
                        item.getCount() >= input.count()) {
                    indexMinCount = j;
                    minCount = item.getCount();
                }
            }

            if(indexMinCount == -1)
                return false; //Ingredient did not match any item

            usedIndices[indexMinCount] = true;
        }

        for(boolean usedIndex:usedIndices)
            if(!usedIndex) //Unused items present
                return false;

        return true;
    }


    @Override
    public ItemStack assemble(RecipeInput container) {
        return ItemStackUtils.fromNullableItemStackTemplate(this.output);
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EPRecipes.ASSEMBLING_MACHINE_CATEGORY;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return Type.INSTANCE;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Arrays.stream(inputs).map(IngredientWithCount::input).toList();
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return Arrays.stream(inputs).map(IngredientWithCount::input).anyMatch(ingredient -> ingredient.test(itemStack));
    }

    @Override
    public boolean isResult(ItemStack itemStack) {
        ItemStack output = ItemStackUtils.fromNullableItemStackTemplate(this.output);

        return ItemStack.isSameItemSameComponents(output, itemStack);
    }

    public static final class Type implements RecipeType<AssemblingMachineRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "assembling_machine";
    }

    public static final class Serializer {
        private Serializer() {}

        private static final MapCodec<AssemblingMachineRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ItemStackTemplate.CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.output;
            }), new ArrayCodec<>(IngredientWithCount.CODEC, IngredientWithCount[]::new).fieldOf("ingredients").forGetter((recipe) -> {
                return recipe.inputs;
            })).apply(instance, AssemblingMachineRecipe::new);
        });

        private static final StreamCodec<RegistryFriendlyByteBuf, AssemblingMachineRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read);

        public static final RecipeSerializer<AssemblingMachineRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);
        public static final Identifier ID = EPAPI.id("assembling_machine");

        private static AssemblingMachineRecipe read(RegistryFriendlyByteBuf buffer) {
            int len = buffer.readInt();
            IngredientWithCount[] inputs = new IngredientWithCount[len];
            for(int i = 0;i < len;i++)
                inputs[i] = IngredientWithCount.STREAM_CODEC.decode(buffer);

            ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);

            return new AssemblingMachineRecipe(output, inputs);
        }

        private static void write(RegistryFriendlyByteBuf buffer, AssemblingMachineRecipe recipe) {
            buffer.writeInt(recipe.inputs.length);
            for(int i = 0; i < recipe.inputs.length; i++)
                IngredientWithCount.STREAM_CODEC.encode(buffer, recipe.inputs[i]);

            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
