package me.jddev0.ep.block.entity.base;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class ItemStorageBlockEntity<I extends Storage<ItemVariant>>
        extends BlockEntity {
    protected final int slotCount;
    protected final I itemHandler;

    public ItemStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                  int slotCount) {
        super(type, blockPos, blockState);

        this.slotCount = slotCount;
        itemHandler = initInventoryStorage();
    }

    protected abstract I initInventoryStorage();
    protected abstract NbtCompound writeInventoryStorage(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries);
    protected abstract void readInventoryStorage(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries);

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        nbt.put("inventory", writeInventoryStorage(new NbtCompound(), registries));
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        readInventoryStorage(nbt.getCompound("inventory"), registries);
    }

    public abstract void drops(World level, BlockPos worldPosition);
}
