package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AdvancedBatteryBoxBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AdvancedBatteryBoxMenu extends EnergyStorageMenu<AdvancedBatteryBoxBlockEntity> {
    public AdvancedBatteryBoxMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv.player.level().getBlockEntity(pos), inv);
    }

    public AdvancedBatteryBoxMenu(int id, BlockEntity blockEntity, Inventory playerInventory) {
        super(
                EPMenuTypes.ADVANCED_BATTERY_BOX_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.ADVANCED_BATTERY_BOX
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
