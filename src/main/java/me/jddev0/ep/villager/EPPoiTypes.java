package me.jddev0.ep.villager;

import com.google.common.collect.ImmutableSet;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;

public class EPPoiTypes {
    private EPPoiTypes() {}

    public static final PoiType ELECTRICIAN = registerPOI("electrician", EPBlocks.BASIC_MACHINE_FRAME);

    public static final ResourceKey<PoiType> ELECTRICIAN_KEY = poiKey("electrician");

    private static PoiType registerPOI(String name, Block block) {
        return PoiHelper.register(EPAPI.id(name), 1, 1,
                ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates()));
    }

    private static ResourceKey<PoiType> poiKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, EPAPI.id(name));
    }

    public static void register() {}
}
