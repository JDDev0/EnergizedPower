package me.jddev0.ep.item;

import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.component.InventoryComponent;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.InventoryChargerMenu;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryChargerItem extends Item implements NamedScreenHandlerFactory {
    public static final int SLOT_COUNT = ModConfigs.COMMON_INVENTORY_CHARGER_SLOT_COUNT.getValue();

    public static final boolean TRANSFER_RATE_LIMIT_ENABLED = ModConfigs.COMMON_INVENTORY_CHARGER_TRANSFER_RATE_LIMIT_ENABLED.getValue();
    public static final long TRANSFER_RATE_LIMIT = ModConfigs.COMMON_INVENTORY_CHARGER_TRANSFER_RATE_LIMIT.getValue();

    public InventoryChargerItem(Item.Settings props) {
        super(props);
    }

    @Override
    public ActionResult use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if(hand == Hand.OFF_HAND)
            return ActionResult.PASS;

        if(level.isClient())
            return ActionResult.SUCCESS.withNewHandStack(itemStack);

        player.openHandledScreen(this);

        return ActionResult.SUCCESS.withNewHandStack(itemStack);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.inventory_charger");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new InventoryChargerMenu(id, inventory, getInventory(inventory.player.getStackInHand(Hand.MAIN_HAND)));
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        SimpleInventory inventory = getInventory(stack);

        return getCapacity(inventory) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        SimpleInventory inventory = getInventory(stack);

        return Math.round(getEnergy(inventory) * 13.f / getCapacity(inventory));
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        SimpleInventory inventory = getInventory(stack);

        float f = Math.max(0.f, getEnergy(inventory) / (float)getCapacity(inventory));
        return MathHelper.hsvToRgb(f * .33f, 1.f, 1.f);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        SimpleInventory inventory = getInventory(stack);

        long energy = getEnergy(inventory);
        long capacity = getCapacity(inventory);
        long maxTransfer = getMaxTransfer(inventory, null);

        tooltip.accept(Text.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(energy), EnergyUtils.getEnergyWithPrefix(capacity)).
                formatted(Formatting.GRAY));
        tooltip.accept(Text.translatable("tooltip.energizedpower.transfer_rate.txt",
                        EnergyUtils.getEnergyWithPrefix(maxTransfer)).
                formatted(Formatting.GRAY));

        if(MinecraftClient.getInstance().isShiftPressed()) {
            tooltip.accept(Text.translatable("tooltip.energizedpower.inventory_charger.txt.shift.1").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
        }else {
            tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    private void distributeEnergy(ItemStack itemStack, ServerWorld level, PlayerInventory inventory, @Nullable EquipmentSlot slot) {
        SimpleInventory inventoryChargerInventory = getInventory(itemStack);

        List<EnergyStorage> consumerItems = new ArrayList<>();
        List<Long> consumerEnergyValues = new ArrayList<>();
        long consumptionSum = 0;
        for(int i = 0;i < inventory.size();i++) {
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

    public void extractEnergyFromBatteries(long energyProductionLeft, Inventory inventory) {
        List<EnergyStorage> energyProduction = new ArrayList<>();
        List<Long> energyProductionValues = new ArrayList<>();

        for(int i = 0;i < inventory.size();i++) {
            ItemStack stack = inventory.getStack(i);

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
    public void inventoryTick(ItemStack itemStack, ServerWorld level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, entity, slot);

        if(level.isClient())
            return;

        if(!(entity instanceof PlayerEntity player))
            return;

        PlayerInventory inventory = player.getInventory();

        //Do not distribute energy if opened
        if(player.currentScreenHandler instanceof InventoryChargerMenu)
            return;

        distributeEnergy(itemStack, level, inventory, slot);
    }

    public static SimpleInventory getInventory(ItemStack itemStack) {
        InventoryComponent inventory = itemStack.get(EPDataComponentTypes.INVENTORY);

        if(inventory != null) {
            DefaultedList<ItemStack> items = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
            for(int i = 0;i < items.size();i++) {
                if(inventory.size() <= i)
                    break;

                items.set(i, inventory.get(i));
            }
            return new SimpleInventory(items.toArray(new ItemStack[0])) {
                @Override
                public void markDirty() {
                    super.markDirty();

                    itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(heldStacks));
                }

                @Override
                public boolean isValid(int slot, @NotNull ItemStack stack) {
                    if(slot >= 0 && slot < size()) {
                        if(!EnergyStorageUtil.isEnergyStorage(stack))
                            return false;

                        EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                        if(energyStorage == null)
                            return false;

                        return energyStorage.supportsExtraction();
                    }

                    return super.isValid(slot, stack);
                }

                @Override
                public boolean canPlayerUse(PlayerEntity player) {
                    return super.canPlayerUse(player) && player.getStackInHand(Hand.MAIN_HAND) == itemStack;
                }

                @Override
                public int getMaxCountPerStack() {
                    return 1;
                }
            };
        }

        return new SimpleInventory(SLOT_COUNT) {
            @Override
            public void markDirty() {
                super.markDirty();

                itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(heldStacks));
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < size()) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(energyStorage == null)
                        return false;

                    return energyStorage.supportsExtraction();
                }

                return super.isValid(slot, stack);
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return super.canPlayerUse(player) && player.getStackInHand(Hand.MAIN_HAND) == itemStack;
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }
        };
    }

    public static long getEnergy(Inventory inventory) {
        long energySum = 0;

        for(int i = 0;i < inventory.size();i++) {
            ItemStack stack = inventory.getStack(i);

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

    public static long getCapacity(Inventory inventory) {
        long capacitySum = 0;

        for(int i = 0;i < inventory.size();i++) {
            ItemStack stack = inventory.getStack(i);

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

    public static long getMaxTransfer(Inventory inventory, @Nullable Transaction outerTransaction) {
        long maxTransferSum = 0;

        for(int i = 0;i < inventory.size();i++) {
            ItemStack stack = inventory.getStack(i);

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
