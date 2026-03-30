package me.jddev0.ep.villager;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

public class EPVillagerProfessions {
    private EPVillagerProfessions() {}
    
    public static final ResourceKey<VillagerProfession> ELECTRICIAN_PROFESSION_KEY = registerKey("electrician");

    public static final VillagerProfession ELECTRICIAN_PROFESSION = register(ELECTRICIAN_PROFESSION_KEY,
            new VillagerProfession(Component.translatable("entity.minecraft.villager.electrician"),
            poiType -> poiType.is(EPPoiTypes.ELECTRICIAN_KEY), poiType -> poiType.is(EPPoiTypes.ELECTRICIAN_KEY),
            ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_TOOLSMITH, Int2ObjectMap.ofEntries(
                    Int2ObjectMap.entry(1, EPTradeSets.ELECTRICIAN_LEVEL_1),
                    Int2ObjectMap.entry(2, EPTradeSets.ELECTRICIAN_LEVEL_2),
                    Int2ObjectMap.entry(3, EPTradeSets.ELECTRICIAN_LEVEL_3),
                    Int2ObjectMap.entry(4, EPTradeSets.ELECTRICIAN_LEVEL_4),
                    Int2ObjectMap.entry(5, EPTradeSets.ELECTRICIAN_LEVEL_5))));

    public static ResourceKey<VillagerProfession> registerKey(String name) {
        return ResourceKey.create(Registries.VILLAGER_PROFESSION, EPAPI.id(name));
    }

    private static VillagerProfession register(ResourceKey<VillagerProfession> professionKey, VillagerProfession profession) {
        return Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, professionKey, profession);
    }

    public static void register() {}
}
