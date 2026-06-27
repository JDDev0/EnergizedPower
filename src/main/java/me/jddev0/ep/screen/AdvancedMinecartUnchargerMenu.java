package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AdvancedMinecartUnchargerBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AdvancedMinecartUnchargerMenu extends EnergyStorageMenu<AdvancedMinecartUnchargerBlockEntity> {
    public AdvancedMinecartUnchargerMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, inv.player.level().getBlockEntity(pos));
    }

    public AdvancedMinecartUnchargerMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                EPMenuTypes.ADVANCED_MINECART_UNCHARGER_MENU, id,

                inv, blockEntity,
                EPBlocks.ADVANCED_MINECART_UNCHARGER
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
