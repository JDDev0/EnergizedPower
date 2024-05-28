package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.ChargerBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageConsumerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
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
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public class ChargerMenu extends UpgradableEnergyStorageMenu<ChargerBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final PropertyDelegate data;

    public ChargerMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv, new SimpleInventory(1) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(inv.player.getWorld() == null || RecipeUtils.isIngredientOfAny(inv.player.getWorld(), ChargerRecipe.Type.INSTANCE, stack))
                    return true;

                if(slot == 0) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(energyStorage == null)
                        return false;

                    return energyStorage.supportsInsertion();
                }

                return super.isValid(slot, stack);
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), new ArrayPropertyDelegate(6));
    }

    public ChargerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                       UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(
                ModMenuTypes.CHARGER_MENU, id,

                playerInventory, blockEntity,
                ModBlocks.CHARGER,

                upgradeModuleInventory, 1
        );

        checkSize(inv, 1);
        checkDataCount(data, 6);
        this.data = data;

        addSlot(new ConstraintInsertSlot(inv, 0, 80, 35) {
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
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1), (short)data.get(2), (short)data.get(3));
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(4));
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return ComparatorMode.fromIndex(data.get(5));
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
            //Allow only 1 item
            if(!insertItem(sourceItem, 4 * 9 + 1, 4 * 9 + 1 + 1, false) &&
                    (slots.get(4 * 9).hasStack() || !insertItem(sourceItem, 4 * 9, 4 * 9 + 1, false))) {
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
