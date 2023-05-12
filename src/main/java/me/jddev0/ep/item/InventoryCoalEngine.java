package me.jddev0.ep.item;

import me.jddev0.ep.energy.ExtractOnlyEnergyStorage;
import me.jddev0.ep.integration.curios.CuriosCompatUtils;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class InventoryCoalEngine extends EnergizedPowerEnergyItem implements ActivatableItem, WorkingItem {
    public static final int CAPACITY = 2048;
    public static final int MAX_EXTRACT = 256;

    public InventoryCoalEngine(Properties props) {
        super(props, () -> new ExtractOnlyEnergyStorage(0, CAPACITY, MAX_EXTRACT));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);

        boolean active = isActive(itemStack);

        components.add(new TranslatableComponent("tooltip.energizedpower.inventory_coal_engine.status").withStyle(ChatFormatting.GRAY).
                append(new TranslatableComponent("tooltip.energizedpower.inventory_coal_engine.status." +
                        (active?"activated":"deactivated")).withStyle(active?ChatFormatting.GREEN:ChatFormatting.RED)));

        if(Screen.hasShiftDown()) {
            int energyProductionLeft = getEnergyProductionLeft(itemStack);
            ItemStack item = getCurrentBurningItem(itemStack);
            if(energyProductionLeft > 0 && item != null) {
                components.add(new TranslatableComponent("tooltip.energizedpower.inventory_coal_engine.txt.shift.currently_burning").
                        withStyle(ChatFormatting.GRAY).
                        append(item.getDisplayName()));

                components.add(new TranslatableComponent("tooltip.energizedpower.inventory_coal_engine.txt.shift.energy_production_left",
                                EnergyUtils.getEnergyWithPrefix(energyProductionLeft)).
                        withStyle(ChatFormatting.GRAY));
            }

            components.add(new TranslatableComponent("tooltip.energizedpower.inventory_coal_engine.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            components.add(new TranslatableComponent("tooltip.energizedpower.inventory_coal_engine.txt.shift.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            components.add(new TranslatableComponent("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    private int addConsumerEnergyItem(List<IEnergyStorage> consumerItems, List<Integer> consumerEnergyValues,
                                      ItemStack itemStack, ItemStack testItemStack) {
        LazyOptional<IEnergyStorage> energyStorageLazyOptional = testItemStack.getCapability(CapabilityEnergy.ENERGY);
        if(!energyStorageLazyOptional.isPresent())
            return 0;

        IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
        if(!energyStorage.canReceive())
            return 0;

        int received = energyStorage.receiveEnergy(Math.min(MAX_EXTRACT, getEnergy(itemStack)), true);
        if(received <= 0)
            return 0;

        consumerItems.add(energyStorage);
        consumerEnergyValues.add(received);

        return received;
    }

    private void distributeEnergy(ItemStack itemStack, Level level, Inventory inventory, int slot, boolean selected) {
        List<IEnergyStorage> consumerItems = new LinkedList<>();
        List<Integer> consumerEnergyValues = new LinkedList<>();
        int consumptionSum = 0;
        for(int i = 0;i < inventory.getContainerSize();i++) {
            if(i == slot)
                continue;

            ItemStack testItemStack = inventory.getItem(i);

            consumptionSum += addConsumerEnergyItem(consumerItems, consumerEnergyValues, itemStack, testItemStack);
        }

        List<ItemStack> curiosItemStacks = CuriosCompatUtils.getCuriosItemStacks(inventory);
        for(ItemStack testItemStack:curiosItemStacks)
            consumptionSum += addConsumerEnergyItem(consumerItems, consumerEnergyValues, itemStack, testItemStack);

        List<Integer> consumerEnergyDistributed = new LinkedList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0);

        int consumptionLeft = Math.min(MAX_EXTRACT, Math.min(getEnergy(itemStack), consumptionSum));
        setEnergy(itemStack, getEnergy(itemStack) - consumptionLeft);

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
            if(energy > 0)
                consumerItems.get(i).receiveEnergy(energy, false);
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemStack, level, entity, slot, selected);

        if(level.isClientSide)
            return;

        if(!(entity instanceof Player))
            return;

        if(!isActive(itemStack))
            return;

        Player player = (Player)entity;
        Inventory inventory = player.getInventory();

        distributeEnergy(itemStack, level, inventory, slot, selected);

        int energyProductionLeft = getEnergyProductionLeft(itemStack);
        if(energyProductionLeft > 0) {
            int progress = getProgress(itemStack);
            int maxProgress = getMaxProgress(itemStack);
            ItemStack getCurrentBurningItem = getCurrentBurningItem(itemStack);
            if(progress >= 0 && maxProgress > 0 && progress < maxProgress && getCurrentBurningItem != null) {
                int energyProductionPerTick = energyProductionLeft / (maxProgress - progress);
                if(getCapacity(itemStack) - getEnergy(itemStack) < energyProductionPerTick) {
                    //Not enough energy storage for production
                    if(isWorking(itemStack))
                        itemStack.getOrCreateTag().putBoolean("working", false);

                    return;
                }

                if(!isWorking(itemStack))
                    itemStack.getOrCreateTag().putBoolean("working", true);

                setEnergy(itemStack, getEnergy(itemStack) + energyProductionPerTick);

                itemStack.getOrCreateTag().putInt("energy_production_left", energyProductionLeft - energyProductionPerTick);

                progress++;
                if(progress == maxProgress) {
                    resetProgress(itemStack);
                }else {
                    itemStack.getOrCreateTag().putInt("progress", progress);

                    return;
                }
            }else {
                resetProgress(itemStack);
            }
        }

        //Find and burn new fuel item

        //i: 0 - 8 -> Hotbar (Ignore)
        //"< items.size()": Ignore armor and offhand slots
        for(int i = 9;i < inventory.items.size();i++) {
            if(i == slot)
                continue;

            ItemStack testItemStack = inventory.getItem(i);
            int energyProduction = ForgeHooks.getBurnTime(testItemStack, null);
            if(energyProduction <= 0)
                continue;

            itemStack.getOrCreateTag().putInt("energy_production_left", energyProduction);
            itemStack.getOrCreateTag().put("item", testItemStack.save(new CompoundTag()));
            itemStack.getOrCreateTag().putInt("progress", 0);

            //Change max progress if item would output more than max extract
            if(energyProduction / 100 <= MAX_EXTRACT)
                itemStack.getOrCreateTag().putInt("max_progress", 100);
            else
                itemStack.getOrCreateTag().putInt("max_progress", (int)Math.ceil((float)energyProduction / MAX_EXTRACT));

            ItemStack newItemStack = testItemStack.copy();
            newItemStack.shrink(1);
            inventory.setItem(i, newItemStack);

            if(testItemStack.hasContainerItem()) {
                ItemStack craftingRemainingItem = testItemStack.getContainerItem();

                if(inventory.add(craftingRemainingItem))
                    player.drop(craftingRemainingItem, false);
            }

            break;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(level.isClientSide)
            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());

        itemStack.getOrCreateTag().putBoolean("active", !isActive(itemStack));

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    private void resetProgress(ItemStack itemStack) {
        itemStack.getOrCreateTag().remove("energy_production_left");
        itemStack.getOrCreateTag().remove("progress");
        itemStack.getOrCreateTag().remove("max_progress");
        itemStack.getOrCreateTag().remove("item");

        if(isWorking(itemStack))
            itemStack.getOrCreateTag().putBoolean("working", false);
    }

    private int getProgress(ItemStack itemStack) {
        if(!itemStack.hasTag())
            return -1;

        CompoundTag nbt = itemStack.getTag();
        if(nbt == null || !nbt.contains("progress"))
            return -1;

        return nbt.getInt("progress");
    }

    private int getMaxProgress(ItemStack itemStack) {
        if(!itemStack.hasTag())
            return -1;

        CompoundTag nbt = itemStack.getTag();
        if(nbt == null || !nbt.contains("max_progress"))
            return -1;

        return nbt.getInt("max_progress");
    }

    private ItemStack getCurrentBurningItem(ItemStack itemStack) {
        if(!itemStack.hasTag())
            return null;

        CompoundTag nbt = itemStack.getTag();
        if(nbt == null || !nbt.contains("item"))
            return null;

        return ItemStack.of(nbt.getCompound("item"));
    }

    private int getEnergyProductionLeft(ItemStack itemStack) {
        if(!itemStack.hasTag())
            return -1;

        CompoundTag nbt = itemStack.getTag();
        if(nbt == null || !nbt.contains("energy_production_left"))
            return -1;

        return nbt.getInt("energy_production_left");
    }

    @Override
    public boolean isActive(ItemStack itemStack) {
        if(!itemStack.hasTag())
            return false;

        CompoundTag nbt = itemStack.getTag();
        if(nbt == null || !nbt.contains("active"))
            return false;

        return nbt.getBoolean("active");
    }

    @Override
    public boolean isWorking(ItemStack itemStack) {
        if(!itemStack.hasTag())
            return false;

        CompoundTag nbt = itemStack.getTag();
        if(nbt == null || !nbt.contains("working"))
            return false;

        return nbt.getBoolean("working");
    }
}
