package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EliteMinecartUnchargerBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EliteMinecartUnchargerMenu extends EnergyStorageMenu<EliteMinecartUnchargerBlockEntity> {
    public EliteMinecartUnchargerMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, inv.player.level().getBlockEntity(pos));
    }

    public EliteMinecartUnchargerMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                EPMenuTypes.ELITE_MINECART_UNCHARGER_MENU, id,

                inv, blockEntity,
                EPBlocks.ELITE_MINECART_UNCHARGER
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
