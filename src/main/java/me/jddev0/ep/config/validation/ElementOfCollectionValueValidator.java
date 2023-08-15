package me.jddev0.ep.config.validation;

import me.jddev0.ep.config.ConfigValidationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ElementOfCollectionValueValidator<T> implements ValueValidator<T> {
    private final List<T> elements;

    public ElementOfCollectionValueValidator(Collection<T> elements) {
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public void validate(@NotNull T value) throws ConfigValidationException {
        if(!elements.contains(value))
            throw new ConfigValidationException("Value must be one of " + elements);
    }

    @Override
    public @NotNull List<String> getValidationCommentLines() {
        return List.of("Value must be one of " + elements);
    }
}
