package me.jddev0.ep.soil;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.registry.EPRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public final class EPSoilTypes {
    private EPSoilTypes() {}

    public static final ResourceKey<SoilType> DIRT = registerKey("dirt");
    public static final ResourceKey<SoilType> COARSE_DIRT = registerKey("coarse_dirt");
    public static final ResourceKey<SoilType> GRASS = registerKey("grass");
    public static final ResourceKey<SoilType> PODZOL = registerKey("podzol");
    public static final ResourceKey<SoilType> MYCELIUM = registerKey("mycelium");
    public static final ResourceKey<SoilType> MUD = registerKey("mud");
    public static final ResourceKey<SoilType> MOSS = registerKey("moss");
    public static final ResourceKey<SoilType> GRAVEL = registerKey("gravel");
    public static final ResourceKey<SoilType> SAND = registerKey("sand");
    public static final ResourceKey<SoilType> STONE = registerKey("stone");
    public static final ResourceKey<SoilType> SOUL_SAND = registerKey("soul_sand");
    public static final ResourceKey<SoilType> END_STONE = registerKey("end_stone");

    public static void bootstrap(BootstrapContext<SoilType> context) {
        context.register(DIRT, new SoilType(Component.translatable("block.minecraft.dirt")));
        context.register(COARSE_DIRT, new SoilType(Component.translatable("block.minecraft.coarse_dirt")));
        context.register(GRASS, new SoilType(Component.translatable("soil_type.energizedpower.grass")));
        context.register(PODZOL, new SoilType(Component.translatable("block.minecraft.podzol")));
        context.register(MYCELIUM, new SoilType(Component.translatable("block.minecraft.mycelium")));
        context.register(MUD, new SoilType(Component.translatable("block.minecraft.mud")));
        context.register(MOSS, new SoilType(Component.translatable("soil_type.energizedpower.moss")));
        context.register(GRAVEL, new SoilType(Component.translatable("block.minecraft.gravel")));
        context.register(SAND, new SoilType(Component.translatable("block.minecraft.sand")));
        context.register(STONE, new SoilType(Component.translatable("block.minecraft.stone")));
        context.register(SOUL_SAND, new SoilType(Component.translatable("block.minecraft.soul_sand")));
        context.register(END_STONE, new SoilType(Component.translatable("block.minecraft.end_stone")));
    }

    public static ResourceKey<SoilType> registerKey(String name) {
        return ResourceKey.create(EPRegistries.SOIL_TYPE, EPAPI.id(name));
    }
}
