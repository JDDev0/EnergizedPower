package me.jddev0.ep.world.village;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.config.ModConfigs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = EPAPI.MOD_ID)
public class VillageAddition {
    @SubscribeEvent
    public static void addVillageHouses(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> templatePoolRegistry = event.getServer().registryAccess().lookup(Registries.TEMPLATE_POOL).orElseThrow();

        //Electrician 1
        int weight = ModConfigs.COMMON_ELECTRICIAN_BUILDING_1_PLACEMENT_WEIGHT.getValue();
        addVillageHouse(templatePoolRegistry, "desert", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "plains", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "savanna", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "snowy", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "taiga", "electrician_1", weight);
    }

    private static void addVillageHouse(Registry<StructureTemplatePool> templatePoolRegistry, String villageType,
                                        String buildingName, int weight) {
        StructureTemplatePool pool = templatePoolRegistry.getValue(Identifier.parse(String.format("minecraft:village/%s/houses", villageType)));
        if(pool == null)
            return;

        SinglePoolElement element = SinglePoolElement.legacy(String.format("%s:village/%s/houses/%s", EPAPI.MOD_ID, villageType, buildingName)).
                apply(StructureTemplatePool.Projection.RIGID);

        for(int i = 0;i < weight;i++)
            pool.templates.add(element);

        List<Pair<StructurePoolElement, Integer>> entries = new ArrayList<>(pool.rawTemplates);
        entries.add(new Pair<>(element, weight));
        pool.rawTemplates = entries;
    }
}
