package me.jddev0.ep.config.value;

import me.jddev0.ep.config.ConfigValidationException;
import me.jddev0.ep.config.ConfigValue;
import me.jddev0.ep.config.validation.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DoubleConfigValue extends ConfigValue<Double> {
    private final double minExclusive;
    private final boolean minLenCheckEnabled;

    private final double maxExclusive;
    private final boolean maxLenCheckEnabled;

    public DoubleConfigValue(@NotNull String key, @NotNull Double defaultValue) {
        this(key, null, defaultValue);
    }

    public DoubleConfigValue(@NotNull String key, @Nullable String comment, @NotNull Double defaultValue) {
        this(key, comment, defaultValue, null);
    }

    public DoubleConfigValue(@NotNull String key, @Nullable String comment, @NotNull Double defaultValue,
                             @Nullable ValueValidator<? super Double> customValidator) {
        this(key, comment, defaultValue, customValidator, null, null);
    }

    public DoubleConfigValue(@NotNull String key, @Nullable String comment, @NotNull Double defaultValue,
                             @Nullable Double minExclusive, @Nullable Double maxExclusive) {
        this(key, comment, defaultValue, null, minExclusive, maxExclusive);
    }

    public DoubleConfigValue(@NotNull String key, @Nullable String comment, @NotNull Double defaultValue,
                             @Nullable ValueValidator<? super Double> customValidator,
                             @Nullable Double minExclusive, @Nullable Double maxExclusive) {
        super(key, comment, defaultValue, customValidator);

        minLenCheckEnabled = minExclusive != null;
        this.minExclusive = minLenCheckEnabled?minExclusive:0;

        maxLenCheckEnabled = maxExclusive != null;
        this.maxExclusive = maxLenCheckEnabled?maxExclusive:0;
    }

    @Override
    public @NotNull List<String> getValidationCommentLines() {
        List<String> commentLines = new ArrayList<>();

        if(minLenCheckEnabled)
            commentLines.add("Value > " + minExclusive);

        if(maxLenCheckEnabled)
            commentLines.add("Value < " + maxExclusive);

        commentLines.addAll(super.getValidationCommentLines());

        return commentLines;
    }

    @Override
    public void validate(@NotNull Double value) throws ConfigValidationException {
        if(minLenCheckEnabled && !(value > minExclusive))
            throw new ConfigValidationException("The value must be greater than " + minExclusive);

        if(maxLenCheckEnabled && !(value < maxExclusive))
            throw new ConfigValidationException("The value must be less than " + maxExclusive);

        super.validate(value);
    }

    @Override
    protected Double readInternal(@NotNull String rawValue) throws ConfigValidationException {
        try {
            return Double.parseDouble(rawValue);
        }catch(NumberFormatException e) {
            throw new ConfigValidationException("Invalid double value: " + e.getMessage());
        }
    }

    @Override
    protected String writeInternal(@NotNull Double value) {
        return String.format(Locale.ENGLISH, "%f", value);
    }
}
