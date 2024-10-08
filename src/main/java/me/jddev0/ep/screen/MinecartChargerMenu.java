package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.MinecartChargerBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class MinecartChargerMenu extends EnergyStorageMenu<MinecartChargerBlockEntity> {
    public MinecartChargerMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv);
    }

    public MinecartChargerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory) {
        super(
                EPMenuTypes.MINECART_CHARGER_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.MINECART_CHARGER
        );
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
