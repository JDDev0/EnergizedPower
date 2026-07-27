package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.ElitePoweredFurnaceBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.inventory.ItemCapabilityMenuHelper;
import me.jddev0.ep.inventory.ResourceHandlerSlot;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.ConfigurableIOUpgradableEnergyStorageMenu;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageConsumerIndicatorBarMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ElitePoweredFurnaceMenu extends ConfigurableIOUpgradableEnergyStorageMenu<ElitePoweredFurnaceBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final SimpleProgressValueContainerData[] progressData = new SimpleProgressValueContainerData[7];
    private final SimpleProgressValueContainerData[] maxProgressData = new SimpleProgressValueContainerData[7];
    private final SimpleEnergyValueContainerData[] energyConsumptionLeftData = new SimpleEnergyValueContainerData[7];
    private final SimpleBooleanValueContainerData[] hasEnoughEnergyData = new SimpleBooleanValueContainerData[7];

    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public ElitePoweredFurnaceMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, inv.player.level().getBlockEntity(pos), new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING,
                UpgradeModuleModifier.XP_YIELD
        ), null);
    }

    public ElitePoweredFurnaceMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                                   ContainerData data) {
        super(
                EPMenuTypes.ELITE_POWERED_FURNACE_MENU, id,

                inv, blockEntity,
                EPBlocks.ELITE_POWERED_FURNACE,
                35, 84,

                upgradeModuleInventory, 7
        );

        ItemCapabilityMenuHelper.getEnergizedPowerItemStackHandlerCapability(this.level, this.blockEntity).ifPresent(itemHandler -> {
            for(int i = 0;i < 7;i++) {
                addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, i, 35 + i * 24, 17) {
                    @Override
                    public boolean isActive() {
                        return super.isActive() && !isInUpgradeModuleView();
                    }
                });
            }

            for(int i = 0;i < 7;i++) {
                addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 7 + i, 35 + i * 24, 53) {
                    @Override
                    public boolean isActive() {
                        return super.isActive() && !isInUpgradeModuleView();
                    }
                });
            }
        });

        for(int i = 0;i < upgradeModuleInventory.getContainerSize();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 53 + i * 18, 35, this::isInUpgradeModuleView));


        for(int i = 0;i < 7;i++) {
            progressData[i] = new SimpleProgressValueContainerData();
            maxProgressData[i] = new SimpleProgressValueContainerData();
            energyConsumptionLeftData[i] = new SimpleEnergyValueContainerData();
            hasEnoughEnergyData[i] = new SimpleBooleanValueContainerData();
        }

        if(data == null) {
            for(int i = 0;i < 7;i++) {
                addDataSlots(progressData[i]);
                addDataSlots(maxProgressData[i]);
                addDataSlots(energyConsumptionLeftData[i]);
                addDataSlots(hasEnoughEnergyData[i]);
            }

            addDataSlots(energyConsumptionPerTickData);
            addDataSlots(redstoneModeData);
            addDataSlots(comparatorModeData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        long energyIndicatorBarValueSum = -1;

        for(SimpleEnergyValueContainerData ele:energyConsumptionLeftData) {
            long value = ele.getValue();

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
    public long getEnergyPerTickBarValue() {
        return energyConsumptionPerTickData.getValue();
    }

    public FluidStack getFluid() {
        return blockEntity.getFluid(0);
    }

    public long getTankCapacity() {
        return blockEntity.getTankCapacity(0);
    }

    public boolean hasXPExtractionUpgradeModule() {
        return slots.get(4 * 9 + 14 + 6) instanceof UpgradeModuleSlot slot &&
                slot.getItem().getItem() instanceof UpgradeModuleItem item &&
                item.getMainUpgradeModuleModifier() == UpgradeModuleModifier.XP_YIELD;
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive(int index) {
        return maxProgressData[index].getValue() > 0;
    }

    public boolean isCrafting(int index) {
        return maxProgressData[index].getValue() > 0 && hasEnoughEnergyData[index].getValue();
    }

    public int getScaledProgressArrowSize(int index) {
        int progress = progressData[index].getValue();
        int maxProgress = maxProgressData[index].getValue();
        int progressArrowSize = 17;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneModeData.getValue();
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return comparatorModeData.getValue();
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
            if(!moveUpgradeModuleItemStackTo(sourceItem, 4 * 9 + 14, 4 * 9 + 14 + 7, player, 0, 4 * 9, false) &&
                    !moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 7, false)) {
                //"+7" instead of "+14": Do not allow adding to output slots
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 14 + 7) {
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
