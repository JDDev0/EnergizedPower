package me.jddev0.ep.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ResourceHandlerSlot extends Slot {
    private static final Container EMPTY_INVENTORY = new SimpleContainer(0);

    private final IEnergizedPowerItemStackHandler handler;
    private final ItemSlotSetter slotSetter;
    private ItemStack cachedStack = null;

    public ResourceHandlerSlot(IEnergizedPowerItemStackHandler handler, ItemSlotSetter slotSetter, int index, int x, int y) {
        super(EMPTY_INVENTORY, index, x, y);

        this.handler = handler;
        this.slotSetter = slotSetter;
    }

    @Override
    protected void onQuickCraft(ItemStack itemStack, int amount) {}

    @Override
    public boolean mayPlace(ItemStack stack) {
        if(stack.isEmpty())
            return false;

        return handler.isValid(this.getContainerSlot(), ItemVariant.of(stack));
    }

    private ItemStack getHandlerStack() {
        return handler.getStackInSlot(this.getContainerSlot());
    }

    private void setHandlerStack(ItemStack stack) {
        slotSetter.set(this.getContainerSlot(), ItemVariant.of(stack), stack.getCount());
    }

    @Override
    public final ItemStack getItem() {
        cachedStack = getHandlerStack();

        return cachedStack;
    }

    @Override
    public final void set(ItemStack stack) {
        setHandlerStack(stack);

        cachedStack = stack;
    }

    @Override
    public void setChanged() {
        if(cachedStack != null && !ItemStack.matches(cachedStack, getHandlerStack())) {
            set(cachedStack);
        }
    }

    @Override
    public int getMaxStackSize() {
        return (int)handler.getCapacity(this.getContainerSlot(), ItemVariant.blank());
    }

    @Override
    public ItemStack remove(int amount) {
        ItemStack stack = getHandlerStack().copy();
        ItemStack ret = stack.split(amount);

        set(stack);

        cachedStack = null;

        return ret;
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemVariant resource = handler.getSlot(this.getContainerSlot()).getResource();
        if(resource.isBlank())
            return false;

        try(Transaction transaction = Transaction.openOuter()) {
            return handler.getSlot(this.getContainerSlot()).extract(resource, 1, transaction) == 1;
        }
    }

    public IEnergizedPowerItemStackHandler getItemHandler() {
        return handler;
    }
}
