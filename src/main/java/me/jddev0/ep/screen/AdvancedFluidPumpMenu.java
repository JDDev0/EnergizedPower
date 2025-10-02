package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AdvancedFluidPumpBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
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
import net.minecraft.item.Items;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class AdvancedFluidPumpMenu extends UpgradableEnergyStorageMenu<AdvancedFluidPumpBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final SimpleProgressValueContainerData progressData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData maxProgressData = new SimpleProgressValueContainerData();
    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData energyConsumptionLeftData = new SimpleEnergyValueContainerData();
    private final SimpleBooleanValueContainerData hasEnoughEnergyData = new SimpleBooleanValueContainerData();
    private final SimpleIntegerValueContainerData xOffsetData = new SimpleIntegerValueContainerData();
    private final SimpleIntegerValueContainerData yOffsetData = new SimpleIntegerValueContainerData();
    private final SimpleIntegerValueContainerData zOffsetData = new SimpleIntegerValueContainerData();
    private final SimpleBooleanValueContainerData extractingFluidData = new SimpleBooleanValueContainerData();
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public AdvancedFluidPumpMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getEntityWorld().getBlockEntity(pos), inv, new SimpleInventory(1) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0) {
                    return stack.isOf(Items.COBBLESTONE);
                }

                return super.isValid(slot, stack);
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.EXTRACTION_RANGE,
                UpgradeModuleModifier.EXTRACTION_DEPTH
        ), null);
    }

    public AdvancedFluidPumpMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                                 UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(
                EPMenuTypes.ADVANCED_FLUID_PUMP_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.ADVANCED_FLUID_PUMP,
                35, 84,

                upgradeModuleInventory, 4
        );

        checkSize(inv, 1);

        addSlot(new ConstraintInsertSlot(inv, 0, 107, 35) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });

        for(int i = 0;i < upgradeModuleInventory.size();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 71 + i * 18, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addProperties(progressData);
            addProperties(maxProgressData);
            addProperties(energyConsumptionPerTickData);
            addProperties(energyConsumptionLeftData);
            addProperties(hasEnoughEnergyData);
            addProperties(xOffsetData);
            addProperties(yOffsetData);
            addProperties(zOffsetData);
            addProperties(extractingFluidData);
            addProperties(redstoneModeData);
            addProperties(comparatorModeData);
        }else {
            addProperties(data);
        }
    }

    public FluidStack getFluid(int tank) {
        return blockEntity.getFluid(tank);
    }

    public long getTankCapacity(int tank) {
        return blockEntity.getTankCapacity(tank);
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

    public BlockPos getTargetOffset() {
        return new BlockPos(xOffsetData.getValue(), yOffsetData.getValue(), zOffsetData.getValue());
    }

    public boolean isExtractingFluid() {
        return extractingFluidData.getValue();
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
            if(!insertItem(sourceItem, 4 * 9 + 1, 4 * 9 + 1 + 5, false) &&
                    !insertItem(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1 + 5) {
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
