package me.jddev0.ep.registry.tags;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class CompatibilityItemTags {
    private CompatibilityItemTags() {}

    public static final TagKey<Item> AE2_ITEM_P2P_TUNNEL_ATTUNEMENTS = TagKey.of(Registry.ITEM_KEY,
            Identifier.of("ae2", "p2p_attunements/item_p2p_tunnel"));

    public static final TagKey<Item> AE2_FLUID_P2P_TUNNEL_ATTUNEMENTS = TagKey.of(Registry.ITEM_KEY,
            Identifier.of("ae2", "p2p_attunements/fluid_p2p_tunnel"));

    public static final TagKey<Item> AE2_FE_P2P_TUNNEL_ATTUNEMENTS = TagKey.of(Registry.ITEM_KEY,
            Identifier.of("ae2", "p2p_attunements/fe_p2p_tunnel"));
}
