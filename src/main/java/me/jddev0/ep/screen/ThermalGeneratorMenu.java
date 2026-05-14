package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.ThermalGeneratorBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.data.SimpleComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.SimpleEnergyValueContainerData;
import me.jddev0.ep.inventory.data.SimpleRedstoneModeValueContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageProducerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ThermalGeneratorMenu extends UpgradableEnergyStorageMenu<ThermalGeneratorBlockEntity>
        implements IEnergyStorageProducerIndicatorBarMenu, IConfigurableMenu {
    private final SimpleEnergyValueContainerData energyProductionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData energyProductionLeftData = new SimpleEnergyValueContainerData();
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public ThermalGeneratorMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv.player.level().getBlockEntity(pos), inv, new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), null);
    }

    public ThermalGeneratorMenu(int id, BlockEntity blockEntity, Inventory playerInventory,
                                UpgradeModuleInventory upgradeModuleInventory, ContainerData data) {
        super(
                EPMenuTypes.THERMAL_GENERATOR_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.THERMAL_GENERATOR,

                upgradeModuleInventory, 1
        );

        addSlot(new UpgradeModuleSlot(upgradeModuleInventory, 0, 80, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addDataSlots(energyProductionPerTickData);
            addDataSlots(energyProductionLeftData);
            addDataSlots(redstoneModeData);
            addDataSlots(comparatorModeData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        return energyProductionLeftData.getValue();
    }

    @Override
    public long getEnergyPerTickBarValue() {
        return energyProductionPerTickData.getValue();
    }

    public FluidStack getFluid() {
        return blockEntity.getFluid(0);
    }

    public long getTankCapacity() {
        return blockEntity.getTankCapacity(0);
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneModeData.getValue();
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return comparatorModeData.getValue();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getItem();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory
            if(!moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.setByPlayer(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceItem);

        return sourceItemCopy;
    }
}
