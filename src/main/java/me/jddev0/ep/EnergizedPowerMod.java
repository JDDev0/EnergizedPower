package me.jddev0.ep;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.renderer.FluidTankBlockEntityRenderer;
import me.jddev0.ep.block.entity.renderer.ItemConveyorBeltBlockEntityRenderer;
import me.jddev0.ep.client.item.property.bool.ActiveProperty;
import me.jddev0.ep.client.item.property.bool.WorkingProperty;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.EPEntityTypes;
import me.jddev0.ep.fluid.EPFluidTypes;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.input.ModKeyBindings;
import me.jddev0.ep.integration.cctweaked.EnergizedPowerCCTweakedIntegration;
import me.jddev0.ep.integration.cctweaked.EnergizedPowerCCTweakedUtils;
import me.jddev0.ep.integration.jei.EnergizedPowerJEIPlugin;
import me.jddev0.ep.integration.jei.EnergizedPowerJEIUtils;
import me.jddev0.ep.item.*;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.item.energy.ItemCapabilityEnergy;
import me.jddev0.ep.loading.EnergizedPowerBookReloadListener;
import me.jddev0.ep.machine.tier.BatteryTier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.ModMessagesClient;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.screen.*;
import me.jddev0.ep.villager.EPVillager;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Mod(EPAPI.MOD_ID)
public class EnergizedPowerMod {
    public EnergizedPowerMod(IEventBus modEventBus) {
        ModConfigs.registerConfigs(true);

        EPDataComponentTypes.register(modEventBus);

        EPItems.register(modEventBus);
        EPBlocks.register(modEventBus);
        EPBlockEntities.register(modEventBus);
        EPRecipes.register(modEventBus);
        EPMenuTypes.register(modEventBus);
        EPVillager.register(modEventBus);
        EPEntityTypes.register(modEventBus);

        EPFluids.register(modEventBus);
        EPFluidTypes.register(modEventBus);

        ModBlockBehaviors.register();

        EPCreativeModeTab.register(modEventBus);

        modEventBus.addListener(this::onLoadComplete);
        modEventBus.addListener(this::addCreativeTab);
        modEventBus.addListener(this::registerCapabilities);

        modEventBus.addListener(ModMessages::register);

        if(EnergizedPowerJEIUtils.isJEIAvailable()) {
            NeoForge.EVENT_BUS.addListener(false, OnDatapackSyncEvent.class, e -> e.sendRecipes(
                    ChargerRecipe.Type.INSTANCE,
                    CrusherRecipe.Type.INSTANCE,
                    PulverizerRecipe.Type.INSTANCE,
                    SawmillRecipe.Type.INSTANCE,
                    CompressorRecipe.Type.INSTANCE,
                    MetalPressRecipe.Type.INSTANCE,
                    AssemblingMachineRecipe.Type.INSTANCE,
                    PlantGrowthChamberRecipe.Type.INSTANCE,
                    PlantGrowthChamberFertilizerRecipe.Type.INSTANCE,
                    EnergizerRecipe.Type.INSTANCE,
                    CrystalGrowthChamberRecipe.Type.INSTANCE,
                    PressMoldMakerRecipe.Type.INSTANCE,
                    AlloyFurnaceRecipe.Type.INSTANCE,
                    StoneLiquefierRecipe.Type.INSTANCE,
                    StoneSolidifierRecipe.Type.INSTANCE,
                    FiltrationPlantRecipe.Type.INSTANCE,
                    FluidTransposerRecipe.Type.INSTANCE
            ));
        }
    }

    public void onLoadComplete(final FMLLoadCompleteEvent event) {
        if(EnergizedPowerCCTweakedUtils.isCCTweakedAvailable())
            EnergizedPowerCCTweakedIntegration.register();
    }

    private ItemStack getChargedItemStack(Item item, int energy) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.set(EPDataComponentTypes.ENERGY, energy);

        return itemStack;
    }

    private void addEmptyAndFullyChargedItem(BuildCreativeModeTabContentsEvent event, ItemLike item, int capacity) {
        event.accept(item);
        event.accept(getChargedItemStack(item.asItem(), capacity));
    }

    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if(event.getTab() == EPCreativeModeTab.ENERGIZED_POWER_TAB.get()) {
            event.accept(EPItems.ENERGIZED_POWER_BOOK);
            addEmptyAndFullyChargedItem(event, EPItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);
            addEmptyAndFullyChargedItem(event, EPItems.FLUID_ANALYZER, FluidAnalyzerItem.ENERGY_CAPACITY);

            event.accept(EPItems.WOODEN_HAMMER);
            event.accept(EPItems.STONE_HAMMER);
            event.accept(EPItems.COPPER_HAMMER);
            event.accept(EPItems.IRON_HAMMER);
            event.accept(EPItems.GOLDEN_HAMMER);
            event.accept(EPItems.DIAMOND_HAMMER);
            event.accept(EPItems.NETHERITE_HAMMER);

            event.accept(EPItems.CUTTER);

            event.accept(EPItems.WRENCH);

            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM);
            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM);
            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM);
            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM);
            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM);
            event.accept(EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM);
            event.accept(EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM);
            event.accept(EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM);

            event.accept(EPBlocks.ITEM_SILO_TINY_ITEM);
            event.accept(EPBlocks.ITEM_SILO_SMALL_ITEM);
            event.accept(EPBlocks.ITEM_SILO_MEDIUM_ITEM);
            event.accept(EPBlocks.ITEM_SILO_LARGE_ITEM);
            event.accept(EPBlocks.ITEM_SILO_GIANT_ITEM);
            event.accept(EPBlocks.CREATIVE_ITEM_SILO_ITEM);

            event.accept(EPBlocks.IRON_FLUID_PIPE_ITEM);
            event.accept(EPBlocks.GOLDEN_FLUID_PIPE_ITEM);

            event.accept(EPBlocks.TIN_CABLE_ITEM);
            event.accept(EPBlocks.COPPER_CABLE_ITEM);
            event.accept(EPBlocks.GOLD_CABLE_ITEM);
            event.accept(EPBlocks.ENERGIZED_COPPER_CABLE_ITEM);
            event.accept(EPBlocks.ENERGIZED_GOLD_CABLE_ITEM);
            event.accept(EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM);

            event.accept(EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM);
            event.accept(EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM);
            event.accept(EPBlocks.HV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.HV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.HV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.CONFIGURABLE_HV_TRANSFORMER_ITEM);
            event.accept(EPBlocks.EHV_TRANSFORMER_1_TO_N_ITEM);
            event.accept(EPBlocks.EHV_TRANSFORMER_3_TO_3_ITEM);
            event.accept(EPBlocks.EHV_TRANSFORMER_N_TO_1_ITEM);
            event.accept(EPBlocks.CONFIGURABLE_EHV_TRANSFORMER_ITEM);

            event.accept(EPBlocks.PRESS_MOLD_MAKER_ITEM);
            event.accept(EPBlocks.ALLOY_FURNACE_ITEM);

            event.accept(EPBlocks.COAL_ENGINE_ITEM);
            event.accept(EPBlocks.HEAT_GENERATOR_ITEM);
            event.accept(EPBlocks.THERMAL_GENERATOR_ITEM);
            event.accept(EPBlocks.LIGHTNING_GENERATOR_ITEM);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_1);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_2);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_3);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_4);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_5);
            event.accept(EPBlocks.SOLAR_PANEL_ITEM_6);

            event.accept(EPBlocks.BATTERY_BOX_ITEM);
            event.accept(EPBlocks.ADVANCED_BATTERY_BOX_ITEM);
            event.accept(EPBlocks.CREATIVE_BATTERY_BOX_ITEM);

            event.accept(EPBlocks.POWERED_LAMP_ITEM);
            event.accept(EPBlocks.POWERED_FURNACE_ITEM);
            event.accept(EPBlocks.ADVANCED_POWERED_FURNACE_ITEM);
            event.accept(EPBlocks.AUTO_CRAFTER_ITEM);
            event.accept(EPBlocks.ADVANCED_AUTO_CRAFTER_ITEM);
            event.accept(EPBlocks.CRUSHER_ITEM);
            event.accept(EPBlocks.ADVANCED_CRUSHER_ITEM);
            event.accept(EPBlocks.PULVERIZER_ITEM);
            event.accept(EPBlocks.ADVANCED_PULVERIZER_ITEM);
            event.accept(EPBlocks.SAWMILL_ITEM);
            event.accept(EPBlocks.COMPRESSOR_ITEM);
            event.accept(EPBlocks.METAL_PRESS_ITEM);
            event.accept(EPBlocks.AUTO_PRESS_MOLD_MAKER_ITEM);
            event.accept(EPBlocks.AUTO_STONECUTTER_ITEM);
            event.accept(EPBlocks.ASSEMBLING_MACHINE_ITEM);
            event.accept(EPBlocks.INDUCTION_SMELTER_ITEM);
            event.accept(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM);
            event.accept(EPBlocks.STONE_LIQUEFIER_ITEM);
            event.accept(EPBlocks.STONE_SOLIDIFIER_ITEM);
            event.accept(EPBlocks.FILTRATION_PLANT_ITEM);
            event.accept(EPBlocks.FLUID_TRANSPOSER_ITEM);
            event.accept(EPBlocks.BLOCK_PLACER_ITEM);
            event.accept(EPBlocks.FLUID_TANK_SMALL_ITEM);
            event.accept(EPBlocks.FLUID_TANK_MEDIUM_ITEM);
            event.accept(EPBlocks.FLUID_TANK_LARGE_ITEM);
            event.accept(EPBlocks.CREATIVE_FLUID_TANK_ITEM);
            event.accept(EPBlocks.FLUID_FILLER_ITEM);
            event.accept(EPBlocks.FLUID_DRAINER_ITEM);
            event.accept(EPBlocks.FLUID_PUMP_ITEM);
            event.accept(EPBlocks.ADVANCED_FLUID_PUMP_ITEM);
            event.accept(EPBlocks.DRAIN_ITEM);
            event.accept(EPBlocks.CHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_CHARGER_ITEM);
            event.accept(EPBlocks.UNCHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_UNCHARGER_ITEM);
            event.accept(EPBlocks.MINECART_CHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_MINECART_CHARGER_ITEM);
            event.accept(EPBlocks.MINECART_UNCHARGER_ITEM);
            event.accept(EPBlocks.ADVANCED_MINECART_UNCHARGER_ITEM);

            event.accept(EPBlocks.ENERGIZER_ITEM);
            event.accept(EPBlocks.CHARGING_STATION_ITEM);
            event.accept(EPBlocks.CRYSTAL_GROWTH_CHAMBER);

            event.accept(EPBlocks.WEATHER_CONTROLLER_ITEM);
            event.accept(EPBlocks.TIME_CONTROLLER_ITEM);
            event.accept(EPBlocks.TELEPORTER_ITEM);

            addEmptyAndFullyChargedItem(event, EPItems.INVENTORY_COAL_ENGINE, InventoryCoalEngineItem.CAPACITY);
            event.accept(EPItems.INVENTORY_CHARGER);

            addEmptyAndFullyChargedItem(event, EPItems.INVENTORY_TELEPORTER, InventoryTeleporterItem.CAPACITY);

            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_1, BatteryTier.BATTERY_1.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_2, BatteryTier.BATTERY_2.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_3, BatteryTier.BATTERY_3.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_4, BatteryTier.BATTERY_4.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_5, BatteryTier.BATTERY_5.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_6, BatteryTier.BATTERY_6.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_7, BatteryTier.BATTERY_7.getCapacity());
            addEmptyAndFullyChargedItem(event, EPItems.BATTERY_8, BatteryTier.BATTERY_8.getCapacity());
            event.accept(EPItems.CREATIVE_BATTERY);

            event.accept(EPItems.BATTERY_BOX_MINECART);
            event.accept(EPItems.ADVANCED_BATTERY_BOX_MINECART);

            event.accept(EPBlocks.BASIC_MACHINE_FRAME_ITEM);
            event.accept(EPBlocks.HARDENED_MACHINE_FRAME_ITEM);
            event.accept(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM);
            event.accept(EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM);

            event.accept(EPItems.BASIC_SOLAR_CELL);
            event.accept(EPItems.ADVANCED_SOLAR_CELL);
            event.accept(EPItems.REINFORCED_ADVANCED_SOLAR_CELL);

            event.accept(EPItems.BASIC_CIRCUIT);
            event.accept(EPItems.ADVANCED_CIRCUIT);
            event.accept(EPItems.PROCESSING_UNIT);

            event.accept(EPItems.TELEPORTER_PROCESSING_UNIT);
            event.accept(EPItems.TELEPORTER_MATRIX);

            event.accept(EPItems.BASIC_UPGRADE_MODULE);
            event.accept(EPItems.ADVANCED_UPGRADE_MODULE);
            event.accept(EPItems.REINFORCED_ADVANCED_UPGRADE_MODULE);

            event.accept(EPItems.SPEED_UPGRADE_MODULE_1);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_2);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_3);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_4);
            event.accept(EPItems.SPEED_UPGRADE_MODULE_5);

            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4);
            event.accept(EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5);

            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_3);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_4);
            event.accept(EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_5);

            event.accept(EPItems.DURATION_UPGRADE_MODULE_1);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_2);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_3);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_4);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_5);
            event.accept(EPItems.DURATION_UPGRADE_MODULE_6);

            event.accept(EPItems.RANGE_UPGRADE_MODULE_1);
            event.accept(EPItems.RANGE_UPGRADE_MODULE_2);
            event.accept(EPItems.RANGE_UPGRADE_MODULE_3);

            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4);
            event.accept(EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5);

            event.accept(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1);
            event.accept(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2);
            event.accept(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_3);
            event.accept(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_4);
            event.accept(EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_5);

            event.accept(EPItems.BLAST_FURNACE_UPGRADE_MODULE);
            event.accept(EPItems.SMOKER_UPGRADE_MODULE);

            event.accept(EPItems.MOON_LIGHT_UPGRADE_MODULE_1);
            event.accept(EPItems.MOON_LIGHT_UPGRADE_MODULE_2);
            event.accept(EPItems.MOON_LIGHT_UPGRADE_MODULE_3);

            event.accept(EPBlocks.SILICON_BLOCK_ITEM);
            event.accept(EPBlocks.TIN_BLOCK_ITEM);
            event.accept(EPBlocks.SAWDUST_BLOCK_ITEM);
            event.accept(EPItems.CABLE_INSULATOR);
            event.accept(EPItems.CHARCOAL_FILTER);
            event.accept(EPItems.SAW_BLADE);
            event.accept(EPItems.CRYSTAL_MATRIX);
            event.accept(EPItems.SAWDUST);
            event.accept(EPItems.CHARCOAL_DUST);
            event.accept(EPItems.BASIC_FERTILIZER);
            event.accept(EPItems.GOOD_FERTILIZER);
            event.accept(EPItems.ADVANCED_FERTILIZER);
            event.accept(EPItems.RAW_GEAR_PRESS_MOLD);
            event.accept(EPItems.RAW_ROD_PRESS_MOLD);
            event.accept(EPItems.RAW_WIRE_PRESS_MOLD);
            event.accept(EPItems.GEAR_PRESS_MOLD);
            event.accept(EPItems.ROD_PRESS_MOLD);
            event.accept(EPItems.WIRE_PRESS_MOLD);
            event.accept(EPItems.SILICON);
            event.accept(EPItems.TIN_DUST);
            event.accept(EPItems.COPPER_DUST);
            event.accept(EPItems.IRON_DUST);
            event.accept(EPItems.GOLD_DUST);
            event.accept(EPItems.TIN_NUGGET);
            event.accept(EPItems.TIN_INGOT);
            event.accept(EPItems.TIN_PLATE);
            event.accept(EPItems.COPPER_PLATE);
            event.accept(EPItems.IRON_PLATE);
            event.accept(EPItems.GOLD_PLATE);
            event.accept(EPItems.STEEL_INGOT);
            event.accept(EPItems.REDSTONE_ALLOY_INGOT);
            event.accept(EPItems.ADVANCED_ALLOY_INGOT);
            event.accept(EPItems.ADVANCED_ALLOY_PLATE);
            event.accept(EPItems.IRON_GEAR);
            event.accept(EPItems.IRON_ROD);
            event.accept(EPItems.TIN_WIRE);
            event.accept(EPItems.COPPER_WIRE);
            event.accept(EPItems.GOLD_WIRE);
            event.accept(EPItems.ENERGIZED_COPPER_INGOT);
            event.accept(EPItems.ENERGIZED_GOLD_INGOT);
            event.accept(EPItems.ENERGIZED_COPPER_PLATE);
            event.accept(EPItems.ENERGIZED_GOLD_PLATE);
            event.accept(EPItems.ENERGIZED_COPPER_WIRE);
            event.accept(EPItems.ENERGIZED_GOLD_WIRE);
            event.accept(EPItems.ENERGIZED_CRYSTAL_MATRIX);

            event.accept(EPFluids.DIRTY_WATER_BUCKET_ITEM);

            event.accept(EPItems.STONE_PEBBLE);

            event.accept(EPItems.RAW_TIN);
            event.accept(EPBlocks.TIN_ORE_ITEM);
            event.accept(EPBlocks.DEEPSLATE_TIN_ORE_ITEM);
            event.accept(EPBlocks.RAW_TIN_BLOCK_ITEM);
        }
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        //Items
        for(Item item:BuiltInRegistries.ITEM) {
            if(item instanceof EnergizedPowerEnergyItem energizedPowerEnergyItem) {
                event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> {
                    return new ItemCapabilityEnergy(stack, energizedPowerEnergyItem.getEnergyStorageProvider().apply(stack));
                }, item);
            }
        }

        //Block Entities
        EPBlockEntities.registerCapabilities(event);
    }

    @EventBusSubscriber(modid = EPAPI.MOD_ID, value = Dist.CLIENT)
    public static class ClientGameEvents {
        @SubscribeEvent
        public static void onRecipesReceived(RecipesReceivedEvent event) {
            if(EnergizedPowerJEIUtils.isJEIAvailable()) {
                EnergizedPowerJEIPlugin.recipeMap = event.getRecipeMap();
            }
        }
    }

    @EventBusSubscriber(modid = EPAPI.MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModConfigs.registerConfigs(false);

            EntityRenderers.register(EPEntityTypes.BATTERY_BOX_MINECART.get(),
                    entity -> new MinecartRenderer(entity, new ModelLayerLocation(
                            ResourceLocation.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));
            EntityRenderers.register(EPEntityTypes.ADVANCED_BATTERY_BOX_MINECART.get(),
                    entity -> new MinecartRenderer(entity, new ModelLayerLocation(
                            ResourceLocation.fromNamespaceAndPath("minecraft", "chest_minecart"), "main")));

            ItemBlockRenderTypes.setRenderLayer(EPFluids.DIRTY_WATER.get(), ChunkSectionLayer.TRANSLUCENT);
            ItemBlockRenderTypes.setRenderLayer(EPFluids.FLOWING_DIRTY_WATER.get(), ChunkSectionLayer.TRANSLUCENT);

            BlockEntityRenderers.register(EPBlockEntities.BASIC_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
            BlockEntityRenderers.register(EPBlockEntities.FAST_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
            BlockEntityRenderers.register(EPBlockEntities.EXPRESS_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntityRenderer::new);
            BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_SMALL_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_MEDIUM_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(EPBlockEntities.FLUID_TANK_LARGE_ENTITY.get(), FluidTankBlockEntityRenderer::new);
            BlockEntityRenderers.register(EPBlockEntities.CREATIVE_FLUID_TANK_ENTITY.get(), FluidTankBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterClientPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
            ModMessagesClient.register(event);
        }

        @SubscribeEvent
        static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public int getTintColor() {
                    return EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getTintColor();
                }

                @Override
                public ResourceLocation getStillTexture() {
                    return EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getStillTexture();
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getFlowingTexture();
                }

                @Override
                public @Nullable ResourceLocation getOverlayTexture() {
                    return EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getOverlayTexture();
                }

                @Override
                public Vector4f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
                    Vector3f fogColor = EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get().getFogColor();
                    return new Vector4f(
                            fogColor.x,
                            fogColor.y,
                            fogColor.z,
                            fluidFogColor.w
                    );
                }

                @Override
                public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
                    //TODO FIX
                }
            }, EPFluidTypes.DIRTY_WATER_FLUID_TYPE.get());
        }

        @SubscribeEvent
        public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
            event.register(EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
            event.register(EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
            event.register(EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_MENU.get(), ItemConveyorBeltLoaderScreen::new);
            event.register(EPMenuTypes.BASIC_ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
            event.register(EPMenuTypes.FAST_ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
            event.register(EPMenuTypes.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_MENU.get(), ItemConveyorBeltSorterScreen::new);
            event.register(EPMenuTypes.AUTO_CRAFTER_MENU.get(), AutoCrafterScreen::new);
            event.register(EPMenuTypes.ADVANCED_AUTO_CRAFTER_MENU.get(), AdvancedAutoCrafterScreen::new);
            event.register(EPMenuTypes.CRUSHER_MENU.get(), CrusherScreen::new);
            event.register(EPMenuTypes.ADVANCED_CRUSHER_MENU.get(), AdvancedCrusherScreen::new);
            event.register(EPMenuTypes.PULVERIZER_MENU.get(), PulverizerScreen::new);
            event.register(EPMenuTypes.ADVANCED_PULVERIZER_MENU.get(), AdvancedPulverizerScreen::new);
            event.register(EPMenuTypes.SAWMILL_MENU.get(), SawmillScreen::new);
            event.register(EPMenuTypes.COMPRESSOR_MENU.get(), CompressorScreen::new);
            event.register(EPMenuTypes.PLANT_GROWTH_CHAMBER_MENU.get(), PlantGrowthChamberScreen::new);
            event.register(EPMenuTypes.STONE_LIQUEFIER_MENU.get(), StoneLiquefierScreen::new);
            event.register(EPMenuTypes.STONE_SOLIDIFIER_MENU.get(), StoneSolidifierScreen::new);
            event.register(EPMenuTypes.FILTRATION_PLANT_MENU.get(), FiltrationPlantScreen::new);
            event.register(EPMenuTypes.FLUID_TRANSPOSER_MENU.get(), FluidTransposerScreen::new);
            event.register(EPMenuTypes.BLOCK_PLACER_MENU.get(), BlockPlacerScreen::new);
            event.register(EPMenuTypes.FLUID_FILLER_MENU.get(), FluidFillerScreen::new);
            event.register(EPMenuTypes.FLUID_DRAINER_MENU.get(), FluidDrainerScreen::new);
            event.register(EPMenuTypes.FLUID_PUMP_MENU.get(), FluidPumpScreen::new);
            event.register(EPMenuTypes.ADVANCED_FLUID_PUMP_MENU.get(), AdvancedFluidPumpScreen::new);
            event.register(EPMenuTypes.DRAIN_MENU.get(), DrainScreen::new);
            event.register(EPMenuTypes.CHARGER_MENU.get(), ChargerScreen::new);
            event.register(EPMenuTypes.ADVANCED_CHARGER_MENU.get(), AdvancedChargerScreen::new);
            event.register(EPMenuTypes.UNCHARGER_MENU.get(), UnchargerScreen::new);
            event.register(EPMenuTypes.ADVANCED_UNCHARGER_MENU.get(), AdvancedUnchargerScreen::new);
            event.register(EPMenuTypes.ENERGIZER_MENU.get(), EnergizerScreen::new);
            event.register(EPMenuTypes.COAL_ENGINE_MENU.get(), CoalEngineScreen::new);
            event.register(EPMenuTypes.POWERED_FURNACE_MENU.get(), PoweredFurnaceScreen::new);
            event.register(EPMenuTypes.ADVANCED_POWERED_FURNACE_MENU.get(), AdvancedPoweredFurnaceScreen::new);
            event.register(EPMenuTypes.WEATHER_CONTROLLER_MENU.get(), WeatherControllerScreen::new);
            event.register(EPMenuTypes.TIME_CONTROLLER_MENU.get(), TimeControllerScreen::new);
            event.register(EPMenuTypes.TELEPORTER_MENU.get(), TeleporterScreen::new);
            event.register(EPMenuTypes.LIGHTNING_GENERATOR_MENU.get(), LightningGeneratorScreen::new);
            event.register(EPMenuTypes.CHARGING_STATION_MENU.get(), ChargingStationScreen::new);
            event.register(EPMenuTypes.CRYSTAL_GROWTH_CHAMBER_MENU.get(), CrystalGrowthChamberScreen::new);
            event.register(EPMenuTypes.HEAT_GENERATOR_MENU.get(), HeatGeneratorScreen::new);
            event.register(EPMenuTypes.THERMAL_GENERATOR_MENU.get(), ThermalGeneratorScreen::new);
            event.register(EPMenuTypes.BATTERY_BOX_MENU.get(), BatteryBoxScreen::new);
            event.register(EPMenuTypes.ADVANCED_BATTERY_BOX_MENU.get(), AdvancedBatteryBoxScreen::new);
            event.register(EPMenuTypes.CREATIVE_BATTERY_BOX_MENU.get(), CreativeBatteryBoxScreen::new);
            event.register(EPMenuTypes.MINECART_CHARGER_MENU.get(), MinecartChargerScreen::new);
            event.register(EPMenuTypes.ADVANCED_MINECART_CHARGER_MENU.get(), AdvancedMinecartChargerScreen::new);
            event.register(EPMenuTypes.MINECART_UNCHARGER_MENU.get(), MinecartUnchargerScreen::new);
            event.register(EPMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU.get(), AdvancedMinecartUnchargerScreen::new);
            event.register(EPMenuTypes.SOLAR_PANEL_MENU_1.get(), SolarPanelScreen::new);
            event.register(EPMenuTypes.SOLAR_PANEL_MENU_2.get(), SolarPanelScreen::new);
            event.register(EPMenuTypes.SOLAR_PANEL_MENU_3.get(), SolarPanelScreen::new);
            event.register(EPMenuTypes.SOLAR_PANEL_MENU_4.get(), SolarPanelScreen::new);
            event.register(EPMenuTypes.SOLAR_PANEL_MENU_5.get(), SolarPanelScreen::new);
            event.register(EPMenuTypes.SOLAR_PANEL_MENU_6.get(), SolarPanelScreen::new);
            event.register(EPMenuTypes.LV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.LV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.LV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.CONFIGURABLE_LV_TRANSFORMER_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.MV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.MV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.MV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.CONFIGURABLE_MV_TRANSFORMER_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.HV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.HV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.HV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.CONFIGURABLE_HV_TRANSFORMER_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.EHV_TRANSFORMER_1_TO_N_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.EHV_TRANSFORMER_3_TO_3_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.EHV_TRANSFORMER_N_TO_1_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.CONFIGURABLE_EHV_TRANSFORMER_MENU.get(), TransformerScreen::new);
            event.register(EPMenuTypes.PRESS_MOLD_MAKER_MENU.get(), PressMoldMakerScreen::new);
            event.register(EPMenuTypes.ALLOY_FURNACE_MENU.get(), AlloyFurnaceScreen::new);
            event.register(EPMenuTypes.METAL_PRESS_MENU.get(), MetalPressScreen::new);
            event.register(EPMenuTypes.AUTO_PRESS_MOLD_MAKER_MENU.get(), AutoPressMoldMakerScreen::new);
            event.register(EPMenuTypes.AUTO_STONECUTTER_MENU.get(), AutoStonecutterScreen::new);
            event.register(EPMenuTypes.ASSEMBLING_MACHINE_MENU.get(), AssemblingMachineScreen::new);
            event.register(EPMenuTypes.INDUCTION_SMELTER_MENU.get(), InductionSmelterScreen::new);
            event.register(EPMenuTypes.FLUID_TANK_SMALL.get(), FluidTankScreen::new);
            event.register(EPMenuTypes.FLUID_TANK_MEDIUM.get(), FluidTankScreen::new);
            event.register(EPMenuTypes.FLUID_TANK_LARGE.get(), FluidTankScreen::new);
            event.register(EPMenuTypes.CREATIVE_FLUID_TANK.get(), CreativeFluidTankScreen::new);
            event.register(EPMenuTypes.ITEM_SILO_TINY.get(), ItemSiloScreen::new);
            event.register(EPMenuTypes.ITEM_SILO_SMALL.get(), ItemSiloScreen::new);
            event.register(EPMenuTypes.ITEM_SILO_MEDIUM.get(), ItemSiloScreen::new);
            event.register(EPMenuTypes.ITEM_SILO_LARGE.get(), ItemSiloScreen::new);
            event.register(EPMenuTypes.ITEM_SILO_GIANT.get(), ItemSiloScreen::new);
            event.register(EPMenuTypes.CREATIVE_ITEM_SILO_MENU.get(), CreativeItemSiloScreen::new);

            event.register(EPMenuTypes.INVENTORY_CHARGER_MENU.get(), InventoryChargerScreen::new);
            event.register(EPMenuTypes.INVENTORY_TELEPORTER_MENU.get(), InventoryTeleporterScreen::new);

            event.register(EPMenuTypes.MINECART_BATTERY_BOX_MENU.get(), MinecartBatteryBoxScreen::new);
            event.register(EPMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU.get(), MinecartAdvancedBatteryBoxScreen::new);
        }

        @SubscribeEvent
        public static void onRegisterConditionalItemModelProperties(RegisterConditionalItemModelPropertyEvent event) {
            event.register(EPAPI.id("active"), ActiveProperty.CODEC);
            event.register(EPAPI.id("working"), WorkingProperty.CODEC);
        }

        @SubscribeEvent
        public static void loadBookPages(AddClientReloadListenersEvent event) {
            event.addListener(EPAPI.id("energized_power_book"), new EnergizedPowerBookReloadListener());
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(ModKeyBindings.TELEPORTER_USE_KEY);
        }
    }
}
