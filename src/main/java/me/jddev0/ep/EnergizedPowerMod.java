package me.jddev0.ep;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.ModBlockEntities;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.ModEntityTypes;
import me.jddev0.ep.entity.data.ModTrackedDataHandlers;
import me.jddev0.ep.event.PlayerInteractHandler;
import me.jddev0.ep.event.ServerStartingHandler;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.item.*;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.paintings.ModPaintings;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.ModMenuTypes;
import me.jddev0.ep.villager.ModVillager;
import me.jddev0.ep.worldgen.ModOreGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.base.SimpleEnergyItem;

public class EnergizedPowerMod implements ModInitializer {
    public static final String MODID = "energizedpower";
    public static final Logger LOGGER = LoggerFactory.getLogger("energizedpower");

    @Override
    public void onInitialize() {
        ModConfigs.registerConfigs(true);

        ModItems.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModRecipes.register();
        ModMenuTypes.register();
        ModVillager.register();
        ModEntityTypes.register();
        ModTrackedDataHandlers.register();
        ModPaintings.register();

        ModFluids.register();

        ModBlockBehaviors.register();

        ModCreativeModeTab.register();

        ModOreGeneration.register();

        ModMessages.registerPacketsC2S();

        PlayerInteractHandler.EVENT.register(new PlayerInteractHandler());
        ServerLifecycleEvents.SERVER_STARTING.register(new ServerStartingHandler());

        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SAWDUST_BLOCK, 5, 20);
    }

    private ItemStack getChargedItemStack(Item item, long energy) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.getOrCreateNbt().put(SimpleEnergyItem.ENERGY_KEY, NbtLong.of(energy));

        return itemStack;
    }
}
