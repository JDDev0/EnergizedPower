package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.MinecartChargerBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MinecartChargerMenu extends EnergyStorageMenu<MinecartChargerBlockEntity> {
    public MinecartChargerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level.getBlockEntity(buffer.readBlockPos()));
    }

    public MinecartChargerMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                ModMenuTypes.MINECART_CHARGER_MENU.get(), id,

                inv, blockEntity,
                ModBlocks.MINECART_CHARGER.get()
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
