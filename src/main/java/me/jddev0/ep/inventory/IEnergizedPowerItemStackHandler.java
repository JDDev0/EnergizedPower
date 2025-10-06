package me.jddev0.ep.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public interface IEnergizedPowerItemStackHandler extends ResourceHandler<ItemResource> {
    void serialize(ValueOutput view);
    void deserialize(ValueInput view);

    default ItemStack getStackInSlot(int slot) {
        return getResource(slot).toStack(getAmountAsInt(slot));
    }
    void setStackInSlot(int slot, ItemStack itemStack);

    void set(int index, ItemResource resource, int amount);

    ItemStack extractItem(int slot, int amount);
}
