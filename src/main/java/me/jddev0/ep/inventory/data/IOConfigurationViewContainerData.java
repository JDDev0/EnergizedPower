package me.jddev0.ep.inventory.data;

import me.jddev0.ep.machine.configuration.SlotType;
import net.minecraft.world.inventory.ContainerData;

public class IOConfigurationViewContainerData implements ContainerData {
    private boolean inIOConfigurationView = false;
    private SlotType slotType = SlotType.ITEM;

    @Override
    public int get(int index) {
        if(index == 0)
            return inIOConfigurationView?1:0;
        else if(index == 1)
            return slotType.ordinal();

        return 0;
    }

    @Override
    public void set(int index, int value) {
        if(index == 0)
            inIOConfigurationView = value != 0;
        else if(index == 1)
            slotType = SlotType.fromIndex(value);
    }

    public boolean isInIOConfigurationView() {
        return inIOConfigurationView;
    }

    public void toggleInIOConfigurationView() {
        inIOConfigurationView = !inIOConfigurationView;
    }

    public void setInIOConfigurationView(boolean inIOConfigurationView) {
        this.inIOConfigurationView = inIOConfigurationView;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(SlotType slotType) {
        this.slotType = slotType;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
