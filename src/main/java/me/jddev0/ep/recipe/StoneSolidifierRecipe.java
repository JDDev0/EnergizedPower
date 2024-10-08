package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class StoneSolidifierRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final long waterAmount;
    private final long lavaAmount;

    public StoneSolidifierRecipe(Identifier id, ItemStack output, long waterAmount, long lavaAmount) {
        this.id = id;
        this.output = output;
        this.waterAmount = waterAmount;
        this.lavaAmount = lavaAmount;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public long getWaterAmount() {
        return waterAmount;
    }

    public long getLavaAmount() {
        return lavaAmount;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        return false;
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
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.STONE_SOLIDIFIER);
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

    public static final class Type implements RecipeType<StoneSolidifierRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "stone_solidifier";
    }

    public static final class Serializer implements RecipeSerializer<StoneSolidifierRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("stone_solidifier");

        @Override
        public StoneSolidifierRecipe read(Identifier recipeID, JsonObject json) {
            long waterAmount = json.get("waterAmount").getAsLong();
            long lavaAmount = json.get("lavaAmount").getAsLong();
            ItemStack output = ItemStackUtils.fromJson(JsonHelper.getObject(json, "output"));

            return new StoneSolidifierRecipe(recipeID, output, waterAmount, lavaAmount);
        }

        @Override
        public StoneSolidifierRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            long waterAmount = buffer.readLong();
            long lavaAmount = buffer.readLong();
            ItemStack output = buffer.readItemStack();

            return new StoneSolidifierRecipe(recipeID, output, waterAmount, lavaAmount);
        }

        @Override
        public void write(PacketByteBuf buffer, StoneSolidifierRecipe recipe) {
            buffer.writeLong(recipe.waterAmount);
            buffer.writeLong(recipe.lavaAmount);
            buffer.writeItemStack(recipe.output);
        }
    }
}
