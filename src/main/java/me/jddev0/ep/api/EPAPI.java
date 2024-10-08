package me.jddev0.ep.api;

import net.minecraft.resources.ResourceLocation;

public final class EPAPI {
    private EPAPI() {}

    public static final String MOD_ID = "energizedpower";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
