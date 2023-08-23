package me.jddev0.ep.config.value;

import me.jddev0.ep.config.ConfigValidationException;
import me.jddev0.ep.config.ConfigValue;
import me.jddev0.ep.config.validation.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanConfigValue extends ConfigValue<Boolean> {
    public BooleanConfigValue(@NotNull String key, @NotNull Boolean defaultValue) {
        this(key, null, defaultValue);
    }

    public BooleanConfigValue(@NotNull String key, @Nullable String comment, @NotNull Boolean defaultValue) {
        this(key, comment, defaultValue, null);
    }

    public BooleanConfigValue(@NotNull String key, @Nullable String comment, @NotNull Boolean defaultValue,
                              @Nullable ValueValidator<? super Boolean> customValidator) {
        super(key, comment, defaultValue, customValidator);
    }

    @Override
    protected @NotNull Boolean readInternal(@NotNull String rawValue) throws ConfigValidationException {
        if(rawValue.equalsIgnoreCase("false"))
            return false;

        if(rawValue.equalsIgnoreCase("true"))
            return true;

        throw new ConfigValidationException("Invalid boolean value: Must be \"false\" or \"true\" (Case insensitive)");
    }

    @Override
    protected @NotNull String writeInternal(@NotNull Boolean value) {
        return value?"true":"false";
    }
}
