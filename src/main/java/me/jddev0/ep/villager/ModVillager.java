package me.jddev0.ep.villager;

import com.google.common.collect.ImmutableSet;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModVillager {
    private ModVillager() {}

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, EnergizedPowerMod.MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, EnergizedPowerMod.MODID);

    public static final RegistryObject<PoiType> BASIC_MACHINE_FRAME_POI = POI_TYPES.register("basic_machine_frame_poi",
            () -> new PoiType("basic_machine_frame_poi", ImmutableSet.of(ModBlocks.BASIC_MACHINE_FRAME.get().defaultBlockState()), 1, 1));

    public static final RegistryObject<VillagerProfession> ELECTRICIAN_PROFESSION = VILLAGER_PROFESSIONS.register("electrician",
            () -> new VillagerProfession("electrician", BASIC_MACHINE_FRAME_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_TOOLSMITH));

    public static void register(IEventBus modEventBus) {
        POI_TYPES.register(modEventBus);
        VILLAGER_PROFESSIONS.register(modEventBus);
    }
}
