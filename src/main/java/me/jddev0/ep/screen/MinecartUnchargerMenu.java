package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.MinecartUnchargerBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MinecartUnchargerMenu extends EnergyStorageMenu<MinecartUnchargerBlockEntity> {
    public MinecartUnchargerMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv.player.level().getBlockEntity(pos), inv);
    }

    public MinecartUnchargerMenu(int id, BlockEntity blockEntity, Inventory playerInventory) {
        super(
                EPMenuTypes.MINECART_UNCHARGER_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.MINECART_UNCHARGER
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
