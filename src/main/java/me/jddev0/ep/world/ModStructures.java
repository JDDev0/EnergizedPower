package me.jddev0.ep.world;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.registry.tags.EnergizedPowerBiomeTags;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;
import java.util.Optional;

public final class ModStructures {
    private ModStructures() {}

    public static final RegistryKey<Structure> FACTORY_1 = registerKey("factory_1");
    public static final RegistryKey<Structure> SMALL_SOLAR_FARM = registerKey("small_solar_farm");

    public static void bootstrap(Registerable<Structure> context) {
        RegistryEntryLookup<Biome> biomes = context.getRegistryLookup(RegistryKeys.BIOME);
        RegistryEntryLookup<StructurePool> templatePools = context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);

        context.register(FACTORY_1, new JigsawStructure(
                new Structure.Config.Builder(biomes.getOrThrow(EnergizedPowerBiomeTags.HAS_STRUCTURE_FACTORY_1)).
                        terrainAdaptation(StructureTerrainAdaptation.BEARD_BOX).
                        step(GenerationStep.Feature.SURFACE_STRUCTURES).
                        build(),
                templatePools.getOrThrow(ModTemplatePools.FACTORY_1_START), Optional.empty(), 1,
                ConstantHeightProvider.create(YOffset.fixed(-1)), false,
                Optional.of(Heightmap.Type.WORLD_SURFACE_WG), new JigsawStructure.MaxDistanceFromCenter(64, 64),
                List.of(), JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS)
        );

        context.register(SMALL_SOLAR_FARM, new JigsawStructure(
                new Structure.Config.Builder(biomes.getOrThrow(EnergizedPowerBiomeTags.HAS_STRUCTURE_SMALL_SOLAR_FARM)).
                        terrainAdaptation(StructureTerrainAdaptation.BEARD_BOX).
                        step(GenerationStep.Feature.SURFACE_STRUCTURES).
                        build(),
                templatePools.getOrThrow(ModTemplatePools.SMALL_SOLAR_FARM_START), Optional.empty(), 1,
                ConstantHeightProvider.create(YOffset.fixed(0)), false,
                Optional.of(Heightmap.Type.WORLD_SURFACE_WG), new JigsawStructure.MaxDistanceFromCenter(16, 16),
                List.of(), JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS)
        );
    }

    public static RegistryKey<Structure> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.STRUCTURE,
                EPAPI.id(name));
    }
}
