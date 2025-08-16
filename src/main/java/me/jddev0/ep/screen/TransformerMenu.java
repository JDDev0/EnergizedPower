package me.jddev0.ep.screen;

import me.jddev0.ep.block.entity.TransformerBlockEntity;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TransformerMenu extends EnergyStorageMenu<TransformerBlockEntity>
        implements IConfigurableMenu {
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();

    public TransformerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public TransformerMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(
                ((TransformerBlockEntity)blockEntity).getTier().getMenuTypeFromTierAndType(
                        ((TransformerBlockEntity)blockEntity).getTransformerType()
                ), id,

                inv, blockEntity,
                ((TransformerBlockEntity)blockEntity).getTier().getBlockFromTierAndType(
                        ((TransformerBlockEntity)blockEntity).getTransformerType()
                )
        );

        if(data == null) {
            addDataSlots(redstoneModeData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneModeData.getValue();
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return ComparatorMode.ENERGY;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
