package me.jddev0.ep.block.entity;

import me.jddev0.ep.component.MachineConfigurationComponent;
import org.jetbrains.annotations.NotNull;

public interface MachineConfiguratorConfigurable {
    /**
     * @param machineConfiguration The machine configuration to be applied
     * @return true if the configuration was applied successfully
     */
    boolean onApplyMachineConfiguration(@NotNull MachineConfigurationComponent machineConfiguration);

    /**
     * @return the current machine configuration
     */
    @NotNull MachineConfigurationComponent onStoreMachineConfiguration();
}
