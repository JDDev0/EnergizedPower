package me.jddev0.ep.event;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.mixin.world.village.StructurePoolElementGetterSetter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class ServerStartingHandler implements ServerLifecycleEvents.ServerStarting {
    @Override
    public void onServerStarting(MinecraftServer server) {
        Registry<StructurePool> templatePoolRegistry = server.getRegistryManager().get(Registry.STRUCTURE_POOL_KEY);

        //Electrician 1
        int weight = ModConfigs.COMMON_ELECTRICIAN_BUILDING_1_PLACEMENT_WEIGHT.getValue();
        addVillageHouse(templatePoolRegistry, "desert", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "plains", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "savanna", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "snowy", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "taiga", "electrician_1", weight);
    }

    private static void addVillageHouse(Registry<StructurePool> templatePoolRegistry, String villageType, String buildingName, int weight) {
        StructurePool pool = templatePoolRegistry.get(new Identifier("minecraft", String.format("village/%s/houses", villageType)));
        if(pool == null)
            return;

        ObjectArrayList<StructurePoolElement> elements = ((StructurePoolElementGetterSetter)pool).getElements();
        List<Pair<StructurePoolElement, Integer>> elementCounts = ((StructurePoolElementGetterSetter)pool).getElementCounts();

        SinglePoolElement element = SinglePoolElement.ofLegacySingle(String.format("%s:village/%s/houses/%s", EnergizedPowerMod.MODID, villageType, buildingName)).
                apply(StructurePool.Projection.RIGID);

        for(int i = 0;i < weight;i++)
            elements.add(element);

        List<Pair<StructurePoolElement, Integer>> entries = new ArrayList<>(elementCounts);
        entries.add(new Pair<>(element, weight));
        ((StructurePoolElementGetterSetter)pool).setElementCounts(entries);
    }
}
