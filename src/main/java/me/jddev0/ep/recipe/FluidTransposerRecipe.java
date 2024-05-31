package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.util.FluidStackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class FluidTransposerRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final FluidTransposerBlockEntity.Mode mode;
    private final ItemStack output;
    private final Ingredient input;
    private final FluidStack fluid;

    public FluidTransposerRecipe(ResourceLocation id, FluidTransposerBlockEntity.Mode mode, ItemStack output, Ingredient input, FluidStack fluid) {
        this.id = id;
        this.mode = mode;
        this.output = output;
        this.input = input;
        this.fluid = fluid;
    }

    public FluidTransposerBlockEntity.Mode getMode() {
        return mode;
    }

    public ItemStack getOutput() {
        return output;
    }

    public Ingredient getInput() {
        return input;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if(level.isClientSide)
            return false;

        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registries) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(1);
        ingredients.add(0, input);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.FLUID_TRANSPOSER_ITEM.get());
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

    public static final class Type implements RecipeType<FluidTransposerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "fluid_transposer";
    }

    public static final class Serializer implements RecipeSerializer<FluidTransposerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "fluid_transposer");

        @Override
        public FluidTransposerRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            FluidTransposerBlockEntity.Mode mode = FluidTransposerBlockEntity.Mode.valueOf(GsonHelper.getAsString(json, "mode"));
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            FluidStack fluid = FluidStackUtils.fromJson(json.get("fluid"));

            return new FluidTransposerRecipe(recipeID, mode, output, input, fluid);
        }

        @Override
        public FluidTransposerRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            FluidTransposerBlockEntity.Mode mode = buffer.readEnum(FluidTransposerBlockEntity.Mode.class);
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            FluidStack fluid = buffer.readFluidStack();

            return new FluidTransposerRecipe(recipeID, mode, output, input, fluid);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FluidTransposerRecipe recipe) {
            buffer.writeEnum(recipe.mode);
            recipe.input.toNetwork(buffer);
            buffer.writeItemStack(recipe.output, false);
            buffer.writeFluidStack(recipe.fluid);
        }
    }
}
