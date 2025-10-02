package me.jddev0.ep.world;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.registry.tags.EnergizedPowerBiomeTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.List;
import java.util.Optional;

public final class ModStructures {
    private ModStructures() {}

    public static final ResourceKey<Structure> FACTORY_1 = registerKey("factory_1");
    public static final ResourceKey<Structure> SMALL_SOLAR_FARM = registerKey("small_solar_farm");

    public static void bootstrap(BootstrapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);

        context.register(FACTORY_1, new JigsawStructure(
                new Structure.StructureSettings.Builder(biomes.getOrThrow(EnergizedPowerBiomeTags.HAS_STRUCTURE_FACTORY_1)).
                        terrainAdapation(TerrainAdjustment.BEARD_BOX).
                        generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES).
                        build(),
                templatePools.getOrThrow(ModTemplatePools.FACTORY_1_START), Optional.empty(), 1,
                ConstantHeight.of(VerticalAnchor.absolute(-1)), false,
                Optional.of(Heightmap.Types.WORLD_SURFACE_WG), new JigsawStructure.MaxDistance(64, 64),
                List.of(), JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS)
        );

        context.register(SMALL_SOLAR_FARM, new JigsawStructure(
                new Structure.StructureSettings.Builder(biomes.getOrThrow(EnergizedPowerBiomeTags.HAS_STRUCTURE_SMALL_SOLAR_FARM)).
                        terrainAdapation(TerrainAdjustment.BEARD_BOX).
                        generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES).
                        build(),
                templatePools.getOrThrow(ModTemplatePools.SMALL_SOLAR_FARM_START), Optional.empty(), 1,
                ConstantHeight.of(VerticalAnchor.absolute(0)), false,
                Optional.of(Heightmap.Types.WORLD_SURFACE_WG), new JigsawStructure.MaxDistance(16, 16),
                List.of(), JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS)
        );
    }

    public static ResourceKey<Structure> registerKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE,
                EPAPI.id(name));
    }
}
