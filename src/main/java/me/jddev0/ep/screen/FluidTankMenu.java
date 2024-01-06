package me.jddev0.ep.screen;

import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class FluidTankMenu extends AbstractContainerMenu {
    private final FluidTankBlockEntity blockEntity;
    private final Level level;

    public static MenuType<FluidTankMenu> getMenuTypeFromTier(FluidTankBlock.Tier tier) {
        return switch(tier) {
            case SMALL -> ModMenuTypes.FLUID_TANK_SMALL.get();
        };
    }

    public FluidTankMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public FluidTankMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(getMenuTypeFromTier(((FluidTankBlockEntity)blockEntity).getTier()), id);

        this.blockEntity = (FluidTankBlockEntity)blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    public FluidTankBlock.Tier getTier() {
        return blockEntity.getTier();
    }

    public FluidStack getFluid() {
        return blockEntity.getFluid(0);
    }

    public int getTankCapacity() {
        return blockEntity.getTankCapacity(0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, FluidTankBlock.getBlockFromTier(getTier()));
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
