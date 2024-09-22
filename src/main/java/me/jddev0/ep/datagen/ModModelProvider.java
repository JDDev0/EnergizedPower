package me.jddev0.ep.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataGenerator output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        new ModBlockStateProvider(generator).registerBlocks();
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        new ModItemModelProvider(generator).registerItems();
    }
}
