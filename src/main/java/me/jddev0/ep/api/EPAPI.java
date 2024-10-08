package me.jddev0.ep.api;

import net.minecraft.util.Identifier;

public final class EPAPI {
    private EPAPI() {}

    public static final String MOD_ID = "energizedpower";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
