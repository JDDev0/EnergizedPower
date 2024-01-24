package me.jddev0.ep.screen;

import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class FluidTankMenu extends AbstractContainerMenu {
    private final FluidTankBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public static MenuType<FluidTankMenu> getMenuTypeFromTier(FluidTankBlock.Tier tier) {
        return switch(tier) {
            case SMALL -> ModMenuTypes.FLUID_TANK_SMALL.get();
            case MEDIUM -> ModMenuTypes.FLUID_TANK_MEDIUM.get();
            case LARGE -> ModMenuTypes.FLUID_TANK_LARGE.get();
        };
    }

    public FluidTankMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level.getBlockEntity(buffer.readBlockPos()), new SimpleContainerData(1));
    }

    public FluidTankMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(getMenuTypeFromTier(((FluidTankBlockEntity)blockEntity).getTier()), id);

        checkContainerDataCount(data, 1);
        this.blockEntity = (FluidTankBlockEntity)blockEntity;
        this.level = inv.player.level;
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(this.data);
    }

    public FluidTankBlock.Tier getTier() {
        return blockEntity.getTier();
    }

    public FluidStack getFluid(int tank) {
        return blockEntity.getFluid(tank);
    }

    public int getTankCapacity(int tank) {
        return blockEntity.getTankCapacity(tank);
    }

    public boolean isIgnoreNBT() {
        return data.get(0) != 0;
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
