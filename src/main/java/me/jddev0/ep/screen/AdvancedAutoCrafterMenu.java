package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import me.jddev0.ep.inventory.*;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageConsumerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class AdvancedAutoCrafterMenu extends UpgradableEnergyStorageMenu<AdvancedAutoCrafterBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final Container[] patternSlots;

    private final Container[] patternResultSlots;

    private final SimpleProgressValueContainerData[] progressData = new SimpleProgressValueContainerData[] {
            new SimpleProgressValueContainerData(),
            new SimpleProgressValueContainerData(),
            new SimpleProgressValueContainerData()
    };
    private final SimpleProgressValueContainerData[] maxProgressData = new SimpleProgressValueContainerData[] {
            new SimpleProgressValueContainerData(),
            new SimpleProgressValueContainerData(),
            new SimpleProgressValueContainerData()
    };
    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData[] energyConsumptionLeftData = new SimpleEnergyValueContainerData[] {
            new SimpleEnergyValueContainerData(),
            new SimpleEnergyValueContainerData(),
            new SimpleEnergyValueContainerData()
    };
    private final SimpleBooleanValueContainerData[] hasEnoughEnergyData = new SimpleBooleanValueContainerData[] {
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData()
    };
    private final SimpleBooleanValueContainerData[] ignoreNBTData = new SimpleBooleanValueContainerData[] {
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData()
    };
    private final SimpleBooleanValueContainerData secondaryExtractModeData = new SimpleBooleanValueContainerData();
    private final SimpleBooleanValueContainerData allowOutputOverflowData = new SimpleBooleanValueContainerData();
    private final SimpleShortValueContainerData currentRecipeIndexData = new SimpleShortValueContainerData();
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public AdvancedAutoCrafterMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), new Container[] {
                new SimpleContainer(9), new SimpleContainer(9), new SimpleContainer(9)
        }, new Container[] {
                new SimpleContainer(1), new SimpleContainer(1), new SimpleContainer(1)
        }, null);
    }

    public AdvancedAutoCrafterMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                                   Container[] patternSlots, Container[] patternResultSlots, ContainerData data) {
        super(
                EPMenuTypes.ADVANCED_AUTO_CRAFTER_MENU.get(), id,

                inv, blockEntity,
                EPBlocks.ADVANCED_AUTO_CRAFTER.get(),
                8, 142,

                upgradeModuleInventory, 3
        );

        this.patternSlots = patternSlots;
        this.patternResultSlots = patternResultSlots;

        ItemCapabilityMenuHelper.getEnergizedPowerItemStackHandlerCapability(this.level, this.blockEntity).ifPresent(itemHandler -> {
            for(int i = 0;i < 3;i++)
                for(int j = 0;j < 9;j++)
                    addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 9 * i + j, 8 + 18 * j, 75 + 18 * i));
        });

        for(int ri = 0;ri < 3;ri++) {
            final int recipeIndex = ri;

            for(int i = 0;i < 3;i++) {
                for(int j = 0;j < 3;j++) {
                    addSlot(new PatternSlot(patternSlots[recipeIndex], j + i * 3, 30 + j * 18, 17 + i * 18, () -> true) {
                        @Override
                        public boolean isActive() {
                            return super.isActive() && !isInUpgradeModuleView() && getRecipeIndex() == recipeIndex;
                        }
                    });
                }
            }

            addSlot(new PatternResultSlot(patternResultSlots[recipeIndex], 0, 124, 35, () -> true) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInUpgradeModuleView() && getRecipeIndex() == recipeIndex;
                }
            });
        }

        for(int i = 0;i < upgradeModuleInventory.getContainerSize();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 62 + i * 18, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addDataSlots(progressData[0]);
            addDataSlots(progressData[1]);
            addDataSlots(progressData[2]);
            addDataSlots(maxProgressData[0]);
            addDataSlots(maxProgressData[1]);
            addDataSlots(maxProgressData[2]);
            addDataSlots(energyConsumptionPerTickData);
            addDataSlots(energyConsumptionLeftData[0]);
            addDataSlots(energyConsumptionLeftData[1]);
            addDataSlots(energyConsumptionLeftData[2]);
            addDataSlots(hasEnoughEnergyData[0]);
            addDataSlots(hasEnoughEnergyData[1]);
            addDataSlots(hasEnoughEnergyData[2]);
            addDataSlots(ignoreNBTData[0]);
            addDataSlots(ignoreNBTData[1]);
            addDataSlots(ignoreNBTData[2]);
            addDataSlots(secondaryExtractModeData);
            addDataSlots(allowOutputOverflowData);
            addDataSlots(currentRecipeIndexData);
            addDataSlots(redstoneModeData);
            addDataSlots(comparatorModeData);
        }else {
            addDataSlots(data);
        }
    }

    public Container[] getPatternSlots() {
        return patternSlots;
    }

    @Override
    public int getEnergyIndicatorBarValue() {
        int energyIndicatorBarValueSum = -1;

        for(SimpleEnergyValueContainerData ele:energyConsumptionLeftData) {
            int value = ele.getValue();

            //Prevent overflow
            if (energyIndicatorBarValueSum + value != (long) energyIndicatorBarValueSum + value)
                return Integer.MAX_VALUE;

            if (value > -1) {
                if (energyIndicatorBarValueSum == -1)
                    energyIndicatorBarValueSum++;

                energyIndicatorBarValueSum += value;
            }
        }

        return energyIndicatorBarValueSum;
    }

    @Override
    public int getEnergyPerTickBarValue() {
        return energyConsumptionPerTickData.getValue();
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive() {
        int index = getRecipeIndex();
        return progressData[index].getValue() > 0;
    }

    public boolean isCrafting() {
        int index = getRecipeIndex();
        return progressData[index].getValue() > 0 && hasEnoughEnergyData[index].getValue();
    }

    public int getScaledProgressArrowSize() {
        int index = getRecipeIndex();
        int progress = progressData[index].getValue();
        int maxProgress = maxProgressData[index].getValue();
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    public boolean isIgnoreNBT() {
        int index = getRecipeIndex();
        return ignoreNBTData[index].getValue();
    }

    public boolean isSecondaryExtractMode() {
        return secondaryExtractModeData.getValue();
    }

    public boolean isAllowOutputOverflow() {
        return allowOutputOverflowData.getValue();
    }

    public int getRecipeIndex() {
        return currentRecipeIndexData.getValue();
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
            //"+ 27": Ignore 3 * (3x3 crafting grid and result slot)
            if(!moveItemStackTo(sourceItem, 4 * 9 + 27 + 3 * (3*3 + 1), 4 * 9 + 27 + 3 * (3*3 + 1) + 3, false) &&
                    !moveItemStackTo(sourceItem, 4 * 9 + 5, 4 * 9 + 27, false)) {
                //"+5" instead of nothing: Do not allow adding to first 5 output item only slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 27) {
            //Tile inventory slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 27 + 3 * (3*3 + 1)) {
            return ItemStack.EMPTY;
        }else if(index < 4 * 9 + 27 + 3 * (3*3 + 1) + 3) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else{
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
