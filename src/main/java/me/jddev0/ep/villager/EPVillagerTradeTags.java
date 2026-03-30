package me.jddev0.ep.villager;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.trading.VillagerTrade;

public class EPVillagerTradeTags {
    private EPVillagerTradeTags() {}

    public static final TagKey<VillagerTrade> ELECTRICIAN_LEVEL_1 = TagKey.create(Registries.VILLAGER_TRADE, EPAPI.id("electrician/level_1"));
    public static final TagKey<VillagerTrade> ELECTRICIAN_LEVEL_2 = TagKey.create(Registries.VILLAGER_TRADE, EPAPI.id("electrician/level_2"));
    public static final TagKey<VillagerTrade> ELECTRICIAN_LEVEL_3 = TagKey.create(Registries.VILLAGER_TRADE, EPAPI.id("electrician/level_3"));
    public static final TagKey<VillagerTrade> ELECTRICIAN_LEVEL_4 = TagKey.create(Registries.VILLAGER_TRADE, EPAPI.id("electrician/level_4"));
    public static final TagKey<VillagerTrade> ELECTRICIAN_LEVEL_5 = TagKey.create(Registries.VILLAGER_TRADE, EPAPI.id("electrician/level_5"));
}
