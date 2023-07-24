package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class WeatherControllerMenu extends ScreenHandler implements EnergyStorageMenu {
    private final WeatherControllerBlockEntity blockEntity;
    private final World level;

    public WeatherControllerMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv);
    }

    public WeatherControllerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory) {
        super(ModMenuTypes.WEATHER_CONTROLLER_MENU, id);

        this.blockEntity = (WeatherControllerBlockEntity)blockEntity;

        this.level = playerInventory.player.world;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public long getEnergy() {
        return blockEntity.getEnergy();
    }

    @Override
    public long getCapacity() {
        return blockEntity.getCapacity();
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, ModBlocks.WEATHER_CONTROLLER);
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

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
