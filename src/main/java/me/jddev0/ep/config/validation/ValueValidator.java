package me.jddev0.ep.config.validation;

import me.jddev0.ep.config.ConfigValidationException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ValueValidator<T> {
    void validate(@NotNull T value) throws ConfigValidationException;

    /**
     * @param validator The validator to be executed before this validator
     * @return The combined validator
     */
    default @NotNull ValueValidator<? super T> validateBefore(@NotNull ValueValidator<? super T> validator) {
        return value -> {
            validator.validate(value);

            validate(value);
        };
    }

    /**
     * @param validator The validator to be executed after this validator
     * @return The combined validator
     */
    default @NotNull ValueValidator<? super T> validateAfter(@NotNull ValueValidator<? super T> validator) {
        return value -> {
            validate(value);

            validator.validate(value);
        };
    }
}
