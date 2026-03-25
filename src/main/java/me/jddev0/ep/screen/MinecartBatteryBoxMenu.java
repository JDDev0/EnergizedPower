package me.jddev0.ep.screen;

import me.jddev0.ep.inventory.data.SimpleEnergyValueContainerData;
import me.jddev0.ep.screen.base.IEnergyStorageMenu;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MinecartBatteryBoxMenu extends AbstractContainerMenu implements IEnergyStorageMenu {
    private final Container inv;
    private final Level level;

    private final SimpleEnergyValueContainerData energyData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData capacityData = new SimpleEnergyValueContainerData();

    public MinecartBatteryBoxMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainer(0), null);
    }

    public MinecartBatteryBoxMenu(int id, Inventory playerInventory, Container inv, ContainerData data) {
        super(EPMenuTypes.MINECART_BATTERY_BOX_MENU, id);

        this.inv = inv;
        checkContainerSize(inv, 0);
        this.level = playerInventory.player.level();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        if(data == null) {
            addDataSlots(energyData);
            addDataSlots(capacityData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
    public long getEnergy() {
        return energyData.getValue();
    }

    @Override
    public long getCapacity() {
        return capacityData.getValue();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return inv.stillValid(player);
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
}
