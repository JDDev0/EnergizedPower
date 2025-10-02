package me.jddev0.ep.screen.base;

import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EnergyStorageMenu<T extends EnergyStorageBlockEntity<?>>
        extends ScreenHandler
        implements IEnergyStorageMenu {
    protected final T blockEntity;
    protected final World level;
    protected final Block blockType;

    protected EnergyStorageMenu(@Nullable ScreenHandlerType<?> menuType, int id, PlayerInventory playerInventory,
                                BlockEntity blockEntity, Block blockType) {
        this(menuType, id, playerInventory, blockEntity, blockType, 8, 84);
    }

    @SuppressWarnings("unchecked")
    protected EnergyStorageMenu(@Nullable ScreenHandlerType<?> menuType, int id, PlayerInventory playerInventory,
                                BlockEntity blockEntity, Block blockType,
                                int playerInventoryX, int playerInventoryY) {
        super(menuType, id);

        this.blockEntity = (T)blockEntity;
        this.level = playerInventory.player.getEntityWorld();
        this.blockType = blockType;

        addPlayerInventorySlots(playerInventory, playerInventoryX, playerInventoryY);
    }

    @Override
    public long getEnergy() {
        return blockEntity.getEnergy();
    }

    @Override
    public long getCapacity() {
        return blockEntity.getCapacity();
    }

    @Override
    public boolean canUse(@NotNull PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, blockType);
    }

    private void addPlayerInventorySlots(PlayerInventory playerInventory, int x, int y) {
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
