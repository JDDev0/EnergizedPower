package me.jddev0.ep.screen.base;

import me.jddev0.ep.machine.configuration.IOConfiguration;
import me.jddev0.ep.machine.configuration.SlotGroup;
import me.jddev0.ep.machine.configuration.SlotType;

import java.util.List;

public interface IConfigurableIOMenu {
    default boolean isInIOConfigurationView() {
        return false;
    }

    default SlotType getSlotType() {
        return SlotType.ITEM;
    }

    default List<SlotGroup> getSlotGroups() {
        return List.of();
    }

    default IOConfiguration getIOConfiguration() {
        return new IOConfiguration();
    }
}
