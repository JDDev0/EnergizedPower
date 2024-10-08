package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.ThermalGeneratorBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageProducerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class ThermalGeneratorMenu extends UpgradableEnergyStorageMenu<ThermalGeneratorBlockEntity>
        implements IEnergyStorageProducerIndicatorBarMenu, IConfigurableMenu {
    private final PropertyDelegate data;

    public ThermalGeneratorMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv, new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), new ArrayPropertyDelegate(6));
    }

    public ThermalGeneratorMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory,
                                UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(
                EPMenuTypes.THERMAL_GENERATOR_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.THERMAL_GENERATOR,

                upgradeModuleInventory, 1
        );

        checkDataCount(data, 6);
        this.data = data;

        addSlot(new UpgradeModuleSlot(upgradeModuleInventory, 0, 80, 35, this::isInUpgradeModuleView));

        addProperties(this.data);
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1), (short)data.get(2), (short)data.get(3));
    }

    public FluidStack getFluid() {
        return blockEntity.getFluid(0);
    }

    public long getTankCapacity() {
        return blockEntity.getTankCapacity(0);
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
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory
            if(!insertItem(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
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
}
