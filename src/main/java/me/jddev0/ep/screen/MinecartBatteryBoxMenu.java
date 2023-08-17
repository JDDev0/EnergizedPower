package me.jddev0.ep.screen;

import me.jddev0.ep.util.ByteUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MinecartBatteryBoxMenu extends AbstractContainerMenu implements EnergyStorageMenu {
    private final Container container;
    private final Level level;
    private final ContainerData data;

    public MinecartBatteryBoxMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, new SimpleContainer(0), new SimpleContainerData(4));
    }

    public MinecartBatteryBoxMenu(int id, Inventory inv, Container container, ContainerData data) {
        super(ModMenuTypes.MINECART_BATTERY_BOX_MENU.get(), id);

        checkContainerDataCount(data, 4);
        this.container = container;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(this.data);
    }

    @Override
    public int getEnergy() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1));
    }

    @Override
    public int getCapacity() {
        return ByteUtils.from2ByteChunks((short)data.get(2), (short)data.get(3));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
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
