package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.HeatGeneratorBlockEntity;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.data.SimpleEnergyValueContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IEnergyStorageProducerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HeatGeneratorMenu extends UpgradableEnergyStorageMenu<HeatGeneratorBlockEntity>
        implements IEnergyStorageProducerIndicatorBarMenu {
    private final SimpleEnergyValueContainerData energyProductionPerTickData = new SimpleEnergyValueContainerData();

    public HeatGeneratorMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ENERGY_PRODUCTION
        ), null);
    }

    public HeatGeneratorMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                             ContainerData data) {
        super(
                EPMenuTypes.HEAT_GENERATOR_MENU.get(), id,

                inv, blockEntity,
                EPBlocks.HEAT_GENERATOR.get(),

                upgradeModuleInventory, 2
        );

        for(int i = 0;i < upgradeModuleInventory.getContainerSize();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 71 + i * 18, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addDataSlots(energyProductionPerTickData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
    public int getEnergyIndicatorBarValue() {
        return 0;
    }

    @Override
    public int getEnergyPerTickBarValue() {
        return energyProductionPerTickData.getValue();
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
            if(!moveUpgradeModuleItemStackTo(sourceItem, 4 * 9, 4 * 9 + 2, player, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 2) {
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
