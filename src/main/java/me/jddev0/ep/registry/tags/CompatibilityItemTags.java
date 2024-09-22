package me.jddev0.ep.registry.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class CompatibilityItemTags {
    private CompatibilityItemTags() {}

    public static final TagKey<Item> AE2_ITEM_P2P_TUNNEL_ATTUNEMENTS = TagKey.create(Registry.ITEM_REGISTRY,
            new ResourceLocation("ae2", "p2p_attunements/item_p2p_tunnel"));

    public static final TagKey<Item> AE2_FLUID_P2P_TUNNEL_ATTUNEMENTS = TagKey.create(Registry.ITEM_REGISTRY,
            new ResourceLocation("ae2", "p2p_attunements/fluid_p2p_tunnel"));

    public static final TagKey<Item> AE2_FE_P2P_TUNNEL_ATTUNEMENTS = TagKey.create(Registry.ITEM_REGISTRY,
            new ResourceLocation("ae2", "p2p_attunements/fe_p2p_tunnel"));
}