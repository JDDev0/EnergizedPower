package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AdvancedPoweredFurnaceBlockEntity;
import me.jddev0.ep.inventory.ItemCapabilityMenuHelper;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageConsumerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class AdvancedPoweredFurnaceMenu extends UpgradableEnergyStorageMenu<AdvancedPoweredFurnaceBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final ContainerData data;

    public AdvancedPoweredFurnaceMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE
        ), new SimpleContainerData(23));
    }

    public AdvancedPoweredFurnaceMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                                      ContainerData data) {
        super(
                EPMenuTypes.ADVANCED_POWERED_FURNACE_MENU.get(), id,

                inv, blockEntity,
                EPBlocks.ADVANCED_POWERED_FURNACE.get(),

                upgradeModuleInventory, 4
        );

        checkContainerDataCount(data, 23);
        this.data = data;

        ItemCapabilityMenuHelper.getCapabilityItemHandler(this.level, this.blockEntity).ifPresent(itemHandler -> {
            addSlot(new SlotItemHandler(itemHandler, 0, 44, 17) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
            addSlot(new SlotItemHandler(itemHandler, 1, 98, 17) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
            addSlot(new SlotItemHandler(itemHandler, 2, 152, 17) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
            addSlot(new SlotItemHandler(itemHandler, 3, 44, 53) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
            addSlot(new SlotItemHandler(itemHandler, 4, 98, 53) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
            addSlot(new SlotItemHandler(itemHandler, 5, 152, 53) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView();
                }
            });
        });

        for(int i = 0;i < upgradeModuleInventory.getContainerSize();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 53 + i * 18, 35, this::isInUpgradeModuleView));

        addDataSlots(this.data);
    }

    @Override
    public int getEnergyIndicatorBarValue() {
        int energyIndicatorBarValueSum = -1;

        for(int i = 0;i < 6;i += 2) {
            int value = ByteUtils.from2ByteChunks((short)data.get(i), (short)data.get(i + 1));

            //Prevent overflow
            if(energyIndicatorBarValueSum + value != (long)energyIndicatorBarValueSum + value)
                return Integer.MAX_VALUE;

            if(value > -1) {
                if(energyIndicatorBarValueSum == -1)
                    energyIndicatorBarValueSum++;

                energyIndicatorBarValueSum += value;
            }
        }

        return energyIndicatorBarValueSum;
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive(int index) {
        return ByteUtils.from2ByteChunks((short)data.get(6 + 4 * index), (short)data.get(7 + 4 * index)) > 0;
    }

    public boolean isCrafting(int index) {
        return ByteUtils.from2ByteChunks((short)data.get(8 + 4 * index), (short)data.get(9 + 4 * index)) > 0 && data.get(18 + index) == 1;
    }

    public int getScaledProgressArrowSize(int index) {
        int progress = ByteUtils.from2ByteChunks((short)data.get(6 + 4 * index), (short)data.get(7 + 4 * index));
        int maxProgress = ByteUtils.from2ByteChunks((short)data.get(8 + 4 * index), (short)data.get(9 + 4 * index));
        int progressArrowSize = 17;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(21));
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return ComparatorMode.fromIndex(data.get(22));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getItem();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory, Merge into tile inventory
            if(!moveItemStackTo(sourceItem, 4 * 9 + 6, 4 * 9 + 6 + 4, false) &&
                    !moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 3, false)) {
                //"+3" instead of "+6": Do not allow adding to output slots
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 6 + 4) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceItem);

        return sourceItemCopy;
    }
}
