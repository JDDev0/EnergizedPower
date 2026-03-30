package me.jddev0.ep.item;

import me.jddev0.ep.component.CurrentItemStackComponent;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryCoalEngineItem extends EnergizedPowerEnergyItem implements ActivatableItem, WorkingItem {
    public static final long CAPACITY = ModConfigs.COMMON_INVENTORY_COAL_ENGINE_CAPACITY.getValue();
    public static final long MAX_EXTRACT = ModConfigs.COMMON_INVENTORY_COAL_ENGINE_TRANSFER_RATE.getValue();

    public static final double ENERGY_PRODUCTION_MULTIPLIER = ModConfigs.COMMON_INVENTORY_COAL_ENGINE_ENERGY_PRODUCTION_MULTIPLIER.getValue();

    public InventoryCoalEngineItem(Item.Properties props) {
        super(props, CAPACITY, 0, MAX_EXTRACT);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, tooltip, type);

        boolean active = isActive(stack);

        tooltip.accept(Component.translatable("tooltip.energizedpower.inventory_coal_engine.status").withStyle(ChatFormatting.GRAY).
                append(Component.translatable("tooltip.energizedpower.inventory_coal_engine.status." +
                        (active?"activated":"deactivated")).withStyle(active?ChatFormatting.GREEN:ChatFormatting.RED)));

        if(Minecraft.getInstance().hasShiftDown()) {
            long energyProductionLeft = getEnergyProductionLeft(stack);
            ItemStack item = getCurrentBurningItem(stack);
            if(energyProductionLeft > 0 && item != null) {
                tooltip.accept(Component.translatable("tooltip.energizedpower.inventory_coal_engine.txt.shift.currently_burning").
                        withStyle(ChatFormatting.GRAY).
                        append(item.getHoverName()));

                tooltip.accept(Component.translatable("tooltip.energizedpower.inventory_coal_engine.txt.shift.energy_production_left",
                                EnergyUtils.getEnergyWithPrefix(energyProductionLeft)).
                        withStyle(ChatFormatting.GRAY));
            }

            tooltip.accept(Component.translatable("tooltip.energizedpower.inventory_coal_engine.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            tooltip.accept(Component.translatable("tooltip.energizedpower.inventory_coal_engine.txt.shift.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    private void distributeEnergy(ItemStack itemStack, ServerLevel level, Inventory inventory, @Nullable EquipmentSlot slot) {
        List<EnergyStorage> consumerItems = new ArrayList<>();
        List<Long> consumerEnergyValues = new ArrayList<>();
        long consumptionSum = 0;
        for(int i = 0;i < inventory.getContainerSize();i++) {
            ItemStack testItemStack = inventory.getItem(i);

            if(!EnergyStorageUtil.isEnergyStorage(testItemStack))
                continue;

            EnergyStorage energyStorage = EnergyStorage.ITEM.find(testItemStack, ContainerItemContext.
                    ofPlayerSlot(inventory.player, ContainerStorage.of(inventory, null).getSlots().get(i)));
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

        List<Long> consumerEnergyDistributed = new ArrayList<>();
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
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, entity, slot);

        if(level.isClientSide())
            return;

        if(!(entity instanceof Player player))
            return;

        if(!isActive(itemStack))
            return;

        Inventory inventory = player.getInventory();

        distributeEnergy(itemStack, level, inventory, slot);

        long energyProductionLeft = getEnergyProductionLeft(itemStack);
        if(energyProductionLeft > 0) {
            int progress = getProgress(itemStack);
            int maxProgress = getMaxProgress(itemStack);
            ItemStack currentBurningItem = getCurrentBurningItem(itemStack);
            if(progress >= 0 && maxProgress > 0 && progress < maxProgress && currentBurningItem != null) {
                long energyProductionPerTick = energyProductionLeft / (maxProgress - progress);
                if(getEnergyCapacity(itemStack) - getEnergy(itemStack) < energyProductionPerTick) {
                    //Not enough energy storage for production
                    if(isWorking(itemStack))
                        itemStack.set(EPDataComponentTypes.WORKING, false);

                    return;
                }

                if(!isWorking(itemStack))
                    itemStack.set(EPDataComponentTypes.WORKING, true);

                setEnergy(itemStack, getEnergy(itemStack) + energyProductionPerTick);

                itemStack.set(EPDataComponentTypes.ENERGY_PRODUCTION_LEFT, energyProductionLeft - energyProductionPerTick);

                progress++;
                if(progress == maxProgress) {
                    resetProgress(itemStack);
                }else {
                    itemStack.set(EPDataComponentTypes.PROGRESS, progress);

                    return;
                }
            }else {
                resetProgress(itemStack);
            }
        }

        //Find and burn new fuel item

        //i: 0 - 8 -> Hotbar (Ignore)
        //"< main.size()": Ignore armor and offhand slots
        for(int i = 9;i < inventory.getNonEquipmentItems().size();i++) {
            ItemStack testItemStack = inventory.getItem(i);
            long energyProduction = level.fuelValues().burnDuration(testItemStack);
            if(energyProduction <= 0)
                continue;

            energyProduction = (long)(energyProduction * ENERGY_PRODUCTION_MULTIPLIER);

            itemStack.set(EPDataComponentTypes.ENERGY_PRODUCTION_LEFT, energyProduction);
            itemStack.set(EPDataComponentTypes.CURRENT_ITEM, new CurrentItemStackComponent(testItemStack));
            itemStack.set(EPDataComponentTypes.PROGRESS, 0);

            //Change max progress if item would output more than max extract
            if(energyProduction / 100 <= MAX_EXTRACT)
                itemStack.set(EPDataComponentTypes.MAX_PROGRESS, 100);
            else
                itemStack.set(EPDataComponentTypes.MAX_PROGRESS, (int)Math.ceil((double)energyProduction / MAX_EXTRACT));

            ItemStack newItemStack = testItemStack.copy();
            newItemStack.shrink(1);
            inventory.setItem(i, newItemStack);

            if(testItemStack.getCraftingRemainder() != null) {
                ItemStack craftingRemainingItem = ItemStackUtils.fromNullableItemStackTemplate(testItemStack.getCraftingRemainder());

                if(inventory.add(craftingRemainingItem))
                    player.drop(craftingRemainingItem, false);
            }

            break;
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if(level.isClientSide())
            return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);

        itemStack.set(EPDataComponentTypes.ACTIVE, !isActive(itemStack));

        return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
    }

    private void resetProgress(ItemStack itemStack) {
        itemStack.remove(EPDataComponentTypes.ENERGY_PRODUCTION_LEFT);
        itemStack.remove(EPDataComponentTypes.PROGRESS);
        itemStack.remove(EPDataComponentTypes.MAX_PROGRESS);
        itemStack.remove(EPDataComponentTypes.CURRENT_ITEM);
        itemStack.remove(EPDataComponentTypes.WORKING);
    }

    private int getProgress(ItemStack itemStack) {
        return itemStack.getOrDefault(EPDataComponentTypes.PROGRESS, -1);
    }

    private int getMaxProgress(ItemStack itemStack) {
        return itemStack.getOrDefault(EPDataComponentTypes.MAX_PROGRESS, -1);
    }

    private ItemStack getCurrentBurningItem(ItemStack itemStack) {
        CurrentItemStackComponent currentItem = itemStack.get(EPDataComponentTypes.CURRENT_ITEM);
        return currentItem == null?null:currentItem.getCurrentItem();
    }

    private long getEnergyProductionLeft(ItemStack itemStack) {
        return itemStack.getOrDefault(EPDataComponentTypes.ENERGY_PRODUCTION_LEFT, -1L);
    }

    @Override
    public boolean isActive(ItemStack itemStack) {
        return itemStack.getOrDefault(EPDataComponentTypes.ACTIVE, false);
    }

    @Override
    public boolean isWorking(ItemStack itemStack) {
        return itemStack.getOrDefault(EPDataComponentTypes.WORKING, false);
    }
}
