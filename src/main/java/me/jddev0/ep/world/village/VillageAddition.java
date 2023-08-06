package me.jddev0.ep.world.village;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.config.ModConfigs;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = EnergizedPowerMod.MODID)
public class VillageAddition {
    @SubscribeEvent
    public static void addVillageHouses(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> templatePoolRegistry = event.getServer().registryAccess().registry(Registry.TEMPLATE_POOL_REGISTRY).orElseThrow();

        //Electrician 1
        int weight = ModConfigs.COMMON_ELECTRICIAN_BUILDING_PLACEMENT_WEIGHT.getValue();
        addVillageHouse(templatePoolRegistry, "desert", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "plains", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "savanna", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "snowy", "electrician_1", weight);
        addVillageHouse(templatePoolRegistry, "taiga", "electrician_1", weight);
    }

    private static void addVillageHouse(Registry<StructureTemplatePool> templatePoolRegistry, String villageType,
                                        String buildingName, int weight) {
        StructureTemplatePool pool = templatePoolRegistry.get(new ResourceLocation(String.format("minecraft:village/%s/houses", villageType)));
        if(pool == null)
            return;

        SinglePoolElement element = SinglePoolElement.legacy(String.format("%s:village/%s/houses/%s", EnergizedPowerMod.MODID, villageType, buildingName)).
                apply(StructureTemplatePool.Projection.RIGID);

        for(int i = 0;i < weight;i++)
            pool.templates.add(element);

        List<Pair<StructurePoolElement, Integer>> entries = new ArrayList<>(pool.rawTemplates);
        entries.add(new Pair<>(element, weight));
        pool.rawTemplates = entries;
    }
}
