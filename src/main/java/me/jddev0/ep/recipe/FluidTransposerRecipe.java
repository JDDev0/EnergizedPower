package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.util.FluidStackUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class FluidTransposerRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final FluidTransposerBlockEntity.Mode mode;
    private final ItemStack output;
    private final Ingredient input;
    private final FluidStack fluid;

    public FluidTransposerRecipe(Identifier id, FluidTransposerBlockEntity.Mode mode, ItemStack output, Ingredient input, FluidStack fluid) {
        this.id = id;
        this.mode = mode;
        this.output = output;
        this.input = input;
        this.fluid = fluid;
    }

    public FluidTransposerBlockEntity.Mode getMode() {
        return mode;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public Ingredient getInput() {
        return input;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        return input.test(container.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory container) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
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
        return new ItemStack(ModBlocks.FLUID_TRANSPOSER_ITEM);
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

    public static final class Type implements RecipeType<FluidTransposerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "fluid_transposer";
    }

    public static final class Serializer implements RecipeSerializer<FluidTransposerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = new Identifier(EnergizedPowerMod.MODID, "fluid_transposer");

        @Override
        public FluidTransposerRecipe read(Identifier recipeID, JsonObject json) {
            FluidTransposerBlockEntity.Mode mode = FluidTransposerBlockEntity.Mode.valueOf(JsonHelper.getString(json, "mode"));
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            ItemStack output = ItemStackUtils.fromJson(JsonHelper.getObject(json, "output"));
            FluidStack fluid = FluidStackUtils.fromJson(JsonHelper.getObject(json, "fluid"));

            return new FluidTransposerRecipe(recipeID, mode, output, input, fluid);
        }

        @Override
        public FluidTransposerRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            FluidTransposerBlockEntity.Mode mode = buffer.readEnumConstant(FluidTransposerBlockEntity.Mode.class);
            Ingredient input = Ingredient.fromPacket(buffer);
            ItemStack output = buffer.readItemStack();
            FluidStack fluid = FluidStack.fromPacket(buffer);

            return new FluidTransposerRecipe(recipeID, mode, output, input, fluid);
        }

        @Override
        public void write(PacketByteBuf buffer, FluidTransposerRecipe recipe) {
            buffer.writeEnumConstant(recipe.mode);
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.output);
            recipe.fluid.toPacket(buffer);
        }
    }
}
