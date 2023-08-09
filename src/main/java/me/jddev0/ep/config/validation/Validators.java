package me.jddev0.ep.config.validation;

import me.jddev0.ep.config.ConfigValidationException;

public final class Validators {
    public static final ValueValidator<String> STRING_NOT_EMPTY = value -> {
        if(value.isEmpty())
            throw new ConfigValidationException("Value must not be empty");
    };
    public static final ValueValidator<String> STRING_NOT_BLANK = value -> {
        if(value.isBlank())
            throw new ConfigValidationException("Value must not be blank");
    };

    private Validators() {}
}
