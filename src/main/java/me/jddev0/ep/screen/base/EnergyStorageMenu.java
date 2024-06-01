package me.jddev0.ep.screen.base;

import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EnergyStorageMenu<T extends EnergyStorageBlockEntity<?>>
        extends AbstractContainerMenu
        implements IEnergyStorageMenu {
    protected final T blockEntity;
    protected final Level level;
    protected final Block blockType;

    protected EnergyStorageMenu(@Nullable MenuType<?> menuType, int id, Inventory playerInventory,
                                BlockEntity blockEntity, Block blockType) {
        this(menuType, id, playerInventory, blockEntity, blockType, 8, 84);
    }

    @SuppressWarnings("unchecked")
    protected EnergyStorageMenu(@Nullable MenuType<?> menuType, int id, Inventory playerInventory,
                                BlockEntity blockEntity, Block blockType,
                                int playerInventoryX, int playerInventoryY) {
        super(menuType, id);

        this.blockEntity = (T)blockEntity;
        this.level = playerInventory.player.level;
        this.blockType = blockType;

        addPlayerInventorySlots(playerInventory, playerInventoryX, playerInventoryY);
    }

    @Override
    public int getEnergy() {
        return blockEntity.getEnergy();
    }

    @Override
    public int getCapacity() {
        return blockEntity.getCapacity();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, blockType);
    }

    private void addPlayerInventorySlots(Inventory playerInventory, int x, int y) {
        //Player Inventory
        for(int i = 0;i < 3;i++)
            for(int j = 0;j < 9;j++)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));

        //Player Hotbar
        for(int i = 0;i < 9;i++)
            addSlot(new Slot(playerInventory, i, x + i * 18, y + 58));
    }

    public T getBlockEntity() {
        return blockEntity;
    }
}
