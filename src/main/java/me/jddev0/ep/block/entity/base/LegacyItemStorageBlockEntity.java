package me.jddev0.ep.block.entity.base;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class LegacyItemStorageBlockEntity<I extends Storage<ItemVariant>>
        extends BlockEntity {
    protected final int slotCount;
    protected final I itemHandler;

    public LegacyItemStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        int slotCount) {
        super(type, blockPos, blockState);

        this.slotCount = slotCount;
        itemHandler = initInventoryStorage();
    }

    protected abstract I initInventoryStorage();
    protected abstract CompoundTag writeInventoryStorage(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries);
    protected abstract void readInventoryStorage(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries);

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.put("inventory", writeInventoryStorage(new CompoundTag(), registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        readInventoryStorage(nbt.getCompound("inventory"), registries);
    }

    public abstract void drops(Level level, BlockPos worldPosition);
}
