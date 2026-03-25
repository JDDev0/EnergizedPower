package me.jddev0.ep.item;

import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.component.InventoryComponent;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.InventoryChargerMenu;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryChargerItem extends Item implements MenuProvider {
    public static final int SLOT_COUNT = ModConfigs.COMMON_INVENTORY_CHARGER_SLOT_COUNT.getValue();

    public static final boolean TRANSFER_RATE_LIMIT_ENABLED = ModConfigs.COMMON_INVENTORY_CHARGER_TRANSFER_RATE_LIMIT_ENABLED.getValue();
    public static final long TRANSFER_RATE_LIMIT = ModConfigs.COMMON_INVENTORY_CHARGER_TRANSFER_RATE_LIMIT.getValue();

    public InventoryChargerItem(Item.Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if(hand == InteractionHand.OFF_HAND)
            return InteractionResult.PASS;

        if(level.isClientSide())
            return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);

        player.openMenu(this);

        return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.inventory_charger");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new InventoryChargerMenu(id, inventory, getInventory(inventory.player.getItemInHand(InteractionHand.MAIN_HAND)));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        SimpleContainer inventory = getInventory(stack);

        return getCapacity(inventory) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        SimpleContainer inventory = getInventory(stack);

        return Math.round(getEnergy(inventory) * 13.f / getCapacity(inventory));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        SimpleContainer inventory = getInventory(stack);

        float f = Math.max(0.f, getEnergy(inventory) / (float)getCapacity(inventory));
        return Mth.hsvToRgb(f * .33f, 1.f, 1.f);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag type) {
        SimpleContainer inventory = getInventory(stack);

        long energy = getEnergy(inventory);
        long capacity = getCapacity(inventory);
        long maxTransfer = getMaxTransfer(inventory, null);

        tooltip.accept(Component.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(energy), EnergyUtils.getEnergyWithPrefix(capacity)).
                withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                        EnergyUtils.getEnergyWithPrefix(maxTransfer)).
                withStyle(ChatFormatting.GRAY));

        if(Minecraft.getInstance().hasShiftDown()) {
            tooltip.accept(Component.translatable("tooltip.energizedpower.inventory_charger.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    private void distributeEnergy(ItemStack itemStack, ServerLevel level, Inventory inventory, @Nullable EquipmentSlot slot) {
        SimpleContainer inventoryChargerInventory = getInventory(itemStack);

        List<EnergyStorage> consumerItems = new ArrayList<>();
        List<Long> consumerEnergyValues = new ArrayList<>();
        long consumptionSum = 0;
        for(int i = 0;i < inventory.getContainerSize();i++) {
            ItemStack testItemStack = inventory.getItem(i);

            if(!EnergyStorageUtil.isEnergyStorage(testItemStack))
                continue;

            EnergyStorage energyStorage = EnergyStorage.ITEM.find(testItemStack, ContainerItemContext.
                    ofPlayerSlot(inventory.player, InventoryStorage.of(inventory, null).getSlots().get(i)));
            if(energyStorage == null)
                continue;

            if(!energyStorage.supportsInsertion())
                continue;

            try(Transaction transaction = Transaction.openOuter()) {
                long received = energyStorage.insert(getMaxTransfer(inventoryChargerInventory, transaction), transaction);

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

        long consumptionLeft = Math.min(getMaxTransfer(inventoryChargerInventory, null), consumptionSum);

        extractEnergyFromBatteries(consumptionLeft, inventoryChargerInventory);

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

    public void extractEnergyFromBatteries(long energyProductionLeft, Container inventory) {
        List<EnergyStorage> energyProduction = new ArrayList<>();
        List<Long> energyProductionValues = new ArrayList<>();

        for(int i = 0;i < inventory.getContainerSize();i++) {
            ItemStack stack = inventory.getItem(i);

            EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(inventory, null).getSlots().get(i)));
            if(energyStorage == null)
                continue;

            try(Transaction transaction = Transaction.openOuter()) {
                long extracted = energyStorage.extract(energyStorage.getCapacity(), transaction);

                if(extracted <= 0)
                    continue;

                energyProduction.add(energyStorage);
                energyProductionValues.add(extracted);
            }
        }

        List<Long> energyProductionDistributed = new ArrayList<>();
        for(int i = 0;i < energyProduction.size();i++)
            energyProductionDistributed.add(0L);

        long productionLeft = energyProductionLeft;
        int divisor = energyProduction.size();
        outer:
        while(productionLeft > 0) {
            long productionPerProducer = productionLeft / divisor;
            if(productionPerProducer == 0) {
                divisor = Math.max(1, divisor - 1);
                productionPerProducer = productionLeft / divisor;
            }

            for(int i = 0;i < energyProductionValues.size();i++) {
                long productionDistributed = energyProductionDistributed.get(i);
                long productionOfProducerLeft = energyProductionValues.get(i) - productionDistributed;

                long productionDistributedNew = Math.min(productionPerProducer, Math.min(productionOfProducerLeft, productionLeft));
                energyProductionDistributed.set(i, productionDistributed + productionDistributedNew);
                productionLeft -= productionDistributedNew;
                if(productionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < energyProduction.size();i++) {
            long energy = energyProductionDistributed.get(i);
            if(energy > 0)
                try(Transaction transaction = Transaction.openOuter()) {
                    energyProduction.get(i).extract(energy, transaction);
                    transaction.commit();
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

        Inventory inventory = player.getInventory();

        //Do not distribute energy if opened
        if(player.containerMenu instanceof InventoryChargerMenu)
            return;

        distributeEnergy(itemStack, level, inventory, slot);
    }

    public static SimpleContainer getInventory(ItemStack itemStack) {
        InventoryComponent inventory = itemStack.get(EPDataComponentTypes.INVENTORY);

        if(inventory != null) {
            NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
            for(int i = 0;i < items.size();i++) {
                if(inventory.size() <= i)
                    break;

                items.set(i, inventory.get(i));
            }
            return new SimpleContainer(items.toArray(new ItemStack[0])) {
                @Override
                public void setChanged() {
                    super.setChanged();

                    itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(items));
                }

                @Override
                public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                    if(slot >= 0 && slot < getContainerSize()) {
                        if(!EnergyStorageUtil.isEnergyStorage(stack))
                            return false;

                        EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                        if(energyStorage == null)
                            return false;

                        return energyStorage.supportsExtraction();
                    }

                    return super.canPlaceItem(slot, stack);
                }

                @Override
                public boolean stillValid(Player player) {
                    return super.stillValid(player) && player.getItemInHand(InteractionHand.MAIN_HAND) == itemStack;
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            };
        }

        return new SimpleContainer(SLOT_COUNT) {
            @Override
            public void setChanged() {
                super.setChanged();

                itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(items));
            }

            @Override
            public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < getContainerSize()) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(energyStorage == null)
                        return false;

                    return energyStorage.supportsExtraction();
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public boolean stillValid(Player player) {
                return super.stillValid(player) && player.getItemInHand(InteractionHand.MAIN_HAND) == itemStack;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    public static long getEnergy(Container inventory) {
        long energySum = 0;

        for(int i = 0;i < inventory.getContainerSize();i++) {
            ItemStack stack = inventory.getItem(i);

            EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
            if(energyStorage == null)
                continue;

            long value = energyStorage.getAmount();

            //Prevent overflow
            if(Math.max(0, energySum) + Math.max(0, value) < 0)
                return Long.MAX_VALUE;

            energySum += value;
        }

        return energySum;
    }

    public static long getCapacity(Container inventory) {
        long capacitySum = 0;

        for(int i = 0;i < inventory.getContainerSize();i++) {
            ItemStack stack = inventory.getItem(i);

            EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
            if(energyStorage == null)
                continue;

            long value = energyStorage.getCapacity();

            //Prevent overflow
            if(Math.max(0, capacitySum) + Math.max(0, value) < 0)
                return Long.MAX_VALUE;

            capacitySum += value;
        }

        return capacitySum;
    }

    public static long getMaxTransfer(Container inventory, @Nullable Transaction outerTransaction) {
        long maxTransferSum = 0;

        for(int i = 0;i < inventory.getContainerSize();i++) {
            ItemStack stack = inventory.getItem(i);

            EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(inventory, null).getSlots().get(i)));
            if(energyStorage == null)
                continue;

            long value;
            try(Transaction transaction = Transaction.openNested(outerTransaction)) {
                value = energyStorage.extract(energyStorage.getCapacity(), transaction);
            }

            //Prevent overflow
            if(Math.max(0, maxTransferSum) + Math.max(0, value) < 0)
                return Long.MAX_VALUE;

            maxTransferSum += value;
        }

        if(TRANSFER_RATE_LIMIT_ENABLED)
            return Math.min(maxTransferSum, TRANSFER_RATE_LIMIT);

        return maxTransferSum;
    }
}
