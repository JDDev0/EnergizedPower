package me.jddev0.ep.datagen;

import me.jddev0.ep.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tag.BlockTags;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataGenerator output) {
        super(output);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).
                add(ModBlocks.SAWDUST_BLOCK);

        getOrCreateTagBuilder(BlockTags.PREVENT_MOB_SPAWNING_INSIDE).
                add(ModBlocks.ITEM_CONVEYOR_BELT);
    }
}
