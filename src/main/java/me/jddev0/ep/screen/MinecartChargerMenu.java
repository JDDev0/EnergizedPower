package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.MinecartChargerBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class MinecartChargerMenu extends EnergyStorageMenu<MinecartChargerBlockEntity> {
    public MinecartChargerMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv);
    }

    public MinecartChargerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory) {
        super(
                ModMenuTypes.MINECART_CHARGER_MENU, id,

                playerInventory, blockEntity,
                ModBlocks.MINECART_CHARGER
        );
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
