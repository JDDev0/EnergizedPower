package me.jddev0.ep;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.behavior.ModBlockBehaviors;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.EPEntityTypes;
import me.jddev0.ep.entity.data.ModTrackedDataHandlers;
import me.jddev0.ep.event.PlayerInteractHandler;
import me.jddev0.ep.event.ServerStartingHandler;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.item.*;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.paintings.EPPaintings;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.villager.EPVillager;
import me.jddev0.ep.worldgen.ModOreGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtLong;
import team.reborn.energy.api.base.SimpleEnergyItem;

public class EnergizedPowerMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ModConfigs.registerConfigs(true);

        EPItems.register();
        EPBlocks.register();
        EPBlockEntities.register();
        EPRecipes.register();
        EPMenuTypes.register();
        EPVillager.register();
        EPEntityTypes.register();
        ModTrackedDataHandlers.register();
        EPPaintings.register();

        EPFluids.register();

        ModBlockBehaviors.register();

        EPCreativeModeTab.register();

        ModOreGeneration.register();

        ModMessages.registerPacketsC2S();

        PlayerInteractHandler.EVENT.register(new PlayerInteractHandler());
        ServerLifecycleEvents.SERVER_STARTING.register(new ServerStartingHandler());

        FlammableBlockRegistry.getDefaultInstance().add(EPBlocks.SAWDUST_BLOCK, 5, 20);
    }

    private ItemStack getChargedItemStack(Item item, long energy) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.getOrCreateNbt().put(SimpleEnergyItem.ENERGY_KEY, NbtLong.of(energy));

        return itemStack;
    }
}
