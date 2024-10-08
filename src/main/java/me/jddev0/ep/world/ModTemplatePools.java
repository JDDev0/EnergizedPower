package me.jddev0.ep.world;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.List;

public final class ModTemplatePools {
    private ModTemplatePools() {}

    public static final ResourceKey<StructureTemplatePool> FACTORY_1_START = registerKey("factory_1/start_pool");
    public static final ResourceKey<StructureTemplatePool> SMALL_SOLAR_FARM_START = registerKey("small_solar_farm/start_pool");

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);

        Holder<StructureTemplatePool> emptyTemplatePool = templatePools.getOrThrow(Pools.EMPTY);

        context.register(FACTORY_1_START, new StructureTemplatePool(emptyTemplatePool, List.of(
                Pair.of(StructurePoolElement.single(EPAPI.MOD_ID + ":factory/factory_1"), 1)
        ), StructureTemplatePool.Projection.RIGID));

        context.register(SMALL_SOLAR_FARM_START, new StructureTemplatePool(emptyTemplatePool, List.of(
                Pair.of(StructurePoolElement.single(EPAPI.MOD_ID + ":misc/small_solar_farm"), 1)
        ), StructureTemplatePool.Projection.RIGID));
    }

    public static ResourceKey<StructureTemplatePool> registerKey(String name) {
        return ResourceKey.create(Registries.TEMPLATE_POOL,
                EPAPI.id(name));
    }
}
