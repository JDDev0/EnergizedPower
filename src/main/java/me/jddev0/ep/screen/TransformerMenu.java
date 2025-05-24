package me.jddev0.ep.screen;

import me.jddev0.ep.block.TransformerBlock;
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
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TransformerMenu extends EnergyStorageMenu<TransformerBlockEntity>
        implements IConfigurableMenu {
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();

    public static MenuType<TransformerMenu> getMenuTypeFromTierAndType(TransformerBlock.Tier tier, TransformerBlock.Type type) {
        return switch(tier) {
            case TIER_LV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.LV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EPMenuTypes.LV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EPMenuTypes.LV_TRANSFORMER_N_TO_1_MENU.get();
            };
            case TIER_MV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.MV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EPMenuTypes.MV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EPMenuTypes.MV_TRANSFORMER_N_TO_1_MENU.get();
            };
            case TIER_HV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.HV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EPMenuTypes.HV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EPMenuTypes.HV_TRANSFORMER_N_TO_1_MENU.get();
            };
            case TIER_EHV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.EHV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EPMenuTypes.EHV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EPMenuTypes.EHV_TRANSFORMER_N_TO_1_MENU.get();
            };
        };
    }

    public TransformerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public TransformerMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(
                getMenuTypeFromTierAndType(
                        ((TransformerBlockEntity)blockEntity).getTier(),
                        ((TransformerBlockEntity)blockEntity).getTransformerType()
                ), id,

                inv, blockEntity,
                TransformerBlock.getBlockFromTierAndType(
                        ((TransformerBlockEntity)blockEntity).getTier(),
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
