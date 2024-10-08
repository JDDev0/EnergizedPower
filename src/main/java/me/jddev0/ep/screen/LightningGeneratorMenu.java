package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.LightningGeneratorBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class LightningGeneratorMenu extends EnergyStorageMenu<LightningGeneratorBlockEntity> {
    public LightningGeneratorMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv);
    }

    public LightningGeneratorMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory) {
        super(
                EPMenuTypes.LIGHTNING_GENERATOR_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.LIGHTNING_GENERATOR
        );
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
