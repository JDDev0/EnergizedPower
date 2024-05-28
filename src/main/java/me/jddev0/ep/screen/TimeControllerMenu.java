package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class TimeControllerMenu extends EnergyStorageMenu<TimeControllerBlockEntity> {
    public TimeControllerMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv);
    }

    public TimeControllerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory) {
        super(
                ModMenuTypes.TIME_CONTROLLER_MENU, id,

                playerInventory, blockEntity,
                ModBlocks.TIME_CONTROLLER
        );
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
