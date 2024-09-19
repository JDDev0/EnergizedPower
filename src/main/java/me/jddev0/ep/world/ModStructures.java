package me.jddev0.ep.world;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.registry.tags.EnergizedPowerBiomeTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class ModStructures {
    private ModStructures() {}

    public static final ResourceKey<Structure> FACTORY_1 = registerKey("factory_1");
    public static final ResourceKey<Structure> SMALL_SOLAR_FARM = registerKey("small_solar_farm");

    public static void bootstrap(BootstapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);

        context.register(FACTORY_1, new JigsawStructure(
                new Structure.StructureSettings(
                        biomes.getOrThrow(EnergizedPowerBiomeTags.HAS_STRUCTURE_FACTORY_1), new HashMap<>(),
                        GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_BOX
                ),
                templatePools.getOrThrow(ModTemplatePools.FACTORY_1_START), Optional.empty(), 1,
                ConstantHeight.of(VerticalAnchor.absolute(-1)), false,
                Optional.of(Heightmap.Types.WORLD_SURFACE_WG), 64)
        );

        context.register(SMALL_SOLAR_FARM, new JigsawStructure(
                new Structure.StructureSettings(
                        biomes.getOrThrow(EnergizedPowerBiomeTags.HAS_STRUCTURE_SMALL_SOLAR_FARM), new HashMap<>(),
                        GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_BOX
                ),
                templatePools.getOrThrow(ModTemplatePools.SMALL_SOLAR_FARM_START), Optional.empty(), 1,
                ConstantHeight.of(VerticalAnchor.absolute(0)), false,
                Optional.of(Heightmap.Types.WORLD_SURFACE_WG), 16)
        );
    }

    public static ResourceKey<Structure> registerKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE,
                new ResourceLocation(EnergizedPowerMod.MODID, name));
    }
}
