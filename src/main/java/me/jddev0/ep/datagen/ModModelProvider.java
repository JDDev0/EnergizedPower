package me.jddev0.ep.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        new ModBlockStateProvider(generator).registerBlocks();
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        new ModItemModelProvider(generator).registeritems();
    }
}
