package me.jddev0.ep.machine.configuration;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IConfigurableIOMachine {
    @NotNull
    SlotType @NotNull [] getSupportedSlotTypes();

    List<SlotGroup> getSlotGroups(@NotNull SlotType slotType);
    IOConfiguration getIOConfiguration(@NotNull SlotType slotType);
}
