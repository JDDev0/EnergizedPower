package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.AdvancedUnchargerBlockEntity;
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
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public class AdvancedUnchargerMenu extends UpgradableEnergyStorageMenu<AdvancedUnchargerBlockEntity>
        implements IEnergyStorageProducerIndicatorBarMenu, IConfigurableMenu {
    private final PropertyDelegate data;

    public AdvancedUnchargerMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, new SimpleInventory(3) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(energyStorage == null)
                        return false;

                    return energyStorage.supportsExtraction();
                }

                return super.isValid(slot, stack);
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), new ArrayPropertyDelegate(14));
    }

    public AdvancedUnchargerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                                 UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(
                ModMenuTypes.ADVANCED_UNCHARGER_MENU, id,

                playerInventory, blockEntity,
                ModBlocks.ADVANCED_UNCHARGER,

                upgradeModuleInventory, 1
        );

        checkSize(inv, 3);
        checkDataCount(data, 14);
        this.data = data;

        addSlot(new ConstraintInsertSlot(inv, 0, 41, 35) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 1, 89, 35) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 2, 137, 35) {
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
        long energyIndicatorBarValueSum = -1;

        for(int i = 0;i < 12;i += 4) {
            long value = ByteUtils.from2ByteChunks((short)data.get(i), (short)data.get(i + 1),
                    (short)data.get(i + 2), (short)data.get(i + 3));

            //Prevent overflow
            if(Math.max(0, energyIndicatorBarValueSum) + Math.max(0, value) < 0)
                return Long.MAX_VALUE;

            if(value > -1) {
                if(energyIndicatorBarValueSum == -1)
                    energyIndicatorBarValueSum++;

                energyIndicatorBarValueSum += value;
            }
        }

        return energyIndicatorBarValueSum;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(12));
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return ComparatorMode.fromIndex(data.get(13));
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
            if(!insertMaxCount1Item(sourceItem, 4 * 9 + 3, 4 * 9 + 3 + 1, false) &&
                    !insertMaxCount1Item(sourceItem, 4 * 9, 4 * 9 + 3, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 3 + 1) {
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
