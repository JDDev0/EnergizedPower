package me.jddev0.ep.config.value;

import me.jddev0.ep.config.ConfigValidationException;
import me.jddev0.ep.config.ConfigValue;
import me.jddev0.ep.config.validation.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FloatConfigValue extends ConfigValue<Float> {
    private final float minExclusive;
    private final boolean minLenCheckEnabled;

    private final float maxExclusive;
    private final boolean maxLenCheckEnabled;

    public FloatConfigValue(@NotNull String key, @NotNull Float defaultValue) {
        this(key, null, defaultValue);
    }

    public FloatConfigValue(@NotNull String key, @Nullable String comment, @NotNull Float defaultValue) {
        this(key, comment, defaultValue, null);
    }

    public FloatConfigValue(@NotNull String key, @Nullable String comment, @NotNull Float defaultValue,
                            @Nullable ValueValidator<? super Float> customValidator) {
        this(key, comment, defaultValue, customValidator, null, null);
    }

    public FloatConfigValue(@NotNull String key, @Nullable String comment, @NotNull Float defaultValue,
                            @Nullable Float minExclusive, @Nullable Float maxExclusive) {
        this(key, comment, defaultValue, null, minExclusive, maxExclusive);
    }

    public FloatConfigValue(@NotNull String key, @Nullable String comment, @NotNull Float defaultValue,
                            @Nullable ValueValidator<? super Float> customValidator,
                           @Nullable Float minExclusive, @Nullable Float maxExclusive) {
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
    public void validate(@NotNull Float value) throws ConfigValidationException {
        if(minLenCheckEnabled && !(value > minExclusive))
            throw new ConfigValidationException("The value must be greater than " + minExclusive);

        if(maxLenCheckEnabled && !(value < maxExclusive))
            throw new ConfigValidationException("The value must be less than " + maxExclusive);

        super.validate(value);
    }

    @Override
    protected @NotNull Float readInternal(@NotNull String rawValue) throws ConfigValidationException {
        try {
            return Float.parseFloat(rawValue);
        }catch(NumberFormatException e) {
            throw new ConfigValidationException("Invalid float value: " + e.getMessage());
        }
    }

    @Override
    protected @NotNull String writeInternal(@NotNull Float value) {
        return String.format(Locale.ENGLISH, "%f", value);
    }
}
