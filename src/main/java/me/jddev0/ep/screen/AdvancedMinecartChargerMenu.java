package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AdvancedChargerBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AdvancedMinecartChargerMenu extends EnergyStorageMenu<AdvancedChargerBlockEntity> {
    public AdvancedMinecartChargerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level.getBlockEntity(buffer.readBlockPos()));
    }

    public AdvancedMinecartChargerMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                EPMenuTypes.ADVANCED_MINECART_CHARGER_MENU.get(), id,

                inv, blockEntity,
                EPBlocks.ADVANCED_MINECART_CHARGER.get()
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
