package me.jddev0.ep.config.value;

import me.jddev0.ep.config.ConfigValidationException;
import me.jddev0.ep.config.ConfigValue;
import me.jddev0.ep.config.validation.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class EnumConfigValue<T extends Enum<T>> extends ConfigValue<T> {
    private final ArrayList<T> ENUM_VALUES;
    private final Function<String, T> VALUE_OF_FUNCTION;

    public EnumConfigValue(@NotNull String key, @NotNull T defaultValue, @NotNull T @NotNull [] enumValues,
                           @NotNull Function<String, T> valueOfFunction) {
        this(key, null, defaultValue, enumValues, valueOfFunction);
    }

    public EnumConfigValue(@NotNull String key, @Nullable String comment, @NotNull T defaultValue,
                           @NotNull T @NotNull [] enumValues, @NotNull Function<String, T> valueOfFunction) {
        this(key, comment, defaultValue, null, enumValues, valueOfFunction);
    }

    public EnumConfigValue(@NotNull String key, @Nullable String comment, @NotNull T defaultValue,
                           @Nullable ValueValidator<T> customValidator, @NotNull T @NotNull [] enumValues,
                           @NotNull Function<String, T> valueOfFunction) {
        super(key, comment, defaultValue, customValidator);

        ENUM_VALUES = new ArrayList<>(Arrays.asList(enumValues));
        VALUE_OF_FUNCTION = valueOfFunction;
    }

    @Override
    public @NotNull List<String> getValidationCommentLines() {
        List<String> commentLines = new ArrayList<>();

        commentLines.add("Value must be on of " + ENUM_VALUES);

        commentLines.addAll(super.getValidationCommentLines());

        return commentLines;
    }

    @Override
    protected @NotNull T readInternal(@NotNull String rawValue) throws ConfigValidationException {
        if(ENUM_VALUES.stream().noneMatch(enumValue -> enumValue.name().equals(rawValue)))
            throw new ConfigValidationException("Value must be on of " + ENUM_VALUES);

        try {
            return VALUE_OF_FUNCTION.apply(rawValue);
        }catch(IllegalArgumentException e) {
            throw new ConfigValidationException("Invalid value: " + e.getMessage());
        }
    }

    @Override
    protected @NotNull String writeInternal(@NotNull T value) {
        return value + "";
    }
}
