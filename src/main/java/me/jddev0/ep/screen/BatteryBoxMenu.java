package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.BatteryBoxBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class BatteryBoxMenu extends EnergyStorageMenu<BatteryBoxBlockEntity> {
    public BatteryBoxMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv);
    }

    public BatteryBoxMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory) {
        super(
                ModMenuTypes.BATTERY_BOX_MENU, id,

                playerInventory, blockEntity,
                ModBlocks.BATTERY_BOX
        );
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
