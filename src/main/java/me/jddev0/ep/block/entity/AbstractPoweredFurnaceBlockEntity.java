package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.UpgradableMenuProvider;
import me.jddev0.ep.block.entity.base.WorkerFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.data.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import me.jddev0.ep.util.XPUtils;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public abstract class AbstractPoweredFurnaceBlockEntity
        extends WorkerFluidMachineBlockEntity<RecipeHolder<? extends AbstractCookingRecipe>>
        implements IngredientPacketUpdate {
    protected final UpgradableMenuProvider menuProvider;

    private final List<@NotNull Identifier> recipeBlacklist;
    private final double recipeDurationMultiplier;

    //Item slot indices are dynamic

    private static final int FLUID_SLOT_INPUT = 0;

    private double leftoverXPAmount = 0;

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    public AbstractPoweredFurnaceBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                             String machineName, UpgradableMenuProvider menuProvider,
                                             int slotCount, List<@NotNull Identifier> recipeBlacklist, double recipeDurationMultiplier,
                                             long baseEnergyCapacity, long baseEnergyTransferRate, long baseEnergyConsumptionPerTickPerRecipe,
                                             long baseTankCapacity) {
        super(
                type, blockPos, blockState,

                machineName,

                slotCount, 1,
                baseEnergyCapacity, baseEnergyTransferRate, baseEnergyConsumptionPerTickPerRecipe,
                baseTankCapacity,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING,
                UpgradeModuleModifier.XP_YIELD
        );

        this.menuProvider = menuProvider;

        this.recipeBlacklist = recipeBlacklist;
        this.recipeDurationMultiplier = recipeDurationMultiplier;
    }

    @Override
    protected abstract int initWorkerThreadCount();

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
                ItemStack stack = resource.toStack();

                boolean isInputSlot = slot < workerThreadCount;
                return isInputSlot && ((level instanceof ServerLevel serverLevel)?
                        RecipeUtils.isIngredientOfAny(serverLevel, getRecipeForFurnaceModeUpgrade(), stack):
                        RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                boolean isInputSlot = slot < workerThreadCount;
                if(isInputSlot) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    if(!stack.isEmpty() && !previousItemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(slot);
                }

                setChanged();
            }
        };
    }

    @Override
    protected EnergizedPowerFluidStorage initFluidStorage() {
        return new EnergizedPowerFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isValid(int index, FluidVariant resource) {
                if(!super.isValid(index, resource) || level == null)
                    return false;

                return resource.is(ConventionalFluidTags.EXPERIENCE);
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        //this.wokerThreadCount is not yet assigned when this method gets called
        int workerThreadCount = initWorkerThreadCount();

        List<ContainerData> combinedContainerDataList = new ArrayList<>(4 * workerThreadCount + 3);
        for(int i = 0;i < workerThreadCount;i++) {
            final int thread = i;

            combinedContainerDataList.add(new ProgressValueContainerData(() -> progress[thread], value -> progress[thread] = value));
            combinedContainerDataList.add(new ProgressValueContainerData(() -> maxProgress[thread], value -> maxProgress[thread] = value));
            combinedContainerDataList.add(new EnergyValueContainerData(() -> energyConsumptionLeft[thread], value -> {}));
            combinedContainerDataList.add(new BooleanValueContainerData(() -> hasEnoughEnergy[thread], value -> {}));
        }

        combinedContainerDataList.add(new EnergyValueContainerData(this::getEnergyConsumptionPerTickSum, value -> {}));

        combinedContainerDataList.add(new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value));
        combinedContainerDataList.add(new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value));

        return new CombinedContainerData(combinedContainerDataList.toArray(ContainerData[]::new));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);
        syncIngredientListToPlayer(player);
        syncIOConfigurationToPlayer(player);

        return menuProvider.createMenu(id, inventory, this, upgradeModuleInventory, data);
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        //this.wokerThreadCount is not yet assigned when this method gets called
        int workerThreadCount = initWorkerThreadCount();

        return switch(slotType) {
            case ITEM -> {
                List<SlotEntry> allInputs = IntStream.range(0, workerThreadCount).mapToObj(SlotEntry::ofInput).toList();
                List<SlotEntry> allOutputs = IntStream.range(workerThreadCount, 2 * workerThreadCount).mapToObj(SlotEntry::ofOutput).toList();
                List<SlotEntry> allSlots = new ArrayList<>(allInputs);
                allSlots.addAll(allOutputs);

                List<SlotGroup> slotGroups = new ArrayList<>();

                //All inputs only
                slotGroups.add(SlotGroup.of(allInputs));

                //All outputs only
                slotGroups.add(SlotGroup.of(allOutputs));

                //All inputs & outputs
                slotGroups.add(SlotGroup.of(allSlots));

                //Only add individual & n - 1 slot groups if more than one thread, otherwise there would be duplicated groups
                if(workerThreadCount > 1) {
                    List<SlotEntry> allInputsExceptFirst = IntStream.range(1, workerThreadCount).mapToObj(SlotEntry::ofInput).toList();
                    List<SlotEntry> allOutputsExceptFirst  = IntStream.range(workerThreadCount + 1, 2 * workerThreadCount).mapToObj(SlotEntry::ofOutput).toList();
                    List<SlotEntry> allSlotsExceptFirst = new ArrayList<>(allInputsExceptFirst);
                    allSlotsExceptFirst.addAll(allOutputsExceptFirst);

                    //First input only
                    slotGroups.add(SlotGroup.of(SlotEntry.ofInput(0)));

                    //First output only
                    slotGroups.add(SlotGroup.of(SlotEntry.ofOutput(workerThreadCount)));

                    //First input & output
                    slotGroups.add(SlotGroup.of(SlotEntry.ofInput(0), SlotEntry.ofOutput(workerThreadCount)));

                    //All except first input only
                    slotGroups.add(SlotGroup.of(allInputsExceptFirst));

                    //All except first output only
                    slotGroups.add(SlotGroup.of(allOutputsExceptFirst));

                    //All except first input & output
                    slotGroups.add(SlotGroup.of(allSlotsExceptFirst));
                }

                yield slotGroups;
            }
            case FLUID -> List.of(
                    SlotGroup.of(SlotEntry.ofOutput(FLUID_SLOT_INPUT))
            );
        };
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        switch(slotType) {
            case ITEM -> {
                for(RelativeDirection direction:RelativeDirection.values())
                    conf.setSlotGroupId(direction, 2);
            }
            case FLUID -> {
                for(RelativeDirection direction:RelativeDirection.values())
                    conf.setSlotGroupId(direction, 0);
            }
        }

        return conf;
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandler, facing, side);
    }

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.FLUID);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.FLUID);
        return conf.createSidedFluidHandlerFor(slotGroups, fluidStorage, facing, side);
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putDouble("recipe.leftover_xp_amount", leftoverXPAmount);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        leftoverXPAmount = view.getDoubleOr("recipe.leftover_xp_amount", 0.);
    }

    protected SimpleContainer getInventoryForRecipe(int thread) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for(int i = 0;i < 2;i++)
            inventory.setItem(i, itemHandler.getStackInSlot(thread + i * workerThreadCount));

        return inventory;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Optional<RecipeHolder<? extends AbstractCookingRecipe>> getCurrentWorkData(int thread) {
        return (Optional<RecipeHolder<? extends AbstractCookingRecipe>>)getRecipeFor(getInventoryForRecipe(thread), level);
    }

    @Override
    protected final boolean hasWork(int thread) {
        return hasRecipe(thread);
    }

    @Override
    protected void onWorkStarted(int thread, RecipeHolder<? extends AbstractCookingRecipe> recipe) {}

    @Override
    protected void onWorkCompleted(int thread, RecipeHolder<? extends AbstractCookingRecipe> workData) {
        craftItem(thread, workData);
    }

    @Override
    protected double getWorkDataDependentWorkDuration(int thread, RecipeHolder<? extends AbstractCookingRecipe> recipe) {
        return recipe.value().cookingTime() * recipeDurationMultiplier;
    }

    private void craftItem(int thread, RecipeHolder<? extends AbstractCookingRecipe> workData) {
        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = getRecipeFor(getInventoryForRecipe(thread), level);

        if(!hasRecipe(thread) || recipe.isEmpty())
            return;

        itemHandler.extractItem(thread, 1);
        itemHandler.setStackInSlot(thread + workerThreadCount, recipe.get().value().assemble(null).copyWithCount(
                itemHandler.getStackInSlot(thread + workerThreadCount).getCount() + recipe.get().value().assemble(null).getCount()));

        if(upgradeModuleInventory.getMainUpgradeModuleModifier(6) == UpgradeModuleModifier.XP_YIELD) {
            leftoverXPAmount += recipe.get().value().experience() * upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.XP_YIELD);
            int xpYieldThisTick = (int)leftoverXPAmount;

            //Only keep decimal part
            leftoverXPAmount -= xpYieldThisTick;

            try(Transaction transaction = Transaction.openOuter()) {
                //Do not check if overflow -> Extra XP should just vanish
                fluidStorage.insert(FluidVariant.of(EPFluids.LIQUID_XP), XPUtils.XP_TO_LIQUID_RATIO * xpYieldThisTick, transaction);

                transaction.commit();
            }
        }

        resetProgress(thread);
    }

    private boolean hasRecipe(int thread) {
        SimpleContainer inventory = getInventoryForRecipe(thread);

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = getRecipeFor(inventory, level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.get().value().assemble(null));
    }

    private Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> getRecipeFor(Container container, Level level) {
        if(!(level instanceof ServerLevel serverLevel))
            return Optional.empty();

        return RecipeUtils.getAllRecipesFor(serverLevel, getRecipeForFurnaceModeUpgrade()).
                stream().filter(recipe -> !recipeBlacklist.contains(recipe.id().identifier())).
                filter(recipe -> recipe.value().matches(new SingleRecipeInput(container.getItem(0)), level)).
                findFirst();
    }

    public RecipeType<? extends AbstractCookingRecipe> getRecipeForFurnaceModeUpgrade() {
        double value = upgradeModuleInventory.getUpgradeModuleModifierEffect(3, UpgradeModuleModifier.FURNACE_MODE);
        if(value == 1)
            return RecipeType.BLASTING;
        else if(value == 2)
            return RecipeType.SMOKING;

        return RecipeType.SMELTING;
    }

    @Override
    protected void updateUpgradeModules() {
        super.updateUpgradeModules();

        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf((ServerLevel)level, getRecipeForFurnaceModeUpgrade())),
                    getBlockPos(), (ServerLevel)level, 32
            );
    }

    protected void syncIngredientListToPlayer(Player player) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        ModMessages.sendToPlayer(
                new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf(serverLevel, getRecipeForFurnaceModeUpgrade())),
                (ServerPlayer)player
        );
    }

    public List<Ingredient> getIngredientsOfRecipes() {
        return new ArrayList<>(ingredientsOfRecipes);
    }

    @Override
    public void setIngredients(int index, List<Ingredient> ingredients) {
        if(index == 0)
            this.ingredientsOfRecipes = ingredients;
    }
}