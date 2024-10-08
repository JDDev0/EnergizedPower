package me.jddev0.ep.recipe;

import com.google.gson.*;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class HeatGeneratorRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final Fluid[] input;
    private final long energyProduction;

    public HeatGeneratorRecipe(Identifier id, Fluid[] input, long energyProduction) {
        this.id = id;
        this.input = input;
        this.energyProduction = energyProduction;
    }

    public Fluid[] getInput() {
        return input;
    }

    public long getEnergyProduction() {
        return energyProduction;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory container, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(EPBlocks.HEAT_GENERATOR_ITEM);
    }

    @Override
    public Identifier getId() {
        return id;
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

    public static final class Type implements RecipeType<HeatGeneratorRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "heat_generator";
    }

    public static final class Serializer implements RecipeSerializer<HeatGeneratorRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("heat_generator");

        private void addFluidsFromJsonElement(JsonElement inputJson, List<Fluid> inputFluids) {
            if(!inputJson.isJsonPrimitive() || !inputJson.getAsJsonPrimitive().isString())
                throw new JsonSyntaxException("Input must be a single fluid or a list of at least one fluid");

            Identifier fluidId = Identifier.tryParse(inputJson.getAsJsonPrimitive().getAsString());

            Fluid fluid = Registries.FLUID.get(fluidId);
            if(fluid == null || fluid == Fluids.EMPTY)
                throw new JsonSyntaxException("Unknown fluid '" + fluidId + "'");

            inputFluids.add(fluid);
        }

        @Override
        public HeatGeneratorRecipe read(Identifier recipeID, JsonObject json) {
            List<Fluid> input = new LinkedList<>();

            JsonElement inputJson = json.get("input");
            if(inputJson.isJsonPrimitive()) {
                addFluidsFromJsonElement(inputJson, input);
            }else if(inputJson.isJsonArray()) {
                JsonArray inputJsonArray = inputJson.getAsJsonArray();

                if(inputJsonArray.isEmpty())
                    throw new JsonSyntaxException("Input must contain at least one fluid");

                for(JsonElement inputJsonEle:inputJsonArray)
                    addFluidsFromJsonElement(inputJsonEle, input);
            }else {
                throw new JsonSyntaxException("Input must be a single fluid or a list of at least one fluid");
            }

            long energyProduction = JsonHelper.getLong(json, "energy");

            return new HeatGeneratorRecipe(recipeID, input.toArray(new Fluid[0]), energyProduction);
        }

        @Override
        public HeatGeneratorRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            int fluidCount = buffer.readInt();
            Fluid[] input = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                input[i] = Registries.FLUID.get(buffer.readIdentifier());

            long energyProduction = buffer.readLong();

            return new HeatGeneratorRecipe(recipeID, input, energyProduction);
        }

        @Override
        public void write(PacketByteBuf buffer, HeatGeneratorRecipe recipe) {
            buffer.writeInt(recipe.getInput().length);
            for(Fluid fluid:recipe.input) {
                Identifier fluidId = Registries.FLUID.getId(fluid);
                if(fluidId == null || fluidId.equals(new Identifier("empty")))
                    throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                buffer.writeIdentifier(fluidId);
            }

            buffer.writeLong(recipe.energyProduction);
        }
    }
}
