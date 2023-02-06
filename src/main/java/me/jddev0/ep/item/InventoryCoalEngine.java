package me.jddev0.ep.item;

import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.LinkedList;
import java.util.List;

public class InventoryCoalEngine extends EnergizedPowerEnergyItem implements ActivatableItem, WorkingItem {
    public static final long CAPACITY = 2048;
    public static final long MAX_EXTRACT = 256;

    public InventoryCoalEngine(FabricItemSettings props) {
        super(props, CAPACITY, 0, MAX_EXTRACT);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World level, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(itemStack, level, tooltip, context);

        boolean active = isActive(itemStack);

        tooltip.add(Text.translatable("tooltip.energizedpower.inventory_coal_engine.status").formatted(Formatting.GRAY).
                append(Text.translatable("tooltip.energizedpower.inventory_coal_engine.status." +
                        (active?"activated":"deactivated")).formatted(active?Formatting.GREEN:Formatting.RED)));

        if(Screen.hasShiftDown()) {
            int energyProductionLeft = getEnergyProductionLeft(itemStack);
            ItemStack item = getCurrentBurningItem(itemStack);
            if(energyProductionLeft > 0 && item != null) {
                tooltip.add(Text.translatable("tooltip.energizedpower.inventory_coal_engine.txt.shift.currently_burning").
                        formatted(Formatting.GRAY).
                        append(item.getName()));

                tooltip.add(Text.translatable("tooltip.energizedpower.inventory_coal_engine.txt.shift.energy_production_left",
                                EnergyUtils.getEnergyWithPrefix(energyProductionLeft)).
                        formatted(Formatting.GRAY));
            }

            tooltip.add(Text.translatable("tooltip.energizedpower.inventory_coal_engine.txt.shift.1").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
            tooltip.add(Text.translatable("tooltip.energizedpower.inventory_coal_engine.txt.shift.2").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    private void distributeEnergy(ItemStack itemStack, World level, PlayerInventory inventory, int slot, boolean selected) {
        List<EnergyStorage> consumerItems = new LinkedList<>();
        List<Long> consumerEnergyValues = new LinkedList<>();
        int consumptionSum = 0;
        for(int i = 0;i < inventory.size();i++) {
            if(i == slot)
                continue;

            ItemStack testItemStack = inventory.getStack(i);

            if(!EnergyStorageUtil.isEnergyStorage(testItemStack))
                continue;

            EnergyStorage energyStorage = EnergyStorage.ITEM.find(testItemStack, ContainerItemContext.
                    ofPlayerSlot(inventory.player, InventoryStorage.of(inventory, null).getSlots().get(i)));
            if(energyStorage == null)
                continue;

            if(!energyStorage.supportsInsertion())
                continue;

            try(Transaction transaction = Transaction.openOuter()) {
                long received = energyStorage.insert(Math.min(MAX_EXTRACT, getEnergy(itemStack)), transaction);

                if(received <= 0)
                    continue;

                consumptionSum += received;
                consumerItems.add(energyStorage);
                consumerEnergyValues.add(received);
            }
        }

        List<Long> consumerEnergyDistributed = new LinkedList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0L);

        long consumptionLeft = Math.min(MAX_EXTRACT, Math.min(getEnergy(itemStack), consumptionSum));
        setEnergy(itemStack, getEnergy(itemStack) - consumptionLeft);

        int divisor = consumerItems.size();
        outer:
        while(consumptionLeft > 0) {
            long consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < consumerEnergyValues.size();i++) {
                long consumptionDistributed = consumerEnergyDistributed.get(i);
                long consumptionOfConsumerLeft = consumerEnergyValues.get(i) - consumptionDistributed;

                long consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                consumerEnergyDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < consumerItems.size();i++) {
            long energy = consumerEnergyDistributed.get(i);
            if(energy > 0) {
                try(Transaction transaction = Transaction.openOuter()) {
                    consumerItems.get(i).insert(energy, transaction);
                    transaction.commit();
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemStack, level, entity, slot, selected);

        if(level.isClient())
            return;

        if(!(entity instanceof PlayerEntity player))
            return;

        if(!isActive(itemStack))
            return;

        PlayerInventory inventory = player.getInventory();

        distributeEnergy(itemStack, level, inventory, slot, selected);

        int energyProductionLeft = getEnergyProductionLeft(itemStack);
        if(energyProductionLeft > 0) {
            int progress = getProgress(itemStack);
            int maxProgress = getMaxProgress(itemStack);
            ItemStack getCurrentBurningItem = getCurrentBurningItem(itemStack);
            if(progress >= 0 && maxProgress > 0 && progress < maxProgress && getCurrentBurningItem != null) {
                int energyProductionPerTick = energyProductionLeft / (maxProgress - progress);
                if(getEnergyCapacity(itemStack) - getEnergy(itemStack) < energyProductionPerTick) {
                    //Not enough energy storage for production
                    if(isWorking(itemStack))
                        itemStack.getOrCreateNbt().putBoolean("working", false);

                    return;
                }

                if(!isWorking(itemStack))
                    itemStack.getOrCreateNbt().putBoolean("working", true);

                setEnergy(itemStack, getEnergy(itemStack) + energyProductionPerTick);

                itemStack.getOrCreateNbt().putInt("energy_production_left", energyProductionLeft - energyProductionPerTick);

                progress++;
                if(progress == maxProgress) {
                    resetProgress(itemStack);
                }else {
                    itemStack.getOrCreateNbt().putInt("progress", progress);

                    return;
                }
            }else {
                resetProgress(itemStack);
            }
        }

        //Find and burn new fuel item

        //i: 0 - 8 -> Hotbar (Ignore)
        for(int i = 9;i < inventory.size();i++) {
            if(i == slot)
                continue;

            ItemStack testItemStack = inventory.getStack(i);
            int energyProduction = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(testItemStack.getItem(), -1);
            if(energyProduction <= 0)
                continue;

            itemStack.getOrCreateNbt().putInt("energy_production_left", energyProduction);
            itemStack.getOrCreateNbt().put("item", testItemStack.writeNbt(new NbtCompound()));
            itemStack.getOrCreateNbt().putInt("progress", 0);

            //Change max progress if item would output more than max extract
            if(energyProduction / 100 <= MAX_EXTRACT)
                itemStack.getOrCreateNbt().putInt("max_progress", 100);
            else
                itemStack.getOrCreateNbt().putInt("max_progress", (int)Math.ceil((float)energyProduction / MAX_EXTRACT));

            ItemStack newItemStack = testItemStack.copy();
            newItemStack.decrement(1);
            inventory.setStack(i, newItemStack);

            if(testItemStack.getRecipeRemainder() != null) {
                ItemStack craftingRemainingItem = testItemStack.getRecipeRemainder();

                if(inventory.insertStack(craftingRemainingItem))
                    player.dropItem(craftingRemainingItem, false);
            }

            break;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if(level.isClient())
            return TypedActionResult.success(itemStack);

        itemStack.getOrCreateNbt().putBoolean("active", !isActive(itemStack));

        return TypedActionResult.success(itemStack);
    }

    private void resetProgress(ItemStack itemStack) {
        itemStack.getOrCreateNbt().remove("energy_production_left");
        itemStack.getOrCreateNbt().remove("progress");
        itemStack.getOrCreateNbt().remove("max_progress");
        itemStack.getOrCreateNbt().remove("item");

        if(isWorking(itemStack))
            itemStack.getOrCreateNbt().putBoolean("working", false);
    }

    private int getProgress(ItemStack itemStack) {
        if(!itemStack.hasNbt())
            return -1;

        NbtCompound nbt = itemStack.getNbt();
        if(nbt == null || !nbt.contains("progress"))
            return -1;

        return nbt.getInt("progress");
    }

    private int getMaxProgress(ItemStack itemStack) {
        if(!itemStack.hasNbt())
            return -1;

        NbtCompound nbt = itemStack.getNbt();
        if(nbt == null || !nbt.contains("max_progress"))
            return -1;

        return nbt.getInt("max_progress");
    }

    private ItemStack getCurrentBurningItem(ItemStack itemStack) {
        if(!itemStack.hasNbt())
            return null;

        NbtCompound nbt = itemStack.getNbt();
        if(nbt == null || !nbt.contains("item"))
            return null;

        return ItemStack.fromNbt(nbt.getCompound("item"));
    }

    private int getEnergyProductionLeft(ItemStack itemStack) {
        if(!itemStack.hasNbt())
            return -1;

        NbtCompound nbt = itemStack.getNbt();
        if(nbt == null || !nbt.contains("energy_production_left"))
            return -1;

        return nbt.getInt("energy_production_left");
    }

    @Override
    public boolean isActive(ItemStack itemStack) {
        if(!itemStack.hasNbt())
            return false;

        NbtCompound nbt = itemStack.getNbt();
        if(nbt == null || !nbt.contains("active"))
            return false;

        return nbt.getBoolean("active");
    }

    @Override
    public boolean isWorking(ItemStack itemStack) {
        if(!itemStack.hasNbt())
            return false;

        NbtCompound nbt = itemStack.getNbt();
        if(nbt == null || !nbt.contains("working"))
            return false;

        return nbt.getBoolean("working");
    }
}
