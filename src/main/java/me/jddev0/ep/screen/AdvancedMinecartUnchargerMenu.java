package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AdvancedMinecartUnchargerBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class AdvancedMinecartUnchargerMenu extends EnergyStorageMenu<AdvancedMinecartUnchargerBlockEntity> {
    public AdvancedMinecartUnchargerMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv);
    }

    public AdvancedMinecartUnchargerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory) {
        super(
                EPMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.ADVANCED_MINECART_UNCHARGER
        );
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
