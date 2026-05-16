package me.jddev0.ep.datagen.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class ExtensionLanguageProvider extends LanguageProvider {
    protected final Map<String, String> templateTranslations = new HashMap<>();
    private final String modid;
    private final String locale;
    private final String sourceLangFilePath;

    public ExtensionLanguageProvider(PackOutput output, String modid, String locale, String sourceLangFilePath) {
        super(output, modid, locale);

        this.modid = modid;
        this.locale = locale;
        this.sourceLangFilePath = sourceLangFilePath;
    }

    @Override
    protected final void addTranslations() {
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
                    add(key, value);
                }
            });

            addExtendedTranslations();
        }catch(IOException e) {
            throw new RuntimeException("Error reading source language file for mod \"" + modid + "\" for locale \"" + locale +
                    "\" (resource path: \"" + sourceLangFilePath + "\")", e);
        }
    }

    protected abstract void addExtendedTranslations();
}
