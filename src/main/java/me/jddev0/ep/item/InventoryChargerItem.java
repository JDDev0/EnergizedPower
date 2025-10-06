package me.jddev0.ep.item;

import me.jddev0.ep.component.InventoryComponent;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.integration.curios.CuriosCompatUtils;
import me.jddev0.ep.screen.InventoryChargerMenu;
import me.jddev0.ep.util.CapabilityUtil;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryChargerItem extends Item implements MenuProvider {
    public static final int SLOT_COUNT = ModConfigs.COMMON_INVENTORY_CHARGER_SLOT_COUNT.getValue();

    public static final boolean TRANSFER_RATE_LIMIT_ENABLED = ModConfigs.COMMON_INVENTORY_CHARGER_TRANSFER_RATE_LIMIT_ENABLED.getValue();
    public static final int TRANSFER_RATE_LIMIT = ModConfigs.COMMON_INVENTORY_CHARGER_TRANSFER_RATE_LIMIT.getValue();

    public InventoryChargerItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(interactionHand == InteractionHand.OFF_HAND)
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
        return new InventoryChargerMenu(id, inventory, getInventory(inventory.getSelectedItem()));
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
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        SimpleContainer inventory = getInventory(itemStack);

        int energy = getEnergy(inventory);
        int capacity = getCapacity(inventory);
        int maxTransfer = getMaxTransfer(inventory, null);

        components.accept(Component.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(energy), EnergyUtils.getEnergyWithPrefix(capacity)).
                withStyle(ChatFormatting.GRAY));
        components.accept(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                        EnergyUtils.getEnergyWithPrefix(maxTransfer)).
                withStyle(ChatFormatting.GRAY));

        if(Minecraft.getInstance().hasShiftDown()) {
            components.accept(Component.translatable("tooltip.energizedpower.inventory_charger.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    private void distributeEnergy(ItemStack itemStack, Level level, Inventory inventory, @Nullable EquipmentSlot slot) {
        SimpleContainer inventoryChargerInventory = getInventory(itemStack);

        List<EnergyHandler> consumerItems = new ArrayList<>();
        List<Integer> consumerEnergyValues = new ArrayList<>();
        int consumptionSum = 0;
        //Limit the size to main + armor + offhand
        for(int i = 0;i < Inventory.SLOT_BODY_ARMOR;i++) {
            ItemStack testItemStack = inventory.getItem(i);

            EnergyHandler energyStorage = testItemStack.getCapability(Capabilities.Energy.ITEM,
                    ItemAccess.forPlayerSlot(inventory.player, i));
            if(energyStorage == null || !CapabilityUtil.canInsert(energyStorage))
                continue;

            try(Transaction transaction = Transaction.open(null)) {
                int received = energyStorage.insert(getMaxTransfer(inventoryChargerInventory, transaction), transaction);

                if(received <= 0)
                    continue;

                consumptionSum += received;
                consumerItems.add(energyStorage);
                consumerEnergyValues.add(received);
            }
        }

        /* TODO FIX CURIOS INTEGRATION
        List<ItemStack> curiosItemStacks = CuriosCompatUtils.getCuriosItemStacks(inventory);
        for(ItemStack testItemStack:curiosItemStacks)
            consumptionSum += addConsumerEnergyItem(consumerItems, consumerEnergyValues, itemStack, testItemStack, inventoryChargerInventory);*/

        List<Integer> consumerEnergyDistributed = new ArrayList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0);

        int consumptionLeft = Math.min(getMaxTransfer(inventoryChargerInventory, null), consumptionSum);

        extractEnergyFromBatteries(consumptionLeft, inventoryChargerInventory);

        int divisor = consumerItems.size();
        outer:
        while(consumptionLeft > 0) {
            int consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < consumerEnergyValues.size();i++) {
                int consumptionDistributed = consumerEnergyDistributed.get(i);
                int consumptionOfConsumerLeft = consumerEnergyValues.get(i) - consumptionDistributed;

                int consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                consumerEnergyDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < consumerItems.size();i++) {
            int energy = consumerEnergyDistributed.get(i);
            if(energy > 0) {
                try(Transaction transaction = Transaction.open(null)) {
                    consumerItems.get(i).insert(energy, transaction);
                    transaction.commit();
                }
            }
        }

        //Fix for energy is not extracted from batteries
        inventoryChargerInventory.setChanged();
    }

    public void extractEnergyFromBatteries(int energyProductionLeft, SimpleContainer inventory) {
        List<EnergyHandler> energyProduction = new ArrayList<>();
        List<Integer> energyProductionValues = new ArrayList<>();

        for(int i = 0;i < inventory.getContainerSize();i++) {
            ItemStack stack = inventory.getItem(i);

            EnergyHandler energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forHandlerIndexStrict(
                    VanillaContainerWrapper.of(inventory), i));
            if(energyStorage == null)
                continue;

            try(Transaction transaction = Transaction.open(null)) {
                int extracted = energyStorage.extract(energyStorage.getCapacityAsInt(), transaction);

                if(extracted <= 0)
                    continue;

                energyProduction.add(energyStorage);
                energyProductionValues.add(extracted);
            }
        }

        List<Integer> energyProductionDistributed = new ArrayList<>();
        for(int i = 0;i < energyProduction.size();i++)
            energyProductionDistributed.add(0);

        int productionLeft = energyProductionLeft;
        int divisor = energyProduction.size();
        outer:
        while(productionLeft > 0) {
            int productionPerProducer = productionLeft / divisor;
            if(productionPerProducer == 0) {
                divisor = Math.max(1, divisor - 1);
                productionPerProducer = productionLeft / divisor;
            }

            for(int i = 0;i < energyProductionValues.size();i++) {
                int productionDistributed = energyProductionDistributed.get(i);
                int productionOfProducerLeft = energyProductionValues.get(i) - productionDistributed;

                int productionDistributedNew = Math.min(productionPerProducer, Math.min(productionOfProducerLeft, productionLeft));
                energyProductionDistributed.set(i, productionDistributed + productionDistributedNew);
                productionLeft -= productionDistributedNew;
                if(productionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < energyProduction.size();i++) {
            int energy = energyProductionDistributed.get(i);
            if(energy > 0)
                try(Transaction transaction = Transaction.open(null)) {
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

        if(!(entity instanceof Player))
            return;

        Player player = (Player)entity;
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

                    NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
                    for(int i = 0;i < getContainerSize();i++)
                        items.set(i, getItem(i));

                    itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(items));
                }

                @Override
                public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                    if(slot >= 0 && slot < getContainerSize()) {
                        EnergyHandler energyStorage = CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Energy.ITEM, stack);
                        return energyStorage != null && CapabilityUtil.canExtract(energyStorage);
                    }

                    return super.canPlaceItem(slot, stack);
                }

                @Override
                public boolean stillValid(Player player) {
                    return super.stillValid(player) && player.getInventory().getSelectedItem() == itemStack;
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

                NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
                for(int i = 0;i < getContainerSize();i++)
                    items.set(i, getItem(i));

                itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(items));
            }

            @Override
            public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < getContainerSize()) {
                    EnergyHandler energyStorage = CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Energy.ITEM, stack);
                    return energyStorage != null && CapabilityUtil.canExtract(energyStorage);
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public boolean stillValid(Player player) {
                return super.stillValid(player) && player.getInventory().getSelectedItem() == itemStack;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    public static int getEnergy(Container inventory) {
        int energySum = 0;

        for(int i = 0;i < inventory.getContainerSize();i++) {
            ItemStack stack = inventory.getItem(i);

            EnergyHandler energyStorage = CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Energy.ITEM, stack);
            if(energyStorage == null)
                continue;

            int value = energyStorage.getAmountAsInt();

            //Prevent overflow
            if(energySum + value != (long)energySum + value)
                return Integer.MAX_VALUE;

            energySum += value;
        }

        return energySum;
    }

    public static int getCapacity(Container inventory) {
        int capacitySum = 0;

        for(int i = 0;i < inventory.getContainerSize();i++) {
            ItemStack stack = inventory.getItem(i);

            EnergyHandler energyStorage = CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Energy.ITEM, stack);
            if(energyStorage == null)
                continue;

            int value = energyStorage.getCapacityAsInt();

            //Prevent overflow
            if(capacitySum + value != (long)capacitySum + value)
                return Integer.MAX_VALUE;

            capacitySum += value;
        }

        return capacitySum;
    }

    public static int getMaxTransfer(SimpleContainer inventory, @Nullable Transaction outerTransaction) {
        int maxTransferSum = 0;

        for(int i = 0;i < inventory.getContainerSize();i++) {
            ItemStack stack = inventory.getItem(i);

            EnergyHandler energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forHandlerIndexStrict(
                    VanillaContainerWrapper.of(inventory), i));
            if(energyStorage == null)
                continue;

            int value;
            try(Transaction transaction = Transaction.open(outerTransaction)) {
                value = energyStorage.extract(energyStorage.getCapacityAsInt(), transaction);
            }

            //Prevent overflow
            if(maxTransferSum + value != (long)maxTransferSum + value)
                return Integer.MAX_VALUE;

            maxTransferSum += value;
        }

        if(TRANSFER_RATE_LIMIT_ENABLED)
            return Math.min(maxTransferSum, TRANSFER_RATE_LIMIT);

        return maxTransferSum;
    }
}
