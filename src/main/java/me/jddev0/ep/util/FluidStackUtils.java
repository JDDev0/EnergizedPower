package me.jddev0.ep.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.jddev0.ep.fluid.FluidStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public final class FluidStackUtils {
    private FluidStackUtils() {}

    public static FluidStack fromJson(JsonObject json) {
        try {
            Fluid fluid = Registries.FLUID.get(new Identifier(JsonHelper.getString(json, "FluidName")));

            //Save milli buckets amount in "Amount" for compatibility with Forge
            //Save leftover droplets amount in "LeftoverDropletsAmount" for preventing rounding errors
            long milliBucketsAmount = JsonHelper.getLong(json, "Amount");
            long dropletsLeftOverAmount = json.has("LeftoverDropletsAmount")?
                    JsonHelper.getLong(json, "LeftoverDropletsAmount"):0;
            long dropletsAmount = FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount) + dropletsLeftOverAmount;

            NbtCompound nbt;
            if(json.has("Tag")) {
                JsonElement tagJson = json.get("Tag");
                if(tagJson.isJsonObject())
                    nbt = StringNbtReader.parse(tagJson.toString());
                else if(tagJson.isJsonPrimitive() && tagJson.getAsJsonPrimitive().isString())
                    nbt = StringNbtReader.parse(tagJson.getAsString());
                else
                    throw new JsonSyntaxException("Invalid FluidStack nbt data (Expected json object or string)");
            }else {
                nbt = null;
            }

            return new FluidStack(fluid, nbt, dropletsAmount);
        }catch(CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid FluidStack json representation", e);
        }
    }

    public static JsonElement toJson(FluidStack fluid) {
        JsonObject fluidJson = new JsonObject();

        fluidJson.addProperty("FluidName", Registries.FLUID.getId(fluid.getFluid()).toString());

        long dropletsAmount = fluid.getDropletsAmount();

        //Save milli buckets amount in "Amount" for compatibility with Forge
        //Save leftover droplets amount in "LeftoverDropletsAmount" for preventing rounding errors
        long milliBucketsAmount = FluidUtils.convertDropletsToMilliBuckets(dropletsAmount);
        long dropletsLeftOverAmount = dropletsAmount - FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount);
        fluidJson.addProperty("Amount", milliBucketsAmount);
        if(dropletsLeftOverAmount > 0)
            fluidJson.addProperty("LeftoverDropletsAmount", dropletsLeftOverAmount);

        if(fluid.getFluidVariant().hasNbt())
            fluidJson.addProperty("Tag", fluid.getFluidVariant().getNbt().toString());

        return fluidJson;
    }
}