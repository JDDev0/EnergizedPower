package me.jddev0.ep.recipe;

import com.google.gson.*;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class HeatGeneratorRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final Fluid[] input;
    private final int energyProduction;

    public HeatGeneratorRecipe(Identifier id, Fluid[] input, int energyProduction) {
        this.id = id;
        this.input = input;
        this.energyProduction = energyProduction;
    }

    public Fluid[] getInput() {
        return input;
    }

    public int getEnergyProduction() {
        return energyProduction;
    }

    @Override
    public boolean matches(SimpleInventory container, World level) {
        return false;
    }

    @Override
    public ItemStack craft(SimpleInventory container) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.HEAT_GENERATOR_ITEM);
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
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "heat_generator");

        private void addFluidsFromJsonElement(JsonElement inputJson, List<Fluid> inputFluids) {
            if(!inputJson.isJsonPrimitive() || !inputJson.getAsJsonPrimitive().isString())
                throw new JsonSyntaxException("Input must be a single fluid or a list of at least one fluid");

            Identifier fluidId = Identifier.tryParse(inputJson.getAsJsonPrimitive().getAsString());

            Fluid fluid = Registry.FLUID.get(fluidId);
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

            int energyProduction = JsonHelper.getInt(json, "energy");

            return new HeatGeneratorRecipe(recipeID, input.toArray(new Fluid[0]), energyProduction);
        }

        @Override
        public HeatGeneratorRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            int fluidCount = buffer.readInt();
            Fluid[] input = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                input[i] = Registry.FLUID.get(buffer.readIdentifier());

            int energyProduction = buffer.readInt();

            return new HeatGeneratorRecipe(recipeID, input, energyProduction);
        }

        @Override
        public void write(PacketByteBuf buffer, HeatGeneratorRecipe recipe) {
            buffer.writeInt(recipe.getInput().length);
            for(Fluid fluid:recipe.input) {
                Identifier fluidId = Registry.FLUID.getId(fluid);
                if(fluidId == null || fluidId.equals(new Identifier("empty")))
                    throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                buffer.writeIdentifier(fluidId);
            }

            buffer.writeInt(recipe.energyProduction);
        }
    }
}
