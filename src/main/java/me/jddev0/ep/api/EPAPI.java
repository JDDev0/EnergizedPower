package me.jddev0.ep.api;

import net.minecraft.resources.Identifier;

public final class EPAPI {
    private EPAPI() {}

    public static final String MOD_ID = "energizedpower";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
