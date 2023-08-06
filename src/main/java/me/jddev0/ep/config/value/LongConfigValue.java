package me.jddev0.ep.config.value;

import me.jddev0.ep.config.ConfigValidationException;
import me.jddev0.ep.config.ConfigValue;
import me.jddev0.ep.config.validation.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LongConfigValue extends ConfigValue<Long> {
    private final long minInclusive;
    private final boolean minLenCheckEnabled;

    private final long maxInclusive;
    private final boolean maxLenCheckEnabled;

    public LongConfigValue(@NotNull String key, @NotNull Long defaultValue) {
        this(key, null, defaultValue);
    }

    public LongConfigValue(@NotNull String key, @Nullable String comment, @NotNull Long defaultValue) {
        this(key, comment, defaultValue, null);
    }

    public LongConfigValue(@NotNull String key, @Nullable String comment, @NotNull Long defaultValue,
                           @Nullable ValueValidator<? super Long> customValidator) {
        this(key, comment, defaultValue, customValidator, null, null);
    }

    public LongConfigValue(@NotNull String key, @Nullable String comment, @NotNull Long defaultValue,
                           @Nullable Long minInclusive, @Nullable Long maxInclusive) {
        this(key, comment, defaultValue, null, minInclusive, maxInclusive);
    }

    public LongConfigValue(@NotNull String key, @Nullable String comment, @NotNull Long defaultValue,
                           @Nullable ValueValidator<? super Long> customValidator,
                           @Nullable Long minInclusive, @Nullable Long maxInclusive) {
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
    public void validate(@NotNull Long value) throws ConfigValidationException {
        if(minLenCheckEnabled && value < minInclusive)
            throw new ConfigValidationException("The value must be at least " + minInclusive);

        if(maxLenCheckEnabled && value > maxInclusive)
            throw new ConfigValidationException("The value must be at most " + maxInclusive);

        super.validate(value);
    }

    @Override
    protected Long readInternal(@NotNull String rawValue) throws ConfigValidationException {
        try {
            return Long.parseLong(rawValue);
        }catch(NumberFormatException e) {
            throw new ConfigValidationException("Invalid long value: " + e.getMessage());
        }
    }

    @Override
    protected String writeInternal(@NotNull Long value) {
        return value + "";
    }
}
