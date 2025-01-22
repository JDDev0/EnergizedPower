package me.jddev0.ep.world;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import java.util.List;

public final class ModStructureSets {
    private ModStructureSets() {}

    public static final ResourceKey<StructureSet> FACTORY_1 = registerKey("factory_1");
    public static final ResourceKey<StructureSet> SMALL_SOLAR_FARM = registerKey("small_solar_farm");

    public static void bootstrap(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

        context.register(FACTORY_1, new StructureSet(
                List.of(
                        StructureSet.entry(structures.getOrThrow(ModStructures.FACTORY_1))
                ),
                new RandomSpreadStructurePlacement(50, 25, RandomSpreadType.LINEAR, 2096632789)
        ));

        context.register(SMALL_SOLAR_FARM, new StructureSet(
                List.of(
                        StructureSet.entry(structures.getOrThrow(ModStructures.SMALL_SOLAR_FARM))
                ),
                new RandomSpreadStructurePlacement(40, 20, RandomSpreadType.LINEAR, 1516723498)
        ));
    }

    public static ResourceKey<StructureSet> registerKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET,
                EPAPI.id(name));
    }
}
