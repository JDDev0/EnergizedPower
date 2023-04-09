package me.jddev0.ep.block.entity.handler;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CachedSidedInventoryStorage<T extends BlockEntity> implements Function<Direction, @Nullable Storage<ItemVariant>> {
    private final InventoryStorage[] cachedInventoryStorages = new InventoryStorage[Direction.values().length];
    private final SidedInventory sidedInventory;

    public CachedSidedInventoryStorage(SidedInventory sidedInventory) {
        this.sidedInventory = sidedInventory;
    }

    @Override
    public @Nullable Storage<ItemVariant> apply(Direction side) {
        if(side == null)
            return null;

        int index = side.ordinal();

        if(cachedInventoryStorages[index] == null)
            cachedInventoryStorages[index] = InventoryStorage.of(sidedInventory, side);

        return cachedInventoryStorages[index];
    }
}
