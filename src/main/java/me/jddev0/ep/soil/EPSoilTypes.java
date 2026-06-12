package me.jddev0.ep.soil;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.registry.EPRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public final class EPSoilTypes {
    private EPSoilTypes() {}

    public static final ResourceKey<SoilType> FARMLAND = registerKey("farmland");
    public static final ResourceKey<SoilType> DIRT = registerKey("dirt");
    public static final ResourceKey<SoilType> COARSE_DIRT = registerKey("coarse_dirt");
    public static final ResourceKey<SoilType> GRASS = registerKey("grass");
    public static final ResourceKey<SoilType> PODZOL = registerKey("podzol");
    public static final ResourceKey<SoilType> MYCELIUM = registerKey("mycelium");
    public static final ResourceKey<SoilType> MUD = registerKey("mud");
    public static final ResourceKey<SoilType> MOSS = registerKey("moss");
    public static final ResourceKey<SoilType> SAND = registerKey("sand");
    public static final ResourceKey<SoilType> GRAVEL = registerKey("gravel");
    public static final ResourceKey<SoilType> STONE = registerKey("stone");
    public static final ResourceKey<SoilType> SOUL_SAND = registerKey("soul_sand");
    public static final ResourceKey<SoilType> END_STONE = registerKey("end_stone");

    public static void bootstrap(BootstrapContext<SoilType> context) {
        context.register(FARMLAND, new SoilType(Component.translatable("block.minecraft.farmland").withColor(0x593d29)));
        context.register(DIRT, new SoilType(Component.translatable("block.minecraft.dirt").withColor(0x362519)));
        context.register(COARSE_DIRT, new SoilType(Component.translatable("block.minecraft.coarse_dirt").withColor(0x593d29)));
        context.register(GRASS, new SoilType(Component.translatable("soil_type.energizedpower.grass").withColor(0x3e5f36)));
        context.register(PODZOL, new SoilType(Component.translatable("block.minecraft.podzol").withColor(0x6a4418)));
        context.register(MYCELIUM, new SoilType(Component.translatable("block.minecraft.mycelium").withColor(0x50474b)));
        context.register(MUD, new SoilType(Component.translatable("block.minecraft.mud").withColor(0x3a383d)));
        context.register(MOSS, new SoilType(Component.translatable("soil_type.energizedpower.moss").withColor(0x495e27)));
        context.register(SAND, new SoilType(Component.translatable("block.minecraft.sand").withColor(0x8c8462)));
        context.register(GRAVEL, new SoilType(Component.translatable("block.minecraft.gravel").withColor(0x817f7f)));
        context.register(STONE, new SoilType(Component.translatable("block.minecraft.stone").withColor(0x7f7f7f)));
        context.register(SOUL_SAND, new SoilType(Component.translatable("block.minecraft.soul_sand").withColor(0x6a5244)));
        context.register(END_STONE, new SoilType(Component.translatable("block.minecraft.end_stone").withColor(0xdee6a4)));
    }

    public static ResourceKey<SoilType> registerKey(String name) {
        return ResourceKey.create(EPRegistries.SOIL_TYPE, EPAPI.id(name));
    }
}
