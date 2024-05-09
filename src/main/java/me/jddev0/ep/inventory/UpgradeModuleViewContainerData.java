package me.jddev0.ep.inventory;

import net.minecraft.screen.PropertyDelegate;

public class UpgradeModuleViewContainerData implements PropertyDelegate {
    private boolean inUpgradeModuleView = false;

    @Override
    public int get(int index) {
        if(index == 0)
            return inUpgradeModuleView?1:0;

        return 0;
    }

    @Override
    public void set(int index, int value) {
        if(index == 0)
            inUpgradeModuleView = value != 0;
    }

    public boolean isInUpgradeModuleView() {
        return inUpgradeModuleView;
    }

    public void toggleInUpgradeModuleView() {
        inUpgradeModuleView = !inUpgradeModuleView;
    }

    @Override
    public int size() {
        return 1;
    }
}
