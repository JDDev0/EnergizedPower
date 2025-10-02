package me.jddev0.ep.screen;

import me.jddev0.ep.inventory.data.SimpleEnergyValueContainerData;
import me.jddev0.ep.screen.base.IEnergyStorageMenu;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class MinecartAdvancedBatteryBoxMenu extends ScreenHandler implements IEnergyStorageMenu {
    private final Inventory inv;
    private final World level;

    private final SimpleEnergyValueContainerData energyData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData capacityData = new SimpleEnergyValueContainerData();

    public MinecartAdvancedBatteryBoxMenu(int id, PlayerInventory inv) {
        this(id, inv, new SimpleInventory(0), null);
    }

    public MinecartAdvancedBatteryBoxMenu(int id, PlayerInventory playerInventory, Inventory inv, PropertyDelegate data) {
        super(EPMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU, id);

        this.inv = inv;
        checkSize(inv, 0);
        this.level = playerInventory.player.getEntityWorld();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        if(data == null) {
            addProperties(energyData);
            addProperties(capacityData);
        }else {
            addProperties(data);
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
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inv.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for(int i = 0;i < 3;i++) {
            for(int j = 0;j < 9;j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for(int i = 0;i < 9;i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
