package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidDrainerBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.util.ByteUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class FluidDrainerMenu extends ScreenHandler implements EnergyStorageMenu {
    private final FluidDrainerBlockEntity blockEntity;
    private final Inventory inv;
    private final World level;
    private final PropertyDelegate data;

    public FluidDrainerMenu(int id, PlayerInventory inv, PacketByteBuf buffer) {
        this(id, inv.player.getWorld().getBlockEntity(buffer.readBlockPos()), inv, new SimpleInventory(1) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0)
                    return ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null;

                return super.isValid(slot, stack);
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }
        }, new ArrayPropertyDelegate(9));
    }

    public FluidDrainerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv, PropertyDelegate data) {
        super(ModMenuTypes.FLUID_DRAINER_MENU, id);

        this.blockEntity = (FluidDrainerBlockEntity)blockEntity;

        this.inv = inv;
        checkSize(this.inv, 1);
        checkDataCount(data, 9);
        this.level = playerInventory.player.getWorld();
        this.inv.onOpen(playerInventory.player);
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addSlot(new ConstraintInsertSlot(this.inv, 0, 80, 35));

        addProperties(this.data);
    }

    @Override
    public long getEnergy() {
        return blockEntity.getEnergy();
    }

    @Override
    public long getCapacity() {
        return blockEntity.getCapacity();
    }

    public FluidStack getFluid() {
        return blockEntity.getFluid(0);
    }

    public long getTankCapacity() {
        return blockEntity.getTankCapacity(0);
    }

    public long getFluidIndicatorBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1), (short)data.get(2), (short)data.get(3));
    }

    public long getFluidIndicatorPendingBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(4), (short)data.get(5), (short)data.get(6), (short)data.get(7));
    }

    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(8));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into tile inventory
            //Allow only 1 item
            if(slots.get(4 * 9).hasStack() || !insertItem(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1) {
            //Tile inventory slot -> Merge into player inventory
            if(!insertItem(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.setStack(ItemStack.EMPTY);
        else
            sourceSlot.markDirty();

        sourceSlot.onTakeItem(player, sourceItem);

        return sourceItemCopy;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, ModBlocks.FLUID_DRAINER);
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
