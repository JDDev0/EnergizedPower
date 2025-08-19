package me.jddev0.ep.block.entity.base;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
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
    protected abstract NbtCompound writeInventoryStorage(@NotNull NbtCompound nbt);
    protected abstract void readInventoryStorage(@NotNull NbtCompound nbt);

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put("inventory", writeInventoryStorage(new NbtCompound()));
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        readInventoryStorage(nbt.getCompound("inventory"));
    }

    public abstract void drops(World level, BlockPos worldPosition);
}
