package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
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

public class StoneSolidifierRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final int waterAmount;
    private final int lavaAmount;

    public StoneSolidifierRecipe(ResourceLocation id, ItemStack output, int waterAmount, int lavaAmount) {
        this.id = id;
        this.output = output;
        this.waterAmount = waterAmount;
        this.lavaAmount = lavaAmount;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public int getLavaAmount() {
        return lavaAmount;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.STONE_SOLIDIFIER.get());
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

    public static final class Type implements RecipeType<StoneSolidifierRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "stone_solidifier";
    }

    public static final class Serializer implements RecipeSerializer<StoneSolidifierRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(EnergizedPowerMod.MODID, "stone_solidifier");

        @Override
        public StoneSolidifierRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            int waterAmount = json.get("waterAmount").getAsInt();
            int lavaAmount = json.get("lavaAmount").getAsInt();
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            return new StoneSolidifierRecipe(recipeID, output, waterAmount, lavaAmount);
        }

        @Override
        public StoneSolidifierRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            int waterAmount = buffer.readInt();
            int lavaAmount = buffer.readInt();
            ItemStack output = buffer.readItem();

            return new StoneSolidifierRecipe(recipeID, output, waterAmount, lavaAmount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, StoneSolidifierRecipe recipe) {
            buffer.writeInt(recipe.waterAmount);
            buffer.writeInt(recipe.lavaAmount);
            buffer.writeItemStack(recipe.output, false);
        }
    }
}
