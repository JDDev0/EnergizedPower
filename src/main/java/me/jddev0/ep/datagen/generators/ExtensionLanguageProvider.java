package me.jddev0.ep.datagen.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ExtensionLanguageProvider extends FabricLanguageProvider {
    protected final Map<String, String> templateTranslations = new HashMap<>();
    private final String modid;
    private final String locale;
    private final String sourceLangFilePath;

    public ExtensionLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                     String locale, String sourceLangFilePath) {
        super(output, locale, lookupProvider);

        this.modid = output.getModId();
        this.locale = locale;
        this.sourceLangFilePath = sourceLangFilePath;
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        Gson gson = new GsonBuilder().create();
        URL sourceLangFileUrl = this.getClass().getClassLoader().getResource(sourceLangFilePath);
        if(sourceLangFileUrl == null) {
            throw new RuntimeException("Error reading source language file for mod \"" + modid + "\" for locale \"" + locale +
                    "\" (resource path \"" + sourceLangFilePath + "\" not found)");
        }

        try(Reader reader = new InputStreamReader(sourceLangFileUrl.openStream())) {
            Map<String, String> translations = gson.fromJson(reader, new TypeToken<>() {});
            translations.forEach((key, value) -> {
                if(key.startsWith("_template")) {
                    templateTranslations.put(key, value);
                }else {
                    translationBuilder.add(key, value);
                }
            });

            addExtendedTranslations(registryLookup, translationBuilder);
        }catch(IOException e) {
            throw new RuntimeException("Error reading source language file for mod \"" + modid + "\" for locale \"" + locale +
                    "\" (resource path: \"" + sourceLangFilePath + "\")", e);
        }
    }

    protected abstract void addExtendedTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder);
}
