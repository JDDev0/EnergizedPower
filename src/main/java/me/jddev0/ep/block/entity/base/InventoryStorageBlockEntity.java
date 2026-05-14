package me.jddev0.ep.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class InventoryStorageBlockEntity<I extends SimpleContainer>
        extends BlockEntity {
    protected final int slotCount;
    protected final I itemHandler;

    public InventoryStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                       int slotCount) {
        super(type, blockPos, blockState);

        this.slotCount = slotCount;
        itemHandler = initInventoryStorage();
    }

    protected abstract I initInventoryStorage();

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.put("inventory", ContainerHelper.saveAllItems(new CompoundTag(), itemHandler.items, registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        ContainerHelper.loadAllItems(nbt.getCompound("inventory"), itemHandler.items, registries);
    }

    public void drops(Level level, BlockPos worldPosition) {
        Containers.dropContents(level, worldPosition, itemHandler);
    }
}
