package me.jddev0.ep.villager;

import com.google.common.collect.ImmutableSet;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class EPVillager {
    private EPVillager() {}

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, EPAPI.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, EPAPI.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> BASIC_MACHINE_FRAME_POI = POI_TYPES.register("basic_machine_frame_poi",
            () -> new PoiType(ImmutableSet.of(EPBlocks.BASIC_MACHINE_FRAME.get().defaultBlockState()), 1, 1));

    public static final ResourceKey<VillagerProfession> ELECTRICIAN_PROFESSION_KEY = professionKey("electrician");

    public static final Supplier<VillagerProfession> ELECTRICIAN_PROFESSION = VILLAGER_PROFESSIONS.register("electrician",
            () -> new VillagerProfession(Component.translatable("entity.minecraft.villager.electrician"), poiType -> poiType.value() == BASIC_MACHINE_FRAME_POI.get(),
                    poiType -> poiType.value() == BASIC_MACHINE_FRAME_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_TOOLSMITH));

    private static ResourceKey<VillagerProfession> professionKey(String name) {
        return ResourceKey.create(Registries.VILLAGER_PROFESSION, EPAPI.id(name));
    }

    public static void register(IEventBus modEventBus) {
        POI_TYPES.register(modEventBus);
        VILLAGER_PROFESSIONS.register(modEventBus);
    }
}
