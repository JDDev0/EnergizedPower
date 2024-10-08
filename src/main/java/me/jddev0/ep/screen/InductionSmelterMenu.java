package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.InductionSmelterBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageConsumerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import me.jddev0.ep.util.ByteUtils;
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

import java.util.Arrays;

public class InductionSmelterMenu extends UpgradableEnergyStorageMenu<InductionSmelterBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final PropertyDelegate data;

    public InductionSmelterMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, new SimpleInventory(5) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> inv.player.getWorld().getRecipeManager().
                            listAllOfType(AlloyFurnaceRecipe.Type.INSTANCE).stream().
                            map(AlloyFurnaceRecipe::getInputs).anyMatch(inputs ->
                                    Arrays.stream(inputs).map(IngredientWithCount::input).
                                            anyMatch(ingredient -> ingredient.test(stack)));
                    case 3, 4 -> false;
                    default -> false;
                };
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), new ArrayPropertyDelegate(11));
    }

    public InductionSmelterMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                                UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(
                EPMenuTypes.INDUCTION_SMELTER_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.INDUCTION_SMELTER,

                upgradeModuleInventory, 3
        );

        checkSize(inv, 5);
        checkDataCount(data, 11);
        this.data = data;

        addSlot(new ConstraintInsertSlot(inv, 0, 36, 37) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 1, 57, 34) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 2, 78, 37) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 3, 141, 25) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 4, 141, 49) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });

        for(int i = 0;i < upgradeModuleInventory.size();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 62 + i * 18, 35, this::isInUpgradeModuleView));

        addProperties(this.data);
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(4), (short)data.get(5), (short)data.get(6), (short)data.get(7));
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1)) > 0;
    }

    public boolean isCrafting() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1)) > 0 && data.get(8) == 1;
    }

    public int getScaledProgressArrowSize() {
        int progress = ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1));
        int maxProgress = ByteUtils.from2ByteChunks((short)data.get(2), (short)data.get(3));
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
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
    public ItemStack transferSlot(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory, Merge into tile inventory
            if(!insertMaxCount1Item(sourceItem, 4 * 9 + 5, 4 * 9 + 5 + 3, false) &&
                    !insertItem(sourceItem, 4 * 9, 4 * 9 + 3, false)) {
                //"+3" instead of "+5": Do not allow adding to output slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 5 + 3) {
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
