package me.jddev0.ep.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TrackedSimpleContainer extends SimpleContainer {
    private final List<@NotNull ContainerListener> listeners = new ArrayList<>();

    public TrackedSimpleContainer(int size) {
        super(size);
    }

    public TrackedSimpleContainer(ItemStack... itemstacks) {
        super(itemstacks);
    }

    public void addListener(@NotNull ContainerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(@NotNull ContainerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public final void setChanged() {
        super.setChanged();

        setChangedInternal();

        for(ContainerListener listener:listeners) {
            listener.onUpdate(this);
        }
    }

    protected void setChangedInternal() {}
}
