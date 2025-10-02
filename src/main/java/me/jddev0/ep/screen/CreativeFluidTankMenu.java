package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.CreativeFluidTankBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CreativeFluidTankMenu extends ScreenHandler {
    private final CreativeFluidTankBlockEntity blockEntity;
    private final World level;

    public CreativeFluidTankMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv, inv.player.getEntityWorld().getBlockEntity(pos));
    }

    public CreativeFluidTankMenu(int id, PlayerInventory inv, BlockEntity blockEntity) {
        super(EPMenuTypes.CREATIVE_FLUID_TANK, id);

        this.blockEntity = (CreativeFluidTankBlockEntity)blockEntity;
        this.level = inv.player.getEntityWorld();

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
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, EPBlocks.CREATIVE_FLUID_TANK);
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
