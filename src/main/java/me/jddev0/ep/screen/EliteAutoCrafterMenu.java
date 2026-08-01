package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EliteAutoCrafterBlockEntity;
import me.jddev0.ep.inventory.ItemCapabilityMenuHelper;
import me.jddev0.ep.inventory.PatternResultSlot;
import me.jddev0.ep.inventory.PatternSlot;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.ConfigurableIOUpgradableEnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class EliteAutoCrafterMenu extends ConfigurableIOUpgradableEnergyStorageMenu<EliteAutoCrafterBlockEntity>
        implements IAutoCrafterMenu {
    private final Container[] patternSlots;

    private final Container[] patternResultSlots;

    private final SimpleProgressValueContainerData[] progressData = new SimpleProgressValueContainerData[7];
    private final SimpleProgressValueContainerData[] maxProgressData = new SimpleProgressValueContainerData[7];
    private final SimpleEnergyValueContainerData[] energyConsumptionLeftData = new SimpleEnergyValueContainerData[7];
    private final SimpleBooleanValueContainerData[] hasEnoughEnergyData = new SimpleBooleanValueContainerData[7];
    private final SimpleBooleanValueContainerData[] ignoreNBTData = new SimpleBooleanValueContainerData[7];

    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleBooleanValueContainerData secondaryExtractModeData = new SimpleBooleanValueContainerData();
    private final SimpleBooleanValueContainerData allowOutputOverflowData = new SimpleBooleanValueContainerData();
    private final SimpleShortValueContainerData currentRecipeIndexData = new SimpleShortValueContainerData();
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public EliteAutoCrafterMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, inv.player.level().getBlockEntity(pos), new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        ), new Container[] {
                new SimpleContainer(9), new SimpleContainer(9), new SimpleContainer(9),
                new SimpleContainer(9), new SimpleContainer(9), new SimpleContainer(9),
                new SimpleContainer(9)
        }, new Container[] {
                new SimpleContainer(1), new SimpleContainer(1), new SimpleContainer(1),
                new SimpleContainer(1), new SimpleContainer(1), new SimpleContainer(1),
                new SimpleContainer(1)
        }, null);
    }

    public EliteAutoCrafterMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory,
                                Container[] patternSlots, Container[] patternResultSlots, ContainerData data) {
        super(
                EPMenuTypes.ELITE_AUTO_CRAFTER_MENU.get(), id,

                inv, blockEntity,
                EPBlocks.ELITE_AUTO_CRAFTER.get(),
                8, 160,

                upgradeModuleInventory, 5
        );

        this.patternSlots = patternSlots;
        this.patternResultSlots = patternResultSlots;

        ItemCapabilityMenuHelper.getEnergizedPowerItemStackHandlerCapability(this.level, this.blockEntity).ifPresent(itemHandler -> {
            for(int i = 0;i < 4;i++)
                for(int j = 0;j < 9;j++)
                    addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 9 * i + j, 8 + 18 * j, 75 + 18 * i));
        });

        for(int ri = 0;ri < 7;ri++) {
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
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 44 + i * 18, 35, this::isInUpgradeModuleView));


        for(int i = 0;i < 7;i++) {
            progressData[i] = new SimpleProgressValueContainerData();
            maxProgressData[i] = new SimpleProgressValueContainerData();
            energyConsumptionLeftData[i] = new SimpleEnergyValueContainerData();
            hasEnoughEnergyData[i] = new SimpleBooleanValueContainerData();
            ignoreNBTData[i] = new SimpleBooleanValueContainerData();
        }

        if(data == null) {
            for(int i = 0;i < 7;i++) {
                addDataSlots(progressData[i]);
                addDataSlots(maxProgressData[i]);
                addDataSlots(energyConsumptionLeftData[i]);
                addDataSlots(hasEnoughEnergyData[i]);
                addDataSlots(ignoreNBTData[i]);
            }

            addDataSlots(energyConsumptionPerTickData);
            addDataSlots(secondaryExtractModeData);
            addDataSlots(allowOutputOverflowData);
            addDataSlots(currentRecipeIndexData);
            addDataSlots(redstoneModeData);
            addDataSlots(comparatorModeData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
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
            //"+ 70": Ignore 7 * (3x3 crafting grid and result slot)
            if(!moveUpgradeModuleItemStackTo(sourceItem, 4 * 9 + 36 + 7 * (3*3 + 1), 4 * 9 + 36 + 7 * (3*3 + 1) + 5, player, 0, 4 * 9, false) &&
                    !moveItemStackTo(sourceItem, 4 * 9 + 9, 4 * 9 + 36, false)) {
                //"+9" instead of nothing: Do not allow adding to first 9 output item only slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 36) {
            //Tile inventory slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 36 + 7 * (3*3 + 1)) {
            return ItemStack.EMPTY;
        }else if(index < 4 * 9 + 36 + 7 * (3*3 + 1) + 5) {
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
