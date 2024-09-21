package me.jddev0.ep.world.gen;

public final class ModWorldGeneration {
    private ModWorldGeneration() {}

    public static void register() {
        ModOreGeneration.register();
    }
}
