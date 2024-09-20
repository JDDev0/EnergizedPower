package me.jddev0.ep.datagen;

import me.jddev0.ep.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookupProvider) {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).
                add(ModBlocks.SAWDUST_BLOCK);

        getOrCreateTagBuilder(BlockTags.PREVENT_MOB_SPAWNING_INSIDE).
                add(ModBlocks.ITEM_CONVEYOR_BELT);
    }
}
