package me.jddev0.ep.config.value;

import me.jddev0.ep.config.ConfigValidationException;
import me.jddev0.ep.config.ConfigValue;
import me.jddev0.ep.config.validation.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IntegerConfigValue extends ConfigValue<Integer> {
    private final int minInclusive;
    private final boolean minLenCheckEnabled;

    private final int maxInclusive;
    private final boolean maxLenCheckEnabled;

    public IntegerConfigValue(@NotNull String key, @NotNull Integer defaultValue) {
        this(key, null, defaultValue);
    }

    public IntegerConfigValue(@NotNull String key, @Nullable String comment, @NotNull Integer defaultValue) {
        this(key, comment, defaultValue, null);
    }

    public IntegerConfigValue(@NotNull String key, @Nullable String comment, @NotNull Integer defaultValue,
                              @Nullable ValueValidator<? super Integer> customValidator) {
        this(key, comment, defaultValue, customValidator, null, null);
    }

    public IntegerConfigValue(@NotNull String key, @Nullable String comment, @NotNull Integer defaultValue,
                              @Nullable Integer minInclusive, @Nullable Integer maxInclusive) {
        this(key, comment, defaultValue, null, minInclusive, maxInclusive);
    }

    public IntegerConfigValue(@NotNull String key, @Nullable String comment, @NotNull Integer defaultValue,
                              @Nullable ValueValidator<? super Integer> customValidator,
                              @Nullable Integer minInclusive, @Nullable Integer maxInclusive) {
        super(key, comment, defaultValue, customValidator);

        minLenCheckEnabled = minInclusive != null;
        this.minInclusive = minLenCheckEnabled?minInclusive:0;

        maxLenCheckEnabled = maxInclusive != null;
        this.maxInclusive = maxLenCheckEnabled?maxInclusive:0;
    }

    @Override
    public @NotNull List<String> getValidationCommentLines() {
        List<String> commentLines = new ArrayList<>();

        if(minLenCheckEnabled)
            commentLines.add("Value >= " + minInclusive);

        if(maxLenCheckEnabled)
            commentLines.add("Value <= " + maxInclusive);

        commentLines.addAll(super.getValidationCommentLines());

        return commentLines;
    }

    @Override
    public void validate(@NotNull Integer value) throws ConfigValidationException {
        if(minLenCheckEnabled && value < minInclusive)
            throw new ConfigValidationException("The value must be at least " + minInclusive);

        if(maxLenCheckEnabled && value > maxInclusive)
            throw new ConfigValidationException("The value must be at most " + maxInclusive);

        super.validate(value);
    }

    @Override
    protected @NotNull Integer readInternal(@NotNull String rawValue) throws ConfigValidationException {
        try {
            return Integer.parseInt(rawValue);
        }catch(NumberFormatException e) {
            throw new ConfigValidationException("Invalid int value: " + e.getMessage());
        }
    }

    @Override
    protected @NotNull String writeInternal(@NotNull Integer value) {
        return value + "";
    }
}
