package me.jddev0.ep.screen;

import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageConsumerIndicatorBarMenu;
import net.minecraft.world.Container;

public interface IAutoCrafterMenu extends IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    Container[] getPatternSlots();
}
