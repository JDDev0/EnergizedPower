package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.LightningGeneratorBlockEntity;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LightningGeneratorMenu extends EnergyStorageMenu<LightningGeneratorBlockEntity> {
    public LightningGeneratorMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level.getBlockEntity(buffer.readBlockPos()));
    }

    public LightningGeneratorMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(
                ModMenuTypes.LIGHTNING_GENERATOR_MENU.get(), id,

                inv, blockEntity,
                ModBlocks.LIGHTNING_GENERATOR.get()
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
