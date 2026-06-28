package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AdvancedChargerBlockEntity;
import me.jddev0.ep.inventory.ItemCapabilityMenuHelper;
import me.jddev0.ep.inventory.ResourceHandlerSlot;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.data.SimpleComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.SimpleEnergyValueContainerData;
import me.jddev0.ep.inventory.data.SimpleRedstoneModeValueContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageConsumerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AdvancedChargerMenu extends UpgradableEnergyStorageMenu<AdvancedChargerBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData[] energyConsumptionLeftData = new SimpleEnergyValueContainerData[] {
            new SimpleEnergyValueContainerData(),
            new SimpleEnergyValueContainerData(),
            new SimpleEnergyValueContainerData()
    };
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public AdvancedChargerMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, inv.player.level().getBlockEntity(pos), new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        ), null);
    }

    public AdvancedChargerMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                               ContainerData data) {
        super(
                EPMenuTypes.ADVANCED_CHARGER_MENU, id,

                inv, blockEntity,
                EPBlocks.ADVANCED_CHARGER,

                upgradeModuleInventory, 3
        );

        ItemCapabilityMenuHelper.getEnergizedPowerItemStackHandlerCapability(this.level, this.blockEntity).ifPresent(itemHandler -> {
            addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 0, 41, 35) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
            addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 1, 89, 35) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
            addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 2, 137, 35) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
        });

        for(int i = 0;i < upgradeModuleInventory.getContainerSize();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 62 + i * 18, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addDataSlots(energyConsumptionPerTickData);
            addDataSlots(energyConsumptionLeftData[0]);
            addDataSlots(energyConsumptionLeftData[1]);
            addDataSlots(energyConsumptionLeftData[2]);
            addDataSlots(redstoneModeData);
            addDataSlots(comparatorModeData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        long energyIndicatorBarValueSum = -1;

        for(SimpleEnergyValueContainerData ele:energyConsumptionLeftData) {
            long value = ele.getValue();

            //Prevent overflow
            if(Math.max(0, energyIndicatorBarValueSum) + Math.max(0, value) < 0)
                return Long.MAX_VALUE;

            if(value > -1) {
                if(energyIndicatorBarValueSum == -1)
                    energyIndicatorBarValueSum++;

                energyIndicatorBarValueSum += value;
            }
        }

        return energyIndicatorBarValueSum;
    }

    @Override
    public long getEnergyPerTickBarValue() {
        return energyConsumptionPerTickData.getValue();
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
            //Player inventory slot -> Merge into upgrade module inventory, Merge into tile inventory
            if(!moveUpgradeModuleItemStackTo(sourceItem, 4 * 9 + 3, 4 * 9 + 3 + 3, player, 0, 4 * 9, false) &&
                    !moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 3, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 3 + 3) {
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
