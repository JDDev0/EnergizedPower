package me.jddev0.ep.config.value;

import me.jddev0.ep.config.ConfigValidationException;
import me.jddev0.ep.config.ConfigValue;
import me.jddev0.ep.config.validation.ValueValidator;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IdentifierListConfigValue extends ConfigValue<List<@NotNull Identifier>> {
    public IdentifierListConfigValue(@NotNull String key, @NotNull List<@NotNull Identifier> defaultValue) {
        this(key, null, defaultValue);
    }

    public IdentifierListConfigValue(@NotNull String key, @Nullable String comment, @NotNull List<@NotNull Identifier> defaultValue) {
        this(key, comment, defaultValue, null);
    }

    public IdentifierListConfigValue(@NotNull String key, @Nullable String comment, @NotNull List<@NotNull Identifier> defaultValue,
                                           @Nullable ValueValidator<List<@NotNull Identifier>> customValidator) {
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
    public void validate(@NotNull List<@NotNull Identifier> value) throws ConfigValidationException {
        super.validate(value);
    }

    @Override
    protected @NotNull List<@NotNull Identifier> readInternal(@NotNull String rawValue) throws ConfigValidationException {
        if(rawValue.equals("[]"))
            return new ArrayList<>(0);

        if(!rawValue.startsWith("[") || !rawValue.endsWith("]"))
            throw new ConfigValidationException("List syntax: Must start with \"[\" and end with \"]\"");

        rawValue = rawValue.substring(1, rawValue.length() - 1).trim();
        if(rawValue.startsWith(",") || rawValue.endsWith(","))
            throw new ConfigValidationException("Value must not start with \"[,\" or end with \",]\"");

        List<@NotNull Identifier> identifiers = new ArrayList<>();
        String[] tokens = rawValue.split(",");
        for(int i = 0;i < tokens.length;i++) {
            String token = tokens[i].trim();
            if(token.isEmpty())
                throw new ConfigValidationException("Value must not be empty at index " + i);

            Identifier identifier = Identifier.tryParse(token);
            if(identifier == null)
                throw new ConfigValidationException("Invalid value at index " + i + ": \"" + token + "\"");

            identifiers.add(identifier);
        }

        return new ArrayList<>(identifiers);
    }

    @Override
    protected @NotNull String writeInternal(@NotNull List<@NotNull Identifier> value) {
        StringBuilder builder = new StringBuilder("[");

        for(Identifier identifier:value)
            builder.append(identifier).append(", ");

        if(!value.isEmpty())
            builder.delete(builder.length() - 2, builder.length());

        builder.append("]");
        return builder.toString();
    }
}
