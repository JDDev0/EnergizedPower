package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.CreativeFluidTankBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CreativeFluidTankMenu extends AbstractContainerMenu {
    private final CreativeFluidTankBlockEntity blockEntity;
    private final Level level;

    public CreativeFluidTankMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, inv.player.level().getBlockEntity(pos));
    }

    public CreativeFluidTankMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(EPMenuTypes.CREATIVE_FLUID_TANK, id);

        this.blockEntity = (CreativeFluidTankBlockEntity)blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    public FluidStack getFluid(int tank) {
        return blockEntity.getFluid(tank);
    }

    public long getTankCapacity(int tank) {
        return blockEntity.getTankCapacity(tank);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, EPBlocks.CREATIVE_FLUID_TANK);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for(int i = 0;i < 3;i++) {
            for(int j = 0;j < 9;j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for(int i = 0;i < 9;i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
