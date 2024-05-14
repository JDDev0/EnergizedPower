package me.jddev0.ep.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class EnergizerRecipe implements Recipe<Inventory> {
    private final ItemStack output;
    private final Ingredient input;
    private final int energyConsumption;

    public EnergizerRecipe(ItemStack output, Ingredient input, int energyConsumption) {
        this.output = output;
        this.input = input;
        this.energyConsumption = energyConsumption;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public Ingredient getInputItem() {
        return input;
    }

    public int getEnergyConsumption() {
        return energyConsumption;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory container, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManage) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.ENERGIZER_ITEM);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static final class Type implements RecipeType<EnergizerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "energizer";
    }

    public static final class Serializer implements RecipeSerializer<EnergizerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "energizer");

        private final Codec<EnergizerRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codecs.POSITIVE_INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyConsumption;
            })).apply(instance, EnergizerRecipe::new);
        });

        @Override
        public Codec<EnergizerRecipe> codec() {
            return CODEC;
        }

        @Override
        public EnergizerRecipe read(PacketByteBuf buffer) {
            Ingredient input = Ingredient.fromPacket(buffer);
            int energyConsumption = buffer.readInt();
            ItemStack output = buffer.readItemStack();

            return new EnergizerRecipe(output, input, energyConsumption);
        }

        @Override
        public void write(PacketByteBuf buffer, EnergizerRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeInt(recipe.energyConsumption);
            buffer.writeItemStack(recipe.output);
        }
    }
}
