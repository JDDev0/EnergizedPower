package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ModReloadableRegistriesProvider {
    private ModReloadableRegistriesProvider() {}

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().
            add(Registries.LOOT_TABLE, ModLootTableProvider.create()).
            add(Registries.ADVANCEMENT, ModAdvancementProvider.create()).
            add(ModRecipeProvider.create());

    public static DatapackBuiltinEntriesProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> worldRegistries,
                                                        CompletableFuture<HolderLookup.Provider> reloadableRegistries) {
        return DatapackBuiltinEntriesProvider.
                forReloadableLayer(output, "Energized Power (Reloadable)", worldRegistries, reloadableRegistries, BUILDER, Set.of(EPAPI.MOD_ID));
    }
}
