package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.UnchargerBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
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
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public class UnchargerMenu extends UpgradableEnergyStorageMenu<UnchargerBlockEntity>
        implements IEnergyStorageProducerIndicatorBarMenu, IConfigurableMenu {
    private final SimpleEnergyValueContainerData energyProductionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData energyProductionLeftData = new SimpleEnergyValueContainerData();
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public UnchargerMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv.player.level().getBlockEntity(pos), inv, new SimpleContainer(1) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                if(slot == 0) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(energyStorage == null)
                        return false;

                    return energyStorage.supportsExtraction();
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), null);
    }

    public UnchargerMenu(int id, BlockEntity blockEntity, Inventory playerInventory, Container inv,
                         UpgradeModuleInventory upgradeModuleInventory, ContainerData data) {
        super(
                EPMenuTypes.UNCHARGER_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.UNCHARGER,

                upgradeModuleInventory, 1
        );

        checkContainerSize(inv, 1);

        addSlot(new ConstraintInsertSlot(inv, 0, 80, 35) {
            @Override
            public boolean isActive() {
                return super.isActive() && !isInUpgradeModuleView();
            }
        });

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
            //Allow only 1 item
            if(!moveItemStackTo(sourceItem, 4 * 9 + 1, 4 * 9 + 1 + 1, false) &&
                    (slots.get(4 * 9).hasItem() || !moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 1, false))) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1 + 1) {
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
