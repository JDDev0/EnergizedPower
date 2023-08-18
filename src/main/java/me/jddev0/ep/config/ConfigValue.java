package me.jddev0.ep.config;

import me.jddev0.ep.config.validation.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigValue<T> {
    @NotNull
    private final String key;

    @Nullable
    private final String comment;

    @NotNull
    private final T defaultValue;

    @NotNull
    private T value;

    private final ValueValidator<? super T> validator;

    private boolean loaded = false;

    public ConfigValue(@NotNull String key, @NotNull T defaultValue) {
        this(key, null, defaultValue);
    }

    public ConfigValue(@NotNull String key, @Nullable String comment, @NotNull T defaultValue) {
        this(key, comment, defaultValue, null);
    }

    public ConfigValue(@NotNull String key, @Nullable String comment, @NotNull T defaultValue,
                       @Nullable ValueValidator<? super T> validator) {
        this.key = key;
        this.comment = comment;
        this.value = this.defaultValue = defaultValue;

        this.validator = validator;
    }

    public @NotNull String getKey() {
        return key;
    }

    public @Nullable String getComment() {
        return comment;
    }

    protected @NotNull T getDefaultValue() {
        return defaultValue;
    }

    public @NotNull T getValue() {
        return value;
    }

    public void setValue(@NotNull T value) throws ConfigValidationException {
        validate(value);

        this.value = value;
    }

    public @NotNull List<String> getValidationCommentLines() {
        List<String> commentLines = new ArrayList<>();
        if(validator != null)
            commentLines.addAll(validator.getValidationCommentLines());

        return commentLines;
    }

    public boolean isLoaded() {
        return loaded;
    }

    void manuallyLoaded() {
        loaded = true;
    }

    public void read(@NotNull String rawValue) throws ConfigValidationException {
        loaded = true;

        setValue(readInternal(rawValue));
    }

    public @NotNull String write() {
        return writeInternal(value);
    }

    public @NotNull String writeDefault() {
        return writeInternal(defaultValue);
    }

    public void validate(@NotNull T value) throws ConfigValidationException {
        if(validator != null)
            validator.validate(value);
    }

    protected abstract @NotNull T readInternal(@NotNull String rawValue) throws ConfigValidationException;

    protected abstract @NotNull String writeInternal(@NotNull T value);
}
