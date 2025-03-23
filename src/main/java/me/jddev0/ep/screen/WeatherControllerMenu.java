package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.data.SimpleBooleanValueContainerData;
import me.jddev0.ep.inventory.data.SimpleEnergyValueContainerData;
import me.jddev0.ep.inventory.data.SimpleShortValueContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WeatherControllerMenu extends UpgradableEnergyStorageMenu<WeatherControllerBlockEntity> {
    private final SimpleShortValueContainerData selectedWeatherTypeData = new SimpleShortValueContainerData();
    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleBooleanValueContainerData hasEnoughEnergyData = new SimpleBooleanValueContainerData();

    public WeatherControllerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new UpgradeModuleInventory(
                UpgradeModuleModifier.DURATION
        ), null);
    }

    public WeatherControllerMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                                 ContainerData data) {
        super(
                EPMenuTypes.WEATHER_CONTROLLER_MENU.get(), id,

                inv, blockEntity,
                EPBlocks.WEATHER_CONTROLLER.get(),

                upgradeModuleInventory, 1
        );

        addSlot(new UpgradeModuleSlot(upgradeModuleInventory, 0, 80, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addDataSlots(selectedWeatherTypeData);
            addDataSlots(energyConsumptionPerTickData);
            addDataSlots(hasEnoughEnergyData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
    public int getEnergyPerTickBarValue() {
        return energyConsumptionPerTickData.getValue();
    }

    public int getSelectedWeatherType() {
        return selectedWeatherTypeData.getValue();
    }

    public boolean hasEnoughEnergy() {
        return hasEnoughEnergyData.getValue();
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
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceItem);

        return sourceItemCopy;
    }
}
