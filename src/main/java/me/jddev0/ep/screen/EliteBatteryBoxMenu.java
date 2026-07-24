package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EliteBatteryBoxBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EliteBatteryBoxMenu extends EnergyStorageMenu<EliteBatteryBoxBlockEntity> {
    public EliteBatteryBoxMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, inv.player.level().getBlockEntity(pos));
    }

    public EliteBatteryBoxMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                EPMenuTypes.ELITE_BATTERY_BOX_MENU.get(), id,

                inv, blockEntity,
                EPBlocks.ELITE_BATTERY_BOX.get()
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
