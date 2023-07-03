package me.jddev0.ep.recipe;

import com.google.gson.*;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedList;
import java.util.List;

public class HeatGeneratorRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final Fluid[] input;
    private final int energyProduction;

    public HeatGeneratorRecipe(ResourceLocation id, Fluid[] input, int energyProduction) {
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
    public boolean matches(SimpleContainer container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.HEAT_GENERATOR_ITEM.get());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean isSpecial() {
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
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "heat_generator");

        private void addFluidsFromJsonElement(JsonElement inputJson, List<Fluid> inputFluids) {
            if(!inputJson.isJsonPrimitive() || !inputJson.getAsJsonPrimitive().isString())
                throw new JsonSyntaxException("Input must be a single fluid or a list of at least one fluid");

            ResourceLocation fluidId = ResourceLocation.tryParse(inputJson.getAsJsonPrimitive().getAsString());

            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
            if(fluid == null)
                throw new JsonSyntaxException("Unknown fluid '" + fluidId + "'");

            inputFluids.add(fluid);
        }

        @Override
        public HeatGeneratorRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
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

            int energyProduction = GsonHelper.getAsInt(json, "energy");

            return new HeatGeneratorRecipe(recipeID, input.toArray(new Fluid[0]), energyProduction);
        }

        @Override
        public HeatGeneratorRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            int fluidCount = buffer.readInt();
            Fluid[] input = new Fluid[fluidCount];
            for(int i = 0;i < fluidCount;i++)
                input[i] = ForgeRegistries.FLUIDS.getValue(buffer.readResourceLocation());

            int energyProduction = buffer.readInt();

            return new HeatGeneratorRecipe(recipeID, input, energyProduction);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, HeatGeneratorRecipe recipe) {
            buffer.writeInt(recipe.getInput().length);
            for(Fluid fluid:recipe.input) {
                ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(fluid);
                if(fluidId == null)
                    throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                buffer.writeResourceLocation(fluidId);
            }

            buffer.writeInt(recipe.energyProduction);
        }
    }
}
