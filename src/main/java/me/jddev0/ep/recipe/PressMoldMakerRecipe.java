package me.jddev0.ep.recipe;

import com.google.gson.JsonObject;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class PressMoldMakerRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final int clayCount;

    public PressMoldMakerRecipe(Identifier id, ItemStack output, int clayCount) {
        this.id = id;
        this.output = output;
        this.clayCount = clayCount;
    }

    public ItemStack getOutputItem() {
        return output;
    }

    public int getClayCount() {
        return clayCount;
    }

    @Override
    public boolean matches(Inventory container, World level) {
        if(level.isClient())
            return false;

        ItemStack item = container.getStack(0);
        return item.isOf(Items.CLAY_BALL) && item.getCount() >= clayCount;
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
        return new ItemStack(ModBlocks.PRESS_MOLD_MAKER_ITEM);
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

    public static final class Type implements RecipeType<PressMoldMakerRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
        public static final String ID = "press_mold_maker";
    }

    public static final class Serializer implements RecipeSerializer<PressMoldMakerRecipe> {
        private Serializer() {}

        public static final Serializer INSTANCE = new Serializer();
        public static final Identifier ID = EPAPI.id("press_mold_maker");

        @Override
        public PressMoldMakerRecipe read(Identifier recipeID, JsonObject json) {
            int clayCount = JsonHelper.getInt(json, "clayCount");
            ItemStack output = ItemStackUtils.fromJson(JsonHelper.getObject(json, "output"));

            return new PressMoldMakerRecipe(recipeID, output, clayCount);
        }

        @Override
        public PressMoldMakerRecipe read(Identifier recipeID, PacketByteBuf buffer) {
            int clayCount = buffer.readInt();
            ItemStack output = buffer.readItemStack();

            return new PressMoldMakerRecipe(recipeID, output, clayCount);
        }

        @Override
        public void write(PacketByteBuf buffer, PressMoldMakerRecipe recipe) {
            buffer.writeInt(recipe.clayCount);
            buffer.writeItemStack(recipe.output);
        }
    }
}
