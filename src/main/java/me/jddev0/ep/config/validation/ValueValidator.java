package me.jddev0.ep.config.validation;

import me.jddev0.ep.config.ConfigValidationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface ValueValidator<T> {
    void validate(@NotNull T value) throws ConfigValidationException;

    default @NotNull List<String> getValidationCommentLines() {
        return new ArrayList<>();
    }

    /**
     * @param validator The validator to be executed before this validator
     * @return The combined validator
     */
    default @NotNull ValueValidator<? super T> validateBefore(@NotNull ValueValidator<? super T> validator) {
        return new ValueValidator<>() {
            @Override
            public void validate(@NotNull T value) throws ConfigValidationException {
                validator.validate(value);

                ValueValidator.this.validate(value);
            }

            @Override
            public @NotNull List<String> getValidationCommentLines() {
                List<String> commentLines = new ArrayList<>(validator.getValidationCommentLines());
                commentLines.addAll(ValueValidator.this.getValidationCommentLines());

                return commentLines;
            }
        };
    }

    /**
     * @param validator The validator to be executed after this validator
     * @return The combined validator
     */
    default @NotNull ValueValidator<? super T> validateAfter(@NotNull ValueValidator<? super T> validator) {
        return new ValueValidator<>() {
            @Override
            public void validate(@NotNull T value) throws ConfigValidationException {
                ValueValidator.this.validate(value);

                validator.validate(value);
            }

            @Override
            public @NotNull List<String> getValidationCommentLines() {
                List<String> commentLines = new ArrayList<>(ValueValidator.this.getValidationCommentLines());
                commentLines.addAll(validator.getValidationCommentLines());

                return commentLines;
            }
        };
    }
}
