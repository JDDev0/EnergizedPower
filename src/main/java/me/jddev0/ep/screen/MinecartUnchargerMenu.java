package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.MinecartUnchargerBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MinecartUnchargerMenu extends EnergyStorageMenu<MinecartUnchargerBlockEntity> {
    public MinecartUnchargerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public MinecartUnchargerMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                EPMenuTypes.MINECART_UNCHARGER_MENU.get(), id,

                inv, blockEntity,
                EPBlocks.MINECART_UNCHARGER.get()
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
