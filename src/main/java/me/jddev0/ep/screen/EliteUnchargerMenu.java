package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EliteUnchargerBlockEntity;
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
import me.jddev0.ep.screen.base.ConfigurableIOUpgradableEnergyStorageMenu;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageProducerIndicatorBarMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EliteUnchargerMenu extends ConfigurableIOUpgradableEnergyStorageMenu<EliteUnchargerBlockEntity>
        implements IEnergyStorageProducerIndicatorBarMenu, IConfigurableMenu {
    private final SimpleEnergyValueContainerData energyProductionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData[] energyProductionLeftData = new SimpleEnergyValueContainerData[7];
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public EliteUnchargerMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, inv.player.level().getBlockEntity(pos), new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        ), null);
    }

    public EliteUnchargerMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                              ContainerData data) {
        super(
                EPMenuTypes.ELITE_UNCHARGER_MENU, id,

                inv, blockEntity,
                EPBlocks.ELITE_UNCHARGER,
                35, 84,

                upgradeModuleInventory, 3
        );

        ItemCapabilityMenuHelper.getEnergizedPowerItemStackHandlerCapability(this.level, this.blockEntity).ifPresent(itemHandler -> {
            for(int i = 0;i < 7;i++) {
                addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, i, 35 + i * 24, 35) {
                    @Override
                    public boolean isActive() {
                        return super.isActive() && !isInUpgradeModuleView();
                    }
                });
            }
        });

        for(int i = 0;i < upgradeModuleInventory.getContainerSize();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 89 + i * 18, 35, this::isInUpgradeModuleView));

        for(int i = 0;i < 7;i++)
            energyProductionLeftData[i] = new SimpleEnergyValueContainerData();

        if(data == null) {
            addDataSlots(energyProductionPerTickData);

            for(int i = 0;i < 7;i++)
                addDataSlots(energyProductionLeftData[i]);

            addDataSlots(redstoneModeData);
            addDataSlots(comparatorModeData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        long energyIndicatorBarValueSum = -1;

        for(SimpleEnergyValueContainerData ele:energyProductionLeftData) {
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
        return energyProductionPerTickData.getValue();
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
            if(!moveUpgradeModuleItemStackTo(sourceItem, 4 * 9 + 7, 4 * 9 + 7 + 3, player, 0, 4 * 9, false) &&
                    !moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 7, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 7 + 3) {
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
