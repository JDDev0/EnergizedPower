package me.jddev0.ep.screen;

import me.jddev0.ep.block.entity.TransformerBlockEntity;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;

public class TransformerMenu extends EnergyStorageMenu<TransformerBlockEntity>
        implements IConfigurableMenu {
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();

    public TransformerMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getEntityWorld().getBlockEntity(pos), inv, null);
    }

    public TransformerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, PropertyDelegate data) {
        super(
                ((TransformerBlockEntity)blockEntity).getTier().getMenuTypeFromTierAndType(
                        ((TransformerBlockEntity)blockEntity).getTransformerType()
                ), id,

                playerInventory, blockEntity,
                ((TransformerBlockEntity)blockEntity).getTier().getBlockFromTierAndType(
                        ((TransformerBlockEntity)blockEntity).getTransformerType()
                )
        );

        if(data == null) {
            addProperties(redstoneModeData);
        }else {
            addProperties(data);
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
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
