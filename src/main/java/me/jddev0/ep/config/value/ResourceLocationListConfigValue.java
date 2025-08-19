package me.jddev0.ep.config.value;

import me.jddev0.ep.config.ConfigValidationException;
import me.jddev0.ep.config.ConfigValue;
import me.jddev0.ep.config.validation.ValueValidator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ResourceLocationListConfigValue extends ConfigValue<List<@NotNull ResourceLocation>> {
    public ResourceLocationListConfigValue(@NotNull String key, @NotNull List<@NotNull ResourceLocation> defaultValue) {
        this(key, null, defaultValue);
    }

    public ResourceLocationListConfigValue(@NotNull String key, @Nullable String comment, @NotNull List<@NotNull ResourceLocation> defaultValue) {
        this(key, comment, defaultValue, null);
    }

    public ResourceLocationListConfigValue(@NotNull String key, @Nullable String comment, @NotNull List<@NotNull ResourceLocation> defaultValue,
                                           @Nullable ValueValidator<List<@NotNull ResourceLocation>> customValidator) {
        super(key, comment, new ArrayList<>(defaultValue), customValidator);
    }

    @Override
    public @NotNull List<String> getValidationCommentLines() {
        List<String> commentLines = new ArrayList<>();

        commentLines.add("Syntax (whitespaces are ignored): [namespace1:path1, namespace2:path2, ...]");

        commentLines.addAll(super.getValidationCommentLines());

        return commentLines;
    }

    @Override
    public void validate(@NotNull List<@NotNull ResourceLocation> value) throws ConfigValidationException {
        super.validate(value);
    }

    @Override
    protected @NotNull List<@NotNull ResourceLocation> readInternal(@NotNull String rawValue) throws ConfigValidationException {
        if(rawValue.equals("[]"))
            return new ArrayList<>(0);

        if(!rawValue.startsWith("[") || !rawValue.endsWith("]"))
            throw new ConfigValidationException("List syntax: Must start with \"[\" and end with \"]\"");

        rawValue = rawValue.substring(1, rawValue.length() - 1).trim();
        if(rawValue.startsWith(",") || rawValue.endsWith(","))
            throw new ConfigValidationException("Value must not start with \"[,\" or end with \",]\"");

        List<@NotNull ResourceLocation> resourceLocations = new ArrayList<>();
        String[] tokens = rawValue.split(",");
        for(int i = 0;i < tokens.length;i++) {
            String token = tokens[i].trim();
            if(token.isEmpty())
                throw new ConfigValidationException("Value must not be empty at index " + i);

            ResourceLocation resourceLocation = ResourceLocation.tryParse(token);
            if(resourceLocation == null)
                throw new ConfigValidationException("Invalid value at index " + i + ": \"" + token + "\"");

            resourceLocations.add(resourceLocation);
        }

        return new ArrayList<>(resourceLocations);
    }

    @Override
    protected @NotNull String writeInternal(@NotNull List<@NotNull ResourceLocation> value) {
        StringBuilder builder = new StringBuilder("[");

        for(ResourceLocation resourceLocation:value)
            builder.append(resourceLocation).append(", ");

        if(!value.isEmpty())
            builder.delete(builder.length() - 2, builder.length());

        builder.append("]");
        return builder.toString();
    }
}
