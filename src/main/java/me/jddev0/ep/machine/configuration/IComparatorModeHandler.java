package me.jddev0.ep.machine.configuration;

import org.jetbrains.annotations.NotNull;

public interface IComparatorModeHandler {
    @NotNull
    ComparatorMode @NotNull [] getAvailableComparatorModes();

    @NotNull
    ComparatorMode getComparatorMode();

    boolean setComparatorMode(@NotNull ComparatorMode comparatorMode);
}
