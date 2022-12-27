package me.jddev0.ep.block.entity;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.SolarPanelBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EnergizedPowerMod.MODID);

    public static final RegistryObject<BlockEntityType<AutoCrafterBlockEntity>> AUTO_CRAFTER_ENTITY =
            BLOCK_ENTITIES.register("auto_crafter", () -> BlockEntityType.Builder.of(AutoCrafterBlockEntity::new,
                    ModBlocks.AUTO_CRAFTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChargerBlockEntity>> CHARGER_ENTITY =
            BLOCK_ENTITIES.register("charger", () -> BlockEntityType.Builder.of(ChargerBlockEntity::new,
                    ModBlocks.CHARGER.get()).build(null));

    private static RegistryObject<BlockEntityType<SolarPanelBlockEntity>> createSolarPanelBlockEntity(String name,
    RegistryObject<SolarPanelBlock> blockRegistryObject) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of((blockPos, state) -> new SolarPanelBlockEntity(blockPos, state,
                        blockRegistryObject.get().getTier()), blockRegistryObject.get()).build(null));
    }
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_1 =
            createSolarPanelBlockEntity("solar_panel_1", ModBlocks.SOLAR_PANEL_1);
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_2 =
            createSolarPanelBlockEntity("solar_panel_2", ModBlocks.SOLAR_PANEL_2);
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_3 =
            createSolarPanelBlockEntity("solar_panel_3", ModBlocks.SOLAR_PANEL_3);
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_4 =
            createSolarPanelBlockEntity("solar_panel_4", ModBlocks.SOLAR_PANEL_4);
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_5 =
            createSolarPanelBlockEntity("solar_panel_5", ModBlocks.SOLAR_PANEL_5);

    public static final RegistryObject<BlockEntityType<CoalEngineBlockEntity>> COAL_ENGINE_ENTITY =
            BLOCK_ENTITIES.register("coal_engine", () -> BlockEntityType.Builder.of(CoalEngineBlockEntity::new,
                    ModBlocks.COAL_ENGINE.get()).build(null));

    public static final RegistryObject<BlockEntityType<LightningGeneratorBlockEntity>> LIGHTING_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("lightning_generator", () -> BlockEntityType.Builder.of(LightningGeneratorBlockEntity::new,
                    ModBlocks.LIGHTNING_GENERATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<EnergizerBlockEntity>> ENERGIZER_ENTITY =
            BLOCK_ENTITIES.register("energizer", () -> BlockEntityType.Builder.of(EnergizerBlockEntity::new,
                    ModBlocks.ENERGIZER.get()).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
