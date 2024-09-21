package me.jddev0.ep.world;

import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;

public final class ModStructureSets {
    private ModStructureSets() {}

    public static final RegistryKey<StructureSet> FACTORY_1 = registerKey("factory_1");
    public static final RegistryKey<StructureSet> SMALL_SOLAR_FARM = registerKey("small_solar_farm");

    public static void bootstrap(Registerable<StructureSet> context) {
        RegistryEntryLookup<Structure> structures = context.getRegistryLookup(RegistryKeys.STRUCTURE);

        context.register(FACTORY_1, new StructureSet(
                List.of(
                        StructureSet.createEntry(structures.getOrThrow(ModStructures.FACTORY_1))
                ),
                new RandomSpreadStructurePlacement(50, 25, SpreadType.LINEAR, 2096632789)
        ));

        context.register(SMALL_SOLAR_FARM, new StructureSet(
                List.of(
                        StructureSet.createEntry(structures.getOrThrow(ModStructures.SMALL_SOLAR_FARM))
                ),
                new RandomSpreadStructurePlacement(40, 20, SpreadType.LINEAR, 1516723498)
        ));
    }

    public static RegistryKey<StructureSet> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.STRUCTURE_SET,
                Identifier.of(EnergizedPowerMod.MODID, name));
    }
}
