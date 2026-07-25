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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public abstract class AbstractChargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler> {
    protected final UpgradableMenuProvider menuProvider;

    private final float chargerRecipeDurationMultiplier;
    protected final int[] energyConsumptionLeft;

    //Item slot indices are dynamic

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i < 0 || i >= slotCount)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);
        if(level != null && RecipeUtils.isResultOfAny(level, ChargerRecipe.Type.INSTANCE, stack))
            return true;

        if(level == null || RecipeUtils.isIngredientOfAny(level, ChargerRecipe.Type.INSTANCE, stack))
            return false;

        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage == null || !energyStorage.canReceive())
            return true;

        return energyStorage.receiveEnergy(AbstractChargerBlockEntity.this.limitingEnergyStorage.getMaxInsert() / slotCount, true) == 0;
    });

    public AbstractChargerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                      String machineName, UpgradableMenuProvider menuProvider,
                                      int slotCount, float chargerRecipeDurationMultiplier,
                                      int baseEnergyCapacityPerSlot, int baseEnergyTransferRatePerSlot) {
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

        energyConsumptionLeft = new int[slotCount];
        for(int i = 0;i < slotCount;i++)
            energyConsumptionLeft[i] = -1;
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
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
            public int getMaxInsert() {
                return Math.max(1, (int)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public int getCapacity(int slot) {
                return 1;
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < slotCount) {
                    if(level == null || RecipeUtils.isIngredientOfAny(level, ChargerRecipe.Type.INSTANCE, stack))
                        return true;

                    IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                    return energyStorage != null && energyStorage.canReceive();
                }

                return super.isValid(slot, stack);
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot >= 0 && slot < slotCount) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() && (!ItemStack.isSameItem(stack, previousItemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, previousItemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(stack.getCapability(Capabilities.EnergyStorage.ITEM) != null &&
                                            previousItemStack.getCapability(Capabilities.EnergyStorage.ITEM) != null))))
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

            //Only add individual & n - 1 slot groups if more than one thread, otherwise there would be duplicated groups
            if(slotCount > 1) {
                List<SlotEntry> allInputsExceptFirst = IntStream.range(1, slotCount).mapToObj(SlotEntry::ofInput).toList();
                List<SlotEntry> allOutputsExceptFirst  = IntStream.range(1, slotCount).mapToObj(SlotEntry::ofOutput).toList();
                List<SlotEntry> allSlotsExceptFirst  = IntStream.range(1, slotCount).mapToObj(SlotEntry::ofBoth).toList();

                //First input only
                slotGroups.add(SlotGroup.of(SlotEntry.ofInput(0)));

                //First output only
                slotGroups.add(SlotGroup.of(SlotEntry.ofOutput(0)));

                //First input & output
                slotGroups.add(SlotGroup.of(SlotEntry.ofBoth(0)));

                //All except first input only
                slotGroups.add(SlotGroup.of(allInputsExceptFirst));

                //All except first output only
                slotGroups.add(SlotGroup.of(allOutputsExceptFirst));

                //All except first input & output
                slotGroups.add(SlotGroup.of(allSlotsExceptFirst));
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

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        //itemHandlerSided must be used because of an extra check
        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandlerSided, facing, side);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        for(int i = 0;i < slotCount;i++)
            nbt.putInt("recipe.energy_consumption_left." + i, energyConsumptionLeft[i]);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        for(int i = 0;i < slotCount;i++)
            energyConsumptionLeft[i] = nbt.getInt("recipe.energy_consumption_left." + i);

        //Ensure compatibility with older versions: Override recipe progress data for slot 0
        if(nbt.contains("recipe.energy_consumption_left"))
            energyConsumptionLeft[0] = nbt.getInt("recipe.energy_consumption_left");
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

        final int maxReceivePerSlot = (int)Math.min(blockEntity.limitingEnergyStorage.getMaxInsert() / (double)blockEntity.slotCount,
                Math.ceil(blockEntity.energyStorage.getEnergy() / (double)blockEntity.slotCount));

        for(int i = 0;i < blockEntity.slotCount;i++) {
            if(blockEntity.hasRecipe(i)) {
                ItemStack stack = blockEntity.itemHandler.getStackInSlot(i);
                int energyConsumptionPerTick;

                SimpleContainer inventory = new SimpleContainer(1);
                inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(i));

                Optional<RecipeHolder<ChargerRecipe>> recipe = level.getRecipeManager().
                        getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);
                if(recipe.isPresent()) {
                    if(blockEntity.energyConsumptionLeft[i] == -1)
                        blockEntity.energyConsumptionLeft[i] = (int)(recipe.get().value().getEnergyConsumption() * blockEntity.chargerRecipeDurationMultiplier);

                    if(blockEntity.energyStorage.getEnergy() == 0) {
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    energyConsumptionPerTick = Math.min(blockEntity.energyConsumptionLeft[i], Math.min(maxReceivePerSlot,
                            blockEntity.energyStorage.getEnergy()));
                }else {
                    IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                    if(energyStorage == null || !energyStorage.canReceive())
                        continue;

                    blockEntity.energyConsumptionLeft[i] = Math.max(0, energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());

                    if(blockEntity.energyStorage.getEnergy() == 0) {
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    energyConsumptionPerTick = energyStorage.receiveEnergy(Math.min(maxReceivePerSlot,
                            blockEntity.energyStorage.getEnergy()), false);
                }

                if(blockEntity.energyConsumptionLeft[i] < 0 || energyConsumptionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    setChanged(level, blockPos, state);

                    continue;
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);
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

    protected final int getEnergyConsumptionPerTickSum() {
        if(!(level instanceof ServerLevel serverLevel))
            return -1;

        final int maxReceivePerSlot = (int)Math.min(this.limitingEnergyStorage.getMaxInsert() / (double)slotCount,
                Math.ceil(this.energyStorage.getEnergy() / (double)slotCount));

        int energyConsumptionSum = -1;

        for(int i = 0;i < slotCount;i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            int energyConsumption;

            SimpleContainer inventory = new SimpleContainer(1);
            inventory.setItem(0, itemHandler.getStackInSlot(i));

            Optional<RecipeHolder<ChargerRecipe>> recipe = level.getRecipeManager().
                    getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

            if(recipe.isPresent()) {
                energyConsumption = Math.min(energyConsumptionLeft[i], Math.min(maxReceivePerSlot, energyStorage.getEnergy()));
            }else {
                IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                if(energyStorage == null || !energyStorage.canReceive())
                    continue;

                energyConsumption = energyStorage.receiveEnergy(Math.min(maxReceivePerSlot, this.energyStorage.getEnergy()), true);
            }

            if(energyConsumptionSum == -1)
                energyConsumptionSum = energyConsumption;
            else
                energyConsumptionSum += energyConsumption;

            if(energyConsumptionSum < 0)
                energyConsumptionSum = Integer.MAX_VALUE;
        }

        return energyConsumptionSum;
    }

    private void resetProgress(int index) {
        energyConsumptionLeft[index] = -1;
    }

    private boolean hasRecipe(int index) {
        ItemStack stack = itemHandler.getStackInSlot(index);
        if(stack.getCapability(Capabilities.EnergyStorage.ITEM) != null)
            return true;

        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, itemHandler.getStackInSlot(index));

        Optional<RecipeHolder<ChargerRecipe>> recipe = level.getRecipeManager().
                getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

        return recipe.isPresent();
    }
}