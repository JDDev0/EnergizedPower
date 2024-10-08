package me.jddev0.ep.screen;

import me.jddev0.ep.screen.base.AbstractEnergizedPowerScreenHandler;
import me.jddev0.ep.screen.base.IEnergyStorageMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class MinecartAdvancedBatteryBoxMenu extends AbstractEnergizedPowerScreenHandler implements IEnergyStorageMenu {
    private final Inventory inv;
    private final World level;
    private final PropertyDelegate data;

    public MinecartAdvancedBatteryBoxMenu(int id, PlayerInventory inv) {
        this(id, inv, new SimpleInventory(0), new ArrayPropertyDelegate(8));
    }

    public MinecartAdvancedBatteryBoxMenu(int id, PlayerInventory playerInventory, Inventory inv, PropertyDelegate data) {
        super(EPMenuTypes.MINECART_ADVANCED_BATTERY_BOX_MENU, id);

        this.inv = inv;
        checkSize(inv, 0);
        checkDataCount(data, 8);
        this.level = playerInventory.player.getWorld();
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(this.data);
    }

    @Override
    public long getEnergy() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1), (short)data.get(2), (short)data.get(3));
    }

    @Override
    public long getCapacity() {
        return ByteUtils.from2ByteChunks((short)data.get(4), (short)data.get(5), (short)data.get(6), (short)data.get(7));
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
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
