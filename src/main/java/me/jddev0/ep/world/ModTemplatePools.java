package me.jddev0.ep.world;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;

import java.util.List;

public final class ModTemplatePools {
    private ModTemplatePools() {}

    public static final RegistryKey<StructurePool> FACTORY_1_START = registerKey("factory_1/start_pool");
    public static final RegistryKey<StructurePool> SMALL_SOLAR_FARM_START = registerKey("small_solar_farm/start_pool");

    public static void bootstrap(Registerable<StructurePool> context) {
        RegistryEntryLookup<StructurePool> templatePools = context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);

        RegistryEntry.Reference<StructurePool> emptyTemplatePool = templatePools.getOrThrow(StructurePools.EMPTY);

        context.register(FACTORY_1_START, new StructurePool(emptyTemplatePool, List.of(
                Pair.of(StructurePoolElement.ofSingle(EnergizedPowerMod.MODID + ":factory/factory_1"), 1)
        ), StructurePool.Projection.RIGID));

        context.register(SMALL_SOLAR_FARM_START, new StructurePool(emptyTemplatePool, List.of(
                Pair.of(StructurePoolElement.ofSingle(EnergizedPowerMod.MODID + ":misc/small_solar_farm"), 1)
        ), StructurePool.Projection.RIGID));
    }

    public static RegistryKey<StructurePool> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.TEMPLATE_POOL,
                Identifier.of(EnergizedPowerMod.MODID, name));
    }
}
