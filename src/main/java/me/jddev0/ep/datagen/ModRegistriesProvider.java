package me.jddev0.ep.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

public class ModRegistriesProvider extends FabricDynamicRegistryProvider {
    public ModRegistriesProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void configure(HolderLookup.Provider lookupProvider, Entries entries) {
        entries.addAll(lookupProvider.lookupOrThrow(Registries.CONFIGURED_FEATURE));
        entries.addAll(lookupProvider.lookupOrThrow(Registries.PLACED_FEATURE));
        entries.addAll(lookupProvider.lookupOrThrow(Registries.TEMPLATE_POOL));
        entries.addAll(lookupProvider.lookupOrThrow(Registries.STRUCTURE));
        entries.addAll(lookupProvider.lookupOrThrow(Registries.STRUCTURE_SET));
        entries.addAll(lookupProvider.lookupOrThrow(Registries.PAINTING_VARIANT));
    }

    @Override
    public String getName() {
        return "Registries Provider";
    }
}
