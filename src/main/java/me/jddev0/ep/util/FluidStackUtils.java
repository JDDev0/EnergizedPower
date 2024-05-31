package me.jddev0.ep.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraftforge.fluids.FluidStack;

public final class FluidStackUtils {
    private FluidStackUtils() {}

    public static FluidStack fromJson(JsonElement json) {
        try {
            return FluidStack.loadFluidStackFromNBT(TagParser.parseTag(json.toString()));
        } catch(CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid FluidStack json representation", e);
        }
    }
}
