package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.CoalEngineBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageProducerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import me.jddev0.ep.util.ByteUtils;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class CoalEngineMenu extends UpgradableEnergyStorageMenu<CoalEngineBlockEntity>
        implements IEnergyStorageProducerIndicatorBarMenu, IConfigurableMenu {
    private final PropertyDelegate data;

    public CoalEngineMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv, new SimpleInventory(1) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0) {
                    Integer burnTime = FuelRegistry.INSTANCE.get(stack.getItem());
                    return burnTime != null && burnTime > 0;
                }

                return super.isValid(slot, stack);
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), new ArrayPropertyDelegate(11));
    }

    public CoalEngineMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                          UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(
                EPMenuTypes.COAL_ENGINE_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.COAL_ENGINE,

                upgradeModuleInventory, 1
        );

        checkSize(inv, 1);
        checkDataCount(data, 11);
        this.data = data;

        addSlot(new ConstraintInsertSlot(inv, 0, 80, 44) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });

        addSlot(new UpgradeModuleSlot(upgradeModuleInventory, 0, 80, 35, this::isInUpgradeModuleView));

        addProperties(this.data);
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(4), (short)data.get(5), (short)data.get(6), (short)data.get(7));
    }

    /**
     * @return Same as isProducing but energy production are ignored
     */
    public boolean isProducingActive() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1)) > 0;
    }

    public boolean isProducing() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1)) > 0 && data.get(8) == 1;
    }

    public int getScaledProgressFlameSize() {
        int progress = ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1));
        int maxProgress = ByteUtils.from2ByteChunks((short)data.get(2), (short)data.get(3));
        int progressFlameSize = 14;

        return (maxProgress == 0 || progress == 0)?0:(progress * progressFlameSize / maxProgress);
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(9));
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return ComparatorMode.fromIndex(data.get(10));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory, Merge into tile inventory
            if(!insertItem(sourceItem, 4 * 9 + 1, 4 * 9 + 1 + 1, false) &&
                    !insertItem(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1 + 1) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!insertItem(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.setStack(ItemStack.EMPTY);
        else
            sourceSlot.markDirty();

        sourceSlot.onTakeItem(player, sourceItem);

        return sourceItemCopy;
    }
}
