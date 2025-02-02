package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, EPAPI.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        new ModBlockStateProvider(blockModels).registerStatesAndModels();
        new ModItemModelProvider(itemModels).registerItems();
    }
}
