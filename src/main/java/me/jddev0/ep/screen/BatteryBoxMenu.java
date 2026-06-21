package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.BatteryBoxBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BatteryBoxMenu extends EnergyStorageMenu<BatteryBoxBlockEntity> {
    public BatteryBoxMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, inv.player.level().getBlockEntity(pos));
    }

    public BatteryBoxMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                EPMenuTypes.BATTERY_BOX_MENU, id,

                inv, blockEntity,
                EPBlocks.BATTERY_BOX
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
