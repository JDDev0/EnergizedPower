package me.jddev0.ep.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class ChargerRecipe implements Recipe<SimpleInventory> {
    private final ItemStack output;
    private final Ingredient input;
    private final int energyConsumption;

    public ChargerRecipe(ItemStack output, Ingredient input, int energyConsumption) {
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
    public boolean matches(SimpleInventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory container, RegistryWrapper.WrapperLookup registries) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
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
        return new ItemStack(ModBlocks.CHARGER_ITEM);
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

    public static final class Type implements RecipeType<ChargerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "charger";
    }

    public static final class Serializer implements RecipeSerializer<ChargerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "charger");

        private final MapCodec<ChargerRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(CodecFix.ITEM_STACK_CODEC.fieldOf("output").forGetter((recipe) -> {
                return recipe.output;
            }), Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> {
                return recipe.input;
            }), Codecs.POSITIVE_INT.fieldOf("energy").forGetter((recipe) -> {
                return recipe.energyConsumption;
            })).apply(instance, ChargerRecipe::new);
        });

        private final PacketCodec<RegistryByteBuf, ChargerRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<ChargerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ChargerRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static ChargerRecipe read(RegistryByteBuf buffer) {
            Ingredient input = Ingredient.PACKET_CODEC.decode(buffer);
            int energyConsumption = buffer.readInt();
            ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer);

            return new ChargerRecipe(output, input, energyConsumption);
        }

        private static void write(RegistryByteBuf buffer, ChargerRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buffer, recipe.input);
            buffer.writeInt(recipe.energyConsumption);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, recipe.output);
        }
    }
}
