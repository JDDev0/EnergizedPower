package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FluidFillerBlockEntity;
import me.jddev0.ep.inventory.ItemCapabilityMenuHelper;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FluidFillerMenu extends UpgradableEnergyStorageMenu<FluidFillerBlockEntity>
        implements IConfigurableMenu {
    private final ContainerData data;

    public FluidFillerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), new SimpleContainerData(6));
    }

    public FluidFillerMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                           ContainerData data) {
        super(
                EPMenuTypes.FLUID_FILLER_MENU.get(), id,

                inv, blockEntity,
                EPBlocks.FLUID_FILLER.get(),

                upgradeModuleInventory, 2
        );

        checkContainerDataCount(data, 6);
        this.data = data;

        ItemCapabilityMenuHelper.getCapabilityItemHandler(this.level, this.blockEntity).ifPresent(itemHandler -> {
            addSlot(new SlotItemHandler(itemHandler, 0, 80, 35) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
        });

        for(int i = 0;i < upgradeModuleInventory.getContainerSize();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 71 + i * 18, 35, this::isInUpgradeModuleView));

        addDataSlots(this.data);
    }

    public FluidStack getFluid() {
        return blockEntity.getFluid(0);
    }

    public int getTankCapacity() {
        return blockEntity.getTankCapacity(0);
    }

    public int getFluidIndicatorBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1));
    }

    public int getFluidIndicatorPendingBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(2), (short)data.get(3));
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(4));
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return ComparatorMode.fromIndex(data.get(5));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getItem();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory, Merge into tile inventory
            if(!moveItemStackTo(sourceItem, 4 * 9 + 1, 4 * 9 + 1 + 2, false) &&
                    !moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1 + 2) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceItem);

        return sourceItemCopy;
    }
}
