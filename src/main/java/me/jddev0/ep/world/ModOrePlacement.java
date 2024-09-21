package me.jddev0.ep.world;


import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.List;

public final class ModOrePlacement {
    private ModOrePlacement() {}

    public static List<PlacementModifier> orePlacement(int count, PlacementModifier heightRange) {
        return List.of(CountPlacementModifier.of(count), SquarePlacementModifier.of(), heightRange, BiomePlacementModifier.of());
    }
}
