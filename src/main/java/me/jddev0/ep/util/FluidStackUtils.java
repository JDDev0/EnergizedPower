package me.jddev0.ep.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class FluidStackUtils {
    private FluidStackUtils() {}

    public static FluidStack fromJson(JsonElement json) {
        try {
            return FluidStack.loadFluidStackFromNBT(TagParser.parseTag(json.toString()));
        }catch(CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid FluidStack json representation", e);
        }
    }

    public static JsonElement toJson(FluidStack fluid) {
        JsonObject fluidJson = new JsonObject();

        fluidJson.addProperty("FluidName", ForgeRegistries.FLUIDS.getKey(fluid.getFluid()).toString());

        fluidJson.addProperty("Amount", fluid.getAmount());

        if(fluid.hasTag())
            fluidJson.addProperty("Tag", fluid.getTag().toString());

        return fluidJson;
    }
}
