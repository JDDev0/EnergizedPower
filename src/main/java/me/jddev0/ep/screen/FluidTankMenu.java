package me.jddev0.ep.screen;

import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import me.jddev0.ep.inventory.data.SimpleBooleanValueContainerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTankMenu extends AbstractContainerMenu {
    private final FluidTankBlockEntity blockEntity;
    private final Level level;

    private final SimpleBooleanValueContainerData ignoreNBTData = new SimpleBooleanValueContainerData();

    public static MenuType<FluidTankMenu> getMenuTypeFromTier(FluidTankBlock.Tier tier) {
        return switch(tier) {
            case SMALL -> EPMenuTypes.FLUID_TANK_SMALL.get();
            case MEDIUM -> EPMenuTypes.FLUID_TANK_MEDIUM.get();
            case LARGE -> EPMenuTypes.FLUID_TANK_LARGE.get();
        };
    }

    public FluidTankMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public FluidTankMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(getMenuTypeFromTier(((FluidTankBlockEntity)blockEntity).getTier()), id);

        this.blockEntity = (FluidTankBlockEntity)blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        if(data == null) {
            addDataSlots(ignoreNBTData);
        }else {
            addDataSlots(data);
        }
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
        return ignoreNBTData.getValue();
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
