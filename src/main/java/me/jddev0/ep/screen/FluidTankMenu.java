package me.jddev0.ep.screen;

import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.screen.base.AbstractEnergizedPowerScreenHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class FluidTankMenu extends AbstractEnergizedPowerScreenHandler {
    private final FluidTankBlockEntity blockEntity;
    private final World level;
    private final PropertyDelegate data;

    public static ScreenHandlerType<FluidTankMenu> getMenuTypeFromTier(FluidTankBlock.Tier tier) {
        return switch(tier) {
            case SMALL -> EPMenuTypes.FLUID_TANK_SMALL;
            case MEDIUM -> EPMenuTypes.FLUID_TANK_MEDIUM;
            case LARGE -> EPMenuTypes.FLUID_TANK_LARGE;
        };
    }

    public FluidTankMenu(int id, PlayerInventory inv, PacketByteBuf buffer) {
        this(id, inv, inv.player.getWorld().getBlockEntity(buffer.readBlockPos()), new ArrayPropertyDelegate(1));
    }

    public FluidTankMenu(int id, PlayerInventory inv, BlockEntity blockEntity, PropertyDelegate data) {
        super(getMenuTypeFromTier(((FluidTankBlockEntity)blockEntity).getTier()), id);

        this.blockEntity = (FluidTankBlockEntity)blockEntity;

        checkDataCount(data, 1);
        this.level = inv.player.getWorld();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addProperties(this.data);
    }

    public FluidTankBlock.Tier getTier() {
        return blockEntity.getTier();
    }

    public FluidStack getFluid(int tank) {
        return blockEntity.getFluid(tank);
    }

    public long getTankCapacity(int tank) {
        return blockEntity.getTankCapacity(tank);
    }

    public boolean isIgnoreNBT() {
        return data.get(0) != 0;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, FluidTankBlock.getBlockFromTier(getTier()));
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for(int i = 0;i < 3;i++) {
            for(int j = 0;j < 9;j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for(int i = 0;i < 9;i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
