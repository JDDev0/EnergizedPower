package me.jddev0.ep.screen;

import me.jddev0.ep.block.TransformerBlock;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;

public class TransformerMenu extends EnergyStorageMenu<TransformerBlockEntity>
        implements IConfigurableMenu {
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();

    public static ScreenHandlerType<TransformerMenu> getMenuTypeFromTierAndType(TransformerBlock.Tier tier, TransformerBlock.Type type) {
        return switch(tier) {
            case TIER_LV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.LV_TRANSFORMER_1_TO_N_MENU;
                case TYPE_3_TO_3 -> EPMenuTypes.LV_TRANSFORMER_3_TO_3_MENU;
                case TYPE_N_TO_1 -> EPMenuTypes.LV_TRANSFORMER_N_TO_1_MENU;
            };
            case TIER_MV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.MV_TRANSFORMER_1_TO_N_MENU;
                case TYPE_3_TO_3 -> EPMenuTypes.MV_TRANSFORMER_3_TO_3_MENU;
                case TYPE_N_TO_1 -> EPMenuTypes.MV_TRANSFORMER_N_TO_1_MENU;
            };
            case TIER_HV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.HV_TRANSFORMER_1_TO_N_MENU;
                case TYPE_3_TO_3 -> EPMenuTypes.HV_TRANSFORMER_3_TO_3_MENU;
                case TYPE_N_TO_1 -> EPMenuTypes.HV_TRANSFORMER_N_TO_1_MENU;
            };
            case TIER_EHV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.EHV_TRANSFORMER_1_TO_N_MENU;
                case TYPE_3_TO_3 -> EPMenuTypes.EHV_TRANSFORMER_3_TO_3_MENU;
                case TYPE_N_TO_1 -> EPMenuTypes.EHV_TRANSFORMER_N_TO_1_MENU;
            };
        };
    }

    public TransformerMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, null);
    }

    public TransformerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, PropertyDelegate data) {
        super(
                getMenuTypeFromTierAndType(
                        ((TransformerBlockEntity)blockEntity).getTier(),
                        ((TransformerBlockEntity)blockEntity).getTransformerType()
                ), id,

                playerInventory, blockEntity,
                TransformerBlock.getBlockFromTierAndType(
                        ((TransformerBlockEntity)blockEntity).getTier(),
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
