package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.block.entity.base.UpgradableMenuProvider;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.CombinedContainerData;
import me.jddev0.ep.inventory.data.ComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public abstract class AbstractChargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler> {
    protected final UpgradableMenuProvider menuProvider;

    private final double chargerRecipeDurationMultiplier;
    protected final long[] energyConsumptionLeft;

    //Item slot indices are dynamic

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i < 0 || i >= slotCount)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);
        if(level != null && RecipeUtils.isResultOfAny(level, ChargerRecipe.Type.INSTANCE, stack))
            return true;

        if(level == null || RecipeUtils.isIngredientOfAny(level, ChargerRecipe.Type.INSTANCE, stack))
            return false;

        if(!EnergyStorageUtil.isEnergyStorage(stack))
            return true;

        EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                ofSingleSlot(itemHandler.getSlot(i)));
        if(limitingEnergyStorage == null)
            return true;

        if(!limitingEnergyStorage.supportsInsertion())
            return true;

        return limitingEnergyStorage.getAmount() == limitingEnergyStorage.getCapacity();
    });

    public AbstractChargerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                      String machineName, UpgradableMenuProvider menuProvider,
                                      int slotCount, double chargerRecipeDurationMultiplier,
                                      long baseEnergyCapacityPerSlot, long baseEnergyTransferRatePerSlot) {
        super(
                type, blockPos, blockState,

                machineName,

                baseEnergyCapacityPerSlot * slotCount,
                baseEnergyTransferRatePerSlot * slotCount,

                slotCount,

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );

        this.menuProvider = menuProvider;

        this.chargerRecipeDurationMultiplier = chargerRecipeDurationMultiplier;

        energyConsumptionLeft = new long[slotCount];
        for(int i = 0;i < slotCount;i++)
            energyConsumptionLeft[i] = -1;
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
            @Override
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0) {
            @Override
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public long getCapacity(int index, ItemVariant resource) {
                return 1;
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
                ItemStack stack = resource.toStack();

                if(slot >= 0 && slot < slotCount) {
                    if(level == null || RecipeUtils.isIngredientOfAny(level, ChargerRecipe.Type.INSTANCE, stack))
                        return true;

                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(limitingEnergyStorage == null)
                        return false;

                    return limitingEnergyStorage.supportsInsertion();
                }

                return super.isValid(slot, resource);
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot >= 0 && slot < slotCount) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() && (!ItemStack.isSameItem(stack, previousItemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, previousItemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(EnergyStorageUtil.isEnergyStorage(stack) && EnergyStorageUtil.isEnergyStorage(previousItemStack)))))
                        resetProgress(slot);
                }

                setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        List<ContainerData> combinedContainerDataList = new ArrayList<>(slotCount + 3);
        combinedContainerDataList.add(new EnergyValueContainerData(this::getEnergyConsumptionPerTickSum, value -> {}));

        for(int i = 0;i < slotCount;i++) {
            final int slot = i;

            combinedContainerDataList.add(new EnergyValueContainerData(() -> energyConsumptionLeft[slot], value -> {}));
        }


        combinedContainerDataList.add(new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value));
        combinedContainerDataList.add(new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value));

        return new CombinedContainerData(combinedContainerDataList.toArray(ContainerData[]::new));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncIOConfigurationToPlayer(player);

        return menuProvider.createMenu(id, inventory, this, upgradeModuleInventory, data);
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        if(slotType == SlotType.ITEM) {
            List<SlotEntry> allInputs = IntStream.range(0, slotCount).mapToObj(SlotEntry::ofInput).toList();
            List<SlotEntry> allOutputs = IntStream.range(0, slotCount).mapToObj(SlotEntry::ofOutput).toList();
            List<SlotEntry> allSlots = IntStream.range(0, slotCount).mapToObj(SlotEntry::ofBoth).toList();

            List<SlotGroup> slotGroups = new ArrayList<>();

            //All inputs only
            slotGroups.add(SlotGroup.of(allInputs));

            //All outputs only
            slotGroups.add(SlotGroup.of(allOutputs));

            //All inputs & outputs
            slotGroups.add(SlotGroup.of(allSlots));

            //Only add 0 to n/2 - 1 & n/2 to n - 1 slot groups if more than one thread, otherwise there would be duplicated groups
            if(slotCount > 1) {
                int firstHalfSlotCount = slotCount/2;

                List<SlotEntry> allInputsFirstHalf = IntStream.range(0, firstHalfSlotCount).mapToObj(SlotEntry::ofInput).toList();
                List<SlotEntry> allOutputsFirstHalf  = IntStream.range(0, firstHalfSlotCount).mapToObj(SlotEntry::ofOutput).toList();
                List<SlotEntry> allSlotsFirstHalf  = IntStream.range(0, firstHalfSlotCount).mapToObj(SlotEntry::ofBoth).toList();

                List<SlotEntry> allInputsSecondHalf = IntStream.range(firstHalfSlotCount, slotCount).mapToObj(SlotEntry::ofInput).toList();
                List<SlotEntry> allOutputsSecondHalf  = IntStream.range(firstHalfSlotCount, slotCount).mapToObj(SlotEntry::ofOutput).toList();
                List<SlotEntry> allSlotsSecondHalf  = IntStream.range(firstHalfSlotCount, slotCount).mapToObj(SlotEntry::ofBoth).toList();

                //First half input only
                slotGroups.add(SlotGroup.of(allInputsFirstHalf));

                //First half output only
                slotGroups.add(SlotGroup.of(allOutputsFirstHalf));

                //First half input & output
                slotGroups.add(SlotGroup.of(allSlotsFirstHalf));

                //Second half input only
                slotGroups.add(SlotGroup.of(allInputsSecondHalf));

                //Second half output only
                slotGroups.add(SlotGroup.of(allOutputsSecondHalf));

                //Second half input & output
                slotGroups.add(SlotGroup.of(allSlotsSecondHalf));
            }

            return slotGroups;
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.ITEM) {
            for(RelativeDirection direction: RelativeDirection.values())
                conf.setSlotGroupId(direction, 2);
        }

        return conf;
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        //itemHandlerSided must be used because of an extra check
        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandlerSided, facing, side);
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(nbt, registries);

        for(int i = 0;i < slotCount;i++)
            nbt.putLong("recipe.energy_consumption_left." + i, energyConsumptionLeft[i]);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(nbt, registries);

        for(int i = 0;i < slotCount;i++)
            energyConsumptionLeft[i] = nbt.getLong("recipe.energy_consumption_left." + i);

        //Ensure compatibility with older versions: Override recipe progress data for slot 0
        if(nbt.contains("recipe.energy_consumption_left"))
            energyConsumptionLeft[0] = nbt.getLong("recipe.energy_consumption_left");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AbstractChargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(WorkerMachineBlock.POWERED)))
            return;

        blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));
        tickRecipe(level, blockPos, state, blockEntity);
        blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, AbstractChargerBlockEntity blockEntity) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        final long maxReceivePerSlot = (long)Math.min(blockEntity.limitingEnergyStorage.getMaxInsert() / (double)blockEntity.slotCount,
                Math.ceil(blockEntity.energyStorage.getAmount() / (double)blockEntity.slotCount));

        for(int i = 0;i < blockEntity.slotCount;i++) {
            if(blockEntity.hasRecipe(i)) {
                ItemStack stack = blockEntity.itemHandler.getStackInSlot(i);
                long energyConsumptionPerTick;

                SimpleContainer inventory = new SimpleContainer(1);
                inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(i));

                Optional<RecipeHolder<ChargerRecipe>> recipe = level.getRecipeManager().
                        getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);
                if(recipe.isPresent()) {
                    if(blockEntity.energyConsumptionLeft[i] == -1)
                        blockEntity.energyConsumptionLeft[i] = (long)(recipe.get().value().getEnergyConsumption() * blockEntity.chargerRecipeDurationMultiplier);

                    if(blockEntity.energyStorage.getAmount() == 0) {
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    energyConsumptionPerTick = Math.min(blockEntity.energyConsumptionLeft[i], Math.min(maxReceivePerSlot,
                            blockEntity.energyStorage.getAmount()));
                }else {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        continue;

                    EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                            ofSingleSlot(blockEntity.itemHandler.getSlot(i)));
                    if(limitingEnergyStorage == null)
                        continue;

                    if(!limitingEnergyStorage.supportsInsertion())
                        continue;

                    blockEntity.energyConsumptionLeft[i] = Math.max(0, limitingEnergyStorage.getCapacity() - limitingEnergyStorage.getAmount());

                    if(blockEntity.energyStorage.getAmount() == 0) {
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    try(Transaction transaction = Transaction.openOuter()) {
                        energyConsumptionPerTick = limitingEnergyStorage.insert(Math.min(maxReceivePerSlot,
                                blockEntity.energyStorage.getAmount()), transaction);
                        transaction.commit();
                    }
                }

                if(blockEntity.energyConsumptionLeft[i] < 0 || energyConsumptionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    setChanged(level, blockPos, state);

                    continue;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    energyConsumptionPerTick = blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);
                    transaction.commit();
                }

                blockEntity.energyConsumptionLeft[i] -= energyConsumptionPerTick;

                if(blockEntity.energyConsumptionLeft[i] <= 0) {
                    final int index = i;
                    recipe.ifPresent(chargerRecipe -> blockEntity.itemHandler.setStackInSlot(index,
                            chargerRecipe.value().getResultItem(level.registryAccess()).copyWithCount(1)));

                    blockEntity.resetProgress(i);
                }
                setChanged(level, blockPos, state);
            }else {
                blockEntity.resetProgress(i);
                setChanged(level, blockPos, state);
            }
        }
    }

    protected final long getEnergyConsumptionPerTickSum() {
        if(!(level instanceof ServerLevel serverLevel))
            return -1;

        final long maxReceivePerSlot = (long)Math.min(this.limitingEnergyStorage.getMaxInsert() / (double)slotCount,
                Math.ceil(this.energyStorage.getAmount() / (double)slotCount));

        long energyConsumptionSum = -1;

        for(int i = 0;i < slotCount;i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            long energyConsumption;

            SimpleContainer inventory = new SimpleContainer(1);
            inventory.setItem(0, itemHandler.getStackInSlot(i));

            Optional<RecipeHolder<ChargerRecipe>> recipe = level.getRecipeManager().
                    getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

            if(recipe.isPresent()) {
                energyConsumption = Math.min(energyConsumptionLeft[i], Math.min(maxReceivePerSlot, energyStorage.getAmount()));
            }else {
                if(!EnergyStorageUtil.isEnergyStorage(stack))
                    continue;

                EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                        ofSingleSlot(itemHandler.getSlot(i)));
                if(limitingEnergyStorage == null)
                    continue;

                if(!limitingEnergyStorage.supportsInsertion())
                    continue;

                try(Transaction transaction = Transaction.openOuter()) {
                    energyConsumption = limitingEnergyStorage.insert(Math.min(maxReceivePerSlot,
                            this.energyStorage.getAmount()), transaction);
                }
            }

            if(energyConsumptionSum == -1)
                energyConsumptionSum = energyConsumption;
            else
                energyConsumptionSum += energyConsumption;

            if(energyConsumptionSum < 0)
                energyConsumptionSum = Long.MAX_VALUE;
        }

        return energyConsumptionSum;
    }

    private void resetProgress(int index) {
        energyConsumptionLeft[index] = -1;
    }

    private boolean hasRecipe(int index) {
        ItemStack stack = itemHandler.getStackInSlot(index);
        if(EnergyStorageUtil.isEnergyStorage(stack))
            return true;

        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, itemHandler.getStackInSlot(index));

        Optional<RecipeHolder<ChargerRecipe>> recipe = level == null?Optional.empty():
                level.getRecipeManager().getRecipeFor(ChargerRecipe.Type.INSTANCE,
                        new ContainerRecipeInputWrapper(inventory), level);

        return recipe.isPresent();
    }
}