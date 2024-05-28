package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.LightningGeneratorBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class LightningGeneratorMenu extends EnergyStorageMenu<LightningGeneratorBlockEntity> {
    public LightningGeneratorMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv);
    }

    public LightningGeneratorMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory) {
        super(
                ModMenuTypes.LIGHTNING_GENERATOR_MENU, id,

                playerInventory, blockEntity,
                ModBlocks.LIGHTNING_GENERATOR
        );
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
