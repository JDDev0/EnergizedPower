package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.inventory.*;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageConsumerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class AutoCrafterMenu extends UpgradableEnergyStorageMenu<AutoCrafterBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final Inventory patternSlots;

    private final Inventory patternResultSlots;

    private final SimpleProgressValueContainerData progressData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData maxProgressData = new SimpleProgressValueContainerData();
    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData energyConsumptionLeftData = new SimpleEnergyValueContainerData();
    private final SimpleBooleanValueContainerData hasEnoughEnergyData = new SimpleBooleanValueContainerData();
    private final SimpleBooleanValueContainerData ignoreNBTData = new SimpleBooleanValueContainerData();
    private final SimpleBooleanValueContainerData secondaryExtractModeData = new SimpleBooleanValueContainerData();
    private final SimpleBooleanValueContainerData allowOutputOverflowData = new SimpleBooleanValueContainerData();
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public AutoCrafterMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getEntityWorld().getBlockEntity(pos), inv, new SimpleInventory(18) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return super.isValid(slot, stack) && slot >= 3;
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), new SimpleInventory(9), new SimpleInventory(1), null);
    }

    public AutoCrafterMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                           UpgradeModuleInventory upgradeModuleInventory, Inventory patternSlots,
                           Inventory patternResultSlots, PropertyDelegate data) {
        super(
                EPMenuTypes.AUTO_CRAFTER_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.AUTO_CRAFTER,
                8, 124,

                upgradeModuleInventory, 3
        );

        this.patternSlots = patternSlots;
        this.patternResultSlots = patternResultSlots;

        checkSize(inv, 18);

        for(int i = 0;i < 2;i++)
            for(int j = 0;j < 9;j++)
                addSlot(new ConstraintInsertSlot(inv, 9 * i + j, 8 + 18 * j, 75 + 18 * i));

        for(int i = 0;i < 3;i++)
            for(int j = 0;j < 3;j++)
                addSlot(new PatternSlot(patternSlots, j + i * 3, 30 + j * 18, 17 + i * 18, () -> true) {
                    @Override
                    public boolean isEnabled() {
                        return super.isEnabled() && !isInUpgradeModuleView();
                    }
                });

        addSlot(new PatternResultSlot(patternResultSlots, 0, 124, 35, () -> true) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });

        for(int i = 0;i < upgradeModuleInventory.size();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 62 + i * 18, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addProperties(progressData);
            addProperties(maxProgressData);
            addProperties(energyConsumptionPerTickData);
            addProperties(energyConsumptionLeftData);
            addProperties(hasEnoughEnergyData);
            addProperties(ignoreNBTData);
            addProperties(secondaryExtractModeData);
            addProperties(allowOutputOverflowData);
            addProperties(redstoneModeData);
            addProperties(comparatorModeData);
        }else {
            addProperties(data);
        }
    }

    public Inventory getPatternSlots() {
        return patternSlots;
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        return energyConsumptionLeftData.getValue();
    }

    @Override
    public long getEnergyPerTickBarValue() {
        return energyConsumptionPerTickData.getValue();
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive() {
        return progressData.getValue() > 0;
    }

    public boolean isCrafting() {
        return progressData.getValue() > 0 && hasEnoughEnergyData.getValue();
    }

    public int getScaledProgressArrowSize() {
        int progress = progressData.getValue();
        int maxProgress = maxProgressData.getValue();
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    public boolean isIgnoreNBT() {
        return ignoreNBTData.getValue();
    }

    public boolean isSecondaryExtractMode() {
        return secondaryExtractModeData.getValue();
    }

    public boolean isAllowOutputOverflow() {
        return allowOutputOverflowData.getValue();
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
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory, Merge into tile inventory
            //"+ 18": Ignore 3x3 crafting grid and result slot
            if(!insertItem(sourceItem, 4 * 9 + 18 + 3*3 + 1, 4 * 9 + 18 + 3*3 + 1 + 3, false) &&
                    !insertItem(sourceItem, 4 * 9 + 3, 4 * 9 + 18, false)) {
                //"+3" instead of nothing: Do not allow adding to first 3 output item only slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 18) {
            //Tile inventory slot -> Merge into player inventory
            if(!insertItem(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 18 + 3*3 + 1) {
            return ItemStack.EMPTY;
        }else if(index < 4 * 9 + 18 + 3*3 + 1 + 3) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!insertItem(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else{
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
