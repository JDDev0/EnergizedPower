package me.jddev0.ep.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModRegistriesProvider extends FabricDynamicRegistryProvider {
    public ModRegistriesProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookupProvider, Entries entries) {
        entries.addAll(lookupProvider.getWrapperOrThrow(RegistryKeys.PAINTING_VARIANT));
    }

    @Override
    public String getName() {
        return "Registries Provider";
    }
}
