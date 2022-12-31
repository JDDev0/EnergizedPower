package me.jddev0.ep;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.item.*;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.*;
import me.jddev0.ep.villager.ModVillager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import javax.annotation.Nullable;

@Mod(EnergizedPowerMod.MODID)
public class EnergizedPowerMod {
    public static final String MODID = "energizedpower";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EnergizedPowerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModVillager.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::registerCreativeTab);
        modEventBus.addListener(this::addCreativeTab);

        DispenserBlock.registerBehavior(Items.SHEARS, new ShearsDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Level level = blockSource.getLevel();
                if(!level.isClientSide()) {
                    BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                    if(tryCraftCableInsulator(level, blockPos)) {
                        if(itemStack.hurt(1, level.getRandom(), null))
                            itemStack.setCount(0);

                        setSuccess(true);

                        return itemStack;
                    }
                }

                return super.execute(blockSource, itemStack);
            }

            private static boolean tryCraftCableInsulator(Level level, BlockPos blockPos) {
                BlockState blockstate = level.getBlockState(blockPos);
                if(blockstate.is(BlockTags.WOOL)) {
                    level.destroyBlock(blockPos, false, null);

                    ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5,
                            new ItemStack(ModItems.CABLE_INSULATOR.get(), 18), 0, 0, 0);
                    itemEntity.setPickUpDelay(20);
                    level.addFreshEntity(itemEntity);

                    level.playSound(null, blockPos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.f, 1.f);

                    return true;
                }

                return false;
            }
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
    }

    private void registerCreativeTab(CreativeModeTabEvent.Register event) {
        ModCreativeModeTab.registerCreative(event);
    }

    private ItemStack getChargedItemStack(Item item, int energy) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.getOrCreateTag().put("energy", IntTag.valueOf(energy));

        return itemStack;
    }

    private void addEmptyAndFullyChargedItem(CreativeModeTabEvent.BuildContents event, RegistryObject<Item> item, int capacity) {
        event.accept(item);
        event.accept(getChargedItemStack(item.get(), capacity));
    }

    private void addCreativeTab(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == ModCreativeModeTab.ENERGIZED_POWER_TAB) {
            event.accept(ModItems.ENERGIZED_POWER_BOOK);
            addEmptyAndFullyChargedItem(event, ModItems.ENERGY_ANALYZER, EnergyAnalyzerItem.ENERGY_CAPACITY);

            event.accept(ModBlocks.COPPER_CABLE_ITEM);
            event.accept(ModBlocks.ENERGIZED_COPPER_CABLE_ITEM);
            event.accept(ModBlocks.COAL_ENGINE_ITEM);
            event.accept(ModBlocks.LIGHTNING_GENERATOR_ITEM);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_1);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_2);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_3);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_4);
            event.accept(ModBlocks.SOLAR_PANEL_ITEM_5);

            event.accept(ModBlocks.AUTO_CRAFTER_ITEM);
            event.accept(ModBlocks.CHARGER_ITEM);
            event.accept(ModBlocks.UNCHARGER_ITEM);
            event.accept(ModBlocks.ENERGIZER_ITEM);
            event.accept(ModBlocks.CHARGING_STATION_ITEM);

            event.accept(ModItems.BASIC_SOLAR_CELL);
            event.accept(ModItems.ADVANCED_SOLAR_CELL);

            addEmptyAndFullyChargedItem(event, ModItems.INVENTORY_COAL_ENGINE, InventoryCoalEngine.CAPACITY);

            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_1, BatteryItem.Tier.BATTERY_1.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_2, BatteryItem.Tier.BATTERY_2.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_3, BatteryItem.Tier.BATTERY_3.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_4, BatteryItem.Tier.BATTERY_4.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_5, BatteryItem.Tier.BATTERY_5.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_6, BatteryItem.Tier.BATTERY_6.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_7, BatteryItem.Tier.BATTERY_7.getCapacity());
            addEmptyAndFullyChargedItem(event, ModItems.BATTERY_8, BatteryItem.Tier.BATTERY_8.getCapacity());

            event.accept(ModBlocks.BASIC_MACHINE_FRAME_ITEM);
            event.accept(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM);

            event.accept(ModBlocks.SILICON_BLOCK_ITEM);
            event.accept(ModItems.CABLE_INSULATOR);
            event.accept(ModItems.SILICON);
            event.accept(ModItems.ENERGIZED_COPPER_INGOT);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.AUTO_CRAFTER_MENU.get(), AutoCrafterScreen::new);
            MenuScreens.register(ModMenuTypes.CHARGER_MENU.get(), ChargerScreen::new);
            MenuScreens.register(ModMenuTypes.UNCHARGER_MENU.get(), UnchargerScreen::new);
            MenuScreens.register(ModMenuTypes.ENERGIZER_MENU.get(), EnergizerScreen::new);
            MenuScreens.register(ModMenuTypes.COAL_ENGINE_MENU.get(), CoalEngineScreen::new);

            event.enqueueWork(() -> {
                ItemProperties.registerGeneric(new ResourceLocation(MODID, "active"), (itemStack, level, entity, seed) -> {
                    Item item = itemStack.getItem();
                    return (item instanceof ActivatableItem && ((ActivatableItem)item).isActive(itemStack))?1.f:0.f;
                });
            });
            event.enqueueWork(() -> {
                ItemProperties.registerGeneric(new ResourceLocation(MODID, "working"), (itemStack, level, entity, seed) -> {
                    Item item = itemStack.getItem();
                    return (item instanceof WorkingItem && ((WorkingItem)item).isWorking(itemStack))?1.f:0.f;
                });
            });
        }
    }
}
