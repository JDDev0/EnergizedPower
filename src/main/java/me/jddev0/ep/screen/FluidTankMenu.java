package me.jddev0.ep.screen;

import me.jddev0.ep.block.entity.FluidTankBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.inventory.data.SimpleBooleanValueContainerData;
import me.jddev0.ep.machine.tier.FluidTankTier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidTankMenu extends ScreenHandler {
    private final FluidTankBlockEntity blockEntity;
    private final World level;

    private final SimpleBooleanValueContainerData ignoreNBTData = new SimpleBooleanValueContainerData();

    public FluidTankMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv, inv.player.getEntityWorld().getBlockEntity(pos), null);
    }

    public FluidTankMenu(int id, PlayerInventory inv, BlockEntity blockEntity, PropertyDelegate data) {
        super(((FluidTankBlockEntity)blockEntity).getTier().getMenuTypeFromTier(), id);

        this.blockEntity = (FluidTankBlockEntity)blockEntity;

        this.level = inv.player.getEntityWorld();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        if(data == null) {
            addProperties(ignoreNBTData);
        }else {
            addProperties(data);
        }
    }

    public FluidTankTier getTier() {
        return blockEntity.getTier();
    }

    public FluidStack getFluid(int tank) {
        return blockEntity.getFluid(tank);
    }

    public long getTankCapacity(int tank) {
        return blockEntity.getTankCapacity(tank);
    }

    public boolean isIgnoreNBT() {
        return ignoreNBTData.getValue();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, getTier().getBlockFromTier());
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
