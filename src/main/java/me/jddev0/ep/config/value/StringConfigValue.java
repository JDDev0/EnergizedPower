package me.jddev0.ep.config.value;

import me.jddev0.ep.config.ConfigValidationException;
import me.jddev0.ep.config.ConfigValue;
import me.jddev0.ep.config.validation.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StringConfigValue extends ConfigValue<String> {
    private final int minLen;
    private final boolean minLenCheckEnabled;

    private final int maxLen;
    private final boolean maxLenCheckEnabled;

    public StringConfigValue(@NotNull String key, @NotNull String defaultValue) {
        this(key, null, defaultValue);
    }

    public StringConfigValue(@NotNull String key, @Nullable String comment, @NotNull String defaultValue) {
        this(key, comment, defaultValue, null);
    }

    public StringConfigValue(@NotNull String key, @Nullable String comment, @NotNull String defaultValue,
                             @Nullable ValueValidator<? super String> customValidator) {
        this(key, comment, defaultValue, customValidator, null, null);
    }

    public StringConfigValue(@NotNull String key, @Nullable String comment, @NotNull String defaultValue,
                             @Nullable Integer minLen, @Nullable Integer maxLen) {
        this(key, comment, defaultValue, null, minLen, maxLen);
    }

    public StringConfigValue(@NotNull String key, @Nullable String comment, @NotNull String defaultValue,
                             @Nullable ValueValidator<? super String> customValidator,
                             @Nullable Integer minLen, @Nullable Integer maxLen) {
        super(key, comment, defaultValue, customValidator);

        minLenCheckEnabled = minLen != null;
        this.minLen = minLenCheckEnabled?minLen:0;

        maxLenCheckEnabled = maxLen != null;
        this.maxLen = maxLenCheckEnabled?maxLen:0;
    }

    @Override
    public @NotNull List<String> getValidationCommentLines() {
        List<String> commentLines = new ArrayList<>();

        if(minLenCheckEnabled)
            commentLines.add("The value must have at least " + minLen + " characters");

        if(maxLenCheckEnabled)
            commentLines.add("The value must have at most " + maxLen + " characters");

        commentLines.addAll(super.getValidationCommentLines());

        return commentLines;
    }

    @Override
    public void validate(@NotNull String value) throws ConfigValidationException {
        if(minLenCheckEnabled && value.length() < minLen)
            throw new ConfigValidationException("The value must have at least " + minLen + " characters");

        if(maxLenCheckEnabled && value.length() > maxLen)
            throw new ConfigValidationException("The value must have at most " + maxLen + " characters");

        super.validate(value);
    }

    @Override
    protected String readInternal(@NotNull String rawValue) {
        return rawValue;
    }

    @Override
    protected String writeInternal(@NotNull String value) {
        return value;
    }
}
