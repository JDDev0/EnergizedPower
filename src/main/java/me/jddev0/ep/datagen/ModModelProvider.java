package me.jddev0.ep.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        new ModBlockStateProvider(generator).registerBlocks();
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        new ModItemModelProvider(generator).registeritems();
    }
}
