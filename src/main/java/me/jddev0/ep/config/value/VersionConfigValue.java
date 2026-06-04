package me.jddev0.ep.config.value;

import me.jddev0.ep.config.ConfigValidationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class VersionConfigValue extends StringConfigValue {
    public VersionConfigValue(@NotNull String key, @NotNull String version) {
        super(key, "The config version of the config file", version);
    }

    @Override
    public @NotNull List<String> getValidationCommentLines() {
        List<String> commentLines = new ArrayList<>();

        commentLines.add("This value should not be changed");

        commentLines.addAll(super.getValidationCommentLines());

        return commentLines;
    }

    @Override
    public void validate(@NotNull String value) throws ConfigValidationException {
        if(!value.equals(getDefaultValue())) {
            throw new ConfigValidationException("The config version was loaded with config version \"" + value +
                    "\" but was saved in config version \"" + getDefaultValue() + "\"");
        }

        super.validate(value);
    }
}
