package me.jddev0.ep.villager;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.Optional;

public class EPTradeSets {
    private EPTradeSets() {}

    public static final ResourceKey<TradeSet> ELECTRICIAN_LEVEL_1 = registerKey("electrician/level_1");
    public static final ResourceKey<TradeSet> ELECTRICIAN_LEVEL_2 = registerKey("electrician/level_2");
    public static final ResourceKey<TradeSet> ELECTRICIAN_LEVEL_3 = registerKey("electrician/level_3");
    public static final ResourceKey<TradeSet> ELECTRICIAN_LEVEL_4 = registerKey("electrician/level_4");
    public static final ResourceKey<TradeSet> ELECTRICIAN_LEVEL_5 = registerKey("electrician/level_5");

    public static void bootstrap(BootstrapContext<TradeSet> context) {
        register(context, ELECTRICIAN_LEVEL_1, EPVillagerTradeTags.ELECTRICIAN_LEVEL_1);
        register(context, ELECTRICIAN_LEVEL_2, EPVillagerTradeTags.ELECTRICIAN_LEVEL_2);
        register(context, ELECTRICIAN_LEVEL_3, EPVillagerTradeTags.ELECTRICIAN_LEVEL_3);
        register(context, ELECTRICIAN_LEVEL_4, EPVillagerTradeTags.ELECTRICIAN_LEVEL_4);
        register(context, ELECTRICIAN_LEVEL_5, EPVillagerTradeTags.ELECTRICIAN_LEVEL_5);
    }

    public static ResourceKey<TradeSet> registerKey(String name) {
        return ResourceKey.create(Registries.TRADE_SET, EPAPI.id(name));
    }

    public static void register(BootstrapContext<TradeSet> context, ResourceKey<TradeSet> key, TagKey<VillagerTrade> tradeTag) {
        register(context, key, tradeTag, ConstantValue.exactly(2.0f));
    }

    public static void register(BootstrapContext<TradeSet> context, ResourceKey<TradeSet> key, TagKey<VillagerTrade> tradeTag,
                                NumberProvider numberProvider) {
        context.register(key, new TradeSet(context.lookup(Registries.VILLAGER_TRADE).getOrThrow(tradeTag), numberProvider, false,
                Optional.of(key.identifier().withPrefix("trade_set/"))));
    }
}
