package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.NoWorkData;
import me.jddev0.ep.block.entity.base.WorkerMachineBlockEntity;
import me.jddev0.ep.inventory.ContainerListener;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.TrackedSimpleContainer;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.util.ItemStackUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;
import java.util.stream.IntStream;

public abstract class AbstractAutoCrafterBlockEntity
        extends WorkerMachineBlockEntity<NoWorkData>
        implements CheckboxUpdate {
    protected final AutoCrafterMenuProvider menuProvider;

    //Item slot indices are dynamic

    private final List<@NotNull Identifier> recipeBlacklist;
    private final long energyConsumptionPerTickPerIngredient;

    private final int outputOnlySlotCount;

    private boolean secondaryExtractMode = false;
    private boolean allowOutputOverflow = true;

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler,
            (i, stack) -> i >= AbstractAutoCrafterBlockEntity.this.outputOnlySlotCount,
            i -> secondaryExtractMode?!isInput(itemHandler.getStackInSlot(i)):
                    isOutputOrCraftingRemainderOfInput(itemHandler.getStackInSlot(i)));

    private final TrackedSimpleContainer[] patternSlots;
    private final SimpleContainer[] patternResultSlots;
    private final ContainerListener[] updatePatternListener;
    private final boolean[] hasRecipeLoaded;
    private final ResourceKey<Recipe<?>>[] recipeIdForSetRecipe;
    private final RecipeHolder<CraftingRecipe>[] craftingRecipe;
    private final CraftingContainer[] oldCopyOfRecipe;
    private final AbstractContainerMenu dummyContainerMenu = new AbstractContainerMenu(null, -1) {
        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return null;
        }
        @Override
        public boolean stillValid(Player player) {
            return false;
        }
        @Override
        public void slotsChanged(Container container) {}
    };

    private final boolean[] ignoreNBT;
    private int currentRecipeIndex = 0;

    @SuppressWarnings("unchecked")
    public AbstractAutoCrafterBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                          String machineName, AutoCrafterMenuProvider menuProvider,
                                          int slotCount, List<@NotNull Identifier> recipeBlacklist, int baseRecipeDuration,
                                          long baseEnergyCapacity, long baseEnergyTransferRate, long energyConsumptionPerTickPerIngredient) {
        super(
                type, blockPos, blockState,

                machineName,

                slotCount, baseRecipeDuration,
                baseEnergyCapacity, baseEnergyTransferRate, 1,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );

        this.menuProvider = menuProvider;

        this.recipeBlacklist = recipeBlacklist;
        this.energyConsumptionPerTickPerIngredient = energyConsumptionPerTickPerIngredient;

        outputOnlySlotCount = initOutputOnlySlotCount();

        patternSlots = new TrackedSimpleContainer[workerThreadCount];
        patternResultSlots = new SimpleContainer[workerThreadCount];
        updatePatternListener = new ContainerListener[workerThreadCount];

        hasRecipeLoaded = new boolean[workerThreadCount];
        recipeIdForSetRecipe = new ResourceKey[workerThreadCount];
        craftingRecipe = new RecipeHolder[workerThreadCount];
        oldCopyOfRecipe = new CraftingContainer[workerThreadCount];

        ignoreNBT = new boolean[workerThreadCount];

        for(int i = 0;i < workerThreadCount;i++) {
            final int thread = i;

            patternSlots[i] = new TrackedSimpleContainer(3 * 3) {
                @Override
                public int getMaxStackSize() {
                            return 1;
                        }
            };
            patternResultSlots[i] = new SimpleContainer(1);
            updatePatternListener[i] = container -> updateRecipe(thread);

            patternSlots[i].addListener(updatePatternListener[i]);
        }
    }

    @Override
    protected abstract int initWorkerThreadCount();

    protected abstract int initOutputOnlySlotCount();

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemVariant stack) {
                if(slot < 0 || slot >= slotCount)
                    return super.isValid(slot, stack);

                //First few slots are for output items only
                return slot >= outputOnlySlotCount;
            }

            @Override
            protected void onFinalCommit(int index, ItemStack previousItemStack) {
                setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        //this.wokerThreadCount is not yet assigned when this method gets called
        int workerThreadCount = initWorkerThreadCount();

        List<ContainerData> combinedContainerDataList = new ArrayList<>(5 * workerThreadCount + 6);
        for(int i = 0;i < workerThreadCount;i++) {
            final int thread = i;

            combinedContainerDataList.add(new ProgressValueContainerData(() -> progress[thread], value -> progress[thread] = value));
            combinedContainerDataList.add(new ProgressValueContainerData(() -> maxProgress[thread], value -> maxProgress[thread] = value));
            combinedContainerDataList.add(new EnergyValueContainerData(() -> energyConsumptionLeft[thread], value -> {}));
            combinedContainerDataList.add(new BooleanValueContainerData(() -> hasEnoughEnergy[thread], value -> {}));
            combinedContainerDataList.add(new BooleanValueContainerData(() -> ignoreNBT[thread], value -> ignoreNBT[thread] = value));
        }

        combinedContainerDataList.add(new EnergyValueContainerData(this::getEnergyConsumptionPerTickSum, value -> {}));

        combinedContainerDataList.add(new BooleanValueContainerData(() -> secondaryExtractMode, value -> secondaryExtractMode = value));
        combinedContainerDataList.add(new BooleanValueContainerData(() -> allowOutputOverflow, value -> allowOutputOverflow = value));

        combinedContainerDataList.add(new ShortValueContainerData(() -> (short)currentRecipeIndex, value -> currentRecipeIndex = value));

        combinedContainerDataList.add(new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value));
        combinedContainerDataList.add(new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value));

        return new CombinedContainerData(combinedContainerDataList.toArray(ContainerData[]::new));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncIOConfigurationToPlayer(player);

        return menuProvider.createMenu(id, inventory, this, upgradeModuleInventory, patternSlots, patternResultSlots, data);
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        //this.outputOnlySlotCount is not yet assigned when this method gets called
        int outputOnlySlotCount = initOutputOnlySlotCount();

        if(slotType == SlotType.ITEM) {
            List<SlotEntry> allOutputOnlySlots = IntStream.range(0, outputOnlySlotCount).mapToObj(SlotEntry::ofOutput).toList();
            List<SlotEntry> allOutputs = IntStream.range(0, slotCount).mapToObj(SlotEntry::ofOutput).toList();
            List<SlotEntry> allInputs = IntStream.range(outputOnlySlotCount, slotCount).mapToObj(SlotEntry::ofInput).toList();
            List<SlotEntry> allInputAndOutputOnlySlots = new ArrayList<>(IntStream.range(0, outputOnlySlotCount).mapToObj(SlotEntry::ofOutput).toList());
            allInputAndOutputOnlySlots.addAll(allInputs);
            List<SlotEntry> allSlots = new ArrayList<>(IntStream.range(0, outputOnlySlotCount).mapToObj(SlotEntry::ofOutput).toList());
            allSlots.addAll(IntStream.range(outputOnlySlotCount, slotCount).mapToObj(SlotEntry::ofBoth).toList());

            List<SlotGroup> slotGroups = new ArrayList<>();

            //All inputs only
            slotGroups.add(SlotGroup.of(allInputs));

            //All output-only slots only
            slotGroups.add(SlotGroup.of(allOutputOnlySlots));

            //All outputs
            slotGroups.add(SlotGroup.of(allOutputs));

            //All inputs & output-only slots
            slotGroups.add(SlotGroup.of(allInputAndOutputOnlySlots));

            //All inputs & outputs
            slotGroups.add(SlotGroup.of(allSlots));

            return slotGroups;
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.ITEM) {
            for(RelativeDirection direction: RelativeDirection.values())
                conf.setSlotGroupId(direction, 4);
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
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        for(int i = 0;i < workerThreadCount;i++)
            savePatternContainer(i, view.child("pattern." + i));

        for(int i = 0;i < workerThreadCount;i++) {
            if(craftingRecipe[i] != null)
                view.putString("recipe.id." + i, craftingRecipe[i].id().identifier().toString());

            view.putBoolean("ignore_nbt." + i, ignoreNBT[i]);
        }

        view.putBoolean("secondary_extract_mode", secondaryExtractMode);
        view.putBoolean("allow_output_overflow", allowOutputOverflow);

        view.putInt("current_recipe_index", currentRecipeIndex);
    }

    private void savePatternContainer(int index, ValueOutput view) {
        NonNullList<ItemStack> items = NonNullList.withSize(patternSlots[index].getContainerSize(), ItemStack.EMPTY);
        for(int i = 0;i < patternSlots[index].getContainerSize();i++)
            items.set(i, patternSlots[index].getItem(i));

        ContainerHelper.saveAllItems(view, items);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        for(int i = 0;i < workerThreadCount;i++)
            loadPatternContainer(i, view.childOrEmpty("pattern." + i));

        for(int i = 0;i < workerThreadCount;i++) {
            final int index = i;
            view.getString("recipe.id." + i).ifPresent(recipeId ->
                    recipeIdForSetRecipe[index] = ResourceKey.create(Registries.RECIPE, Identifier.tryParse(recipeId))
            );

            ignoreNBT[i] = view.getBooleanOr("ignore_nbt." + i, false);
        }

        secondaryExtractMode = view.getBooleanOr("secondary_extract_mode", false);
        allowOutputOverflow = view.getBooleanOr("allow_output_overflow", true);

        currentRecipeIndex = view.getIntOr("current_recipe_index", 0);
        if(currentRecipeIndex < 0 || currentRecipeIndex >= workerThreadCount)
            currentRecipeIndex = 0;
    }

    private void loadPatternContainer(int index, ValueInput view) {
        patternSlots[index].removeListener(updatePatternListener[index]);

        NonNullList<ItemStack> items = NonNullList.withSize(patternSlots[index].getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(view, items);
        for(int i = 0;i < patternSlots[index].getContainerSize();i++)
            patternSlots[index].setItem(i, items.get(i));

        patternSlots[index].addListener(updatePatternListener[index]);
    }

    @Override
    protected double getWorkDataDependentEnergyConsumption(int thread, NoWorkData workData) {
        int itemCount = 0;
        for(int i = 0;i < patternSlots[thread].getContainerSize();i++)
            if(!patternSlots[thread].getItem(i).isEmpty())
                itemCount++;

        return itemCount * energyConsumptionPerTickPerIngredient;
    }

    @Override
    protected void onTickStart() {
        for(int i = 0;i < workerThreadCount;i++) {
            if(!hasRecipeLoaded[i]) {
                updateRecipe(i);

                if(craftingRecipe[i] == null)
                    resetProgress(i);

                setChanged();
            }
        }
    }

    @Override
    protected boolean hasWork(int thread) {
        return craftingRecipe[thread] != null &&
                canInsertItemsIntoOutputSlots(thread) &&
                canExtractItemsFromInput(thread);
    }

    @Override
    protected Optional<NoWorkData> getCurrentWorkData(int thread) {
        int itemCount = 0;
        for(int j = 0;j < patternSlots[thread].getContainerSize();j++)
            if(!patternSlots[thread].getItem(j).isEmpty())
                itemCount++;

        //Ignore empty recipes
        if(itemCount == 0)
            return Optional.empty();

        return Optional.of(NoWorkData.INSTANCE);
    }

    @Override
    protected void onWorkStarted(int thread, NoWorkData workData) {}

    @Override
    protected void onWorkCompleted(int thread, NoWorkData workData) {
        SimpleContainer patternSlotsForRecipe = ignoreNBT[thread]?
                replaceCraftingPatternWithCurrentNBTItems(patternSlots[thread]):patternSlots[thread];
        CraftingContainer copyOfPatternSlots = new TransientCraftingContainer(dummyContainerMenu, 3, 3);
        for(int j = 0;j < patternSlotsForRecipe.getContainerSize();j++)
            copyOfPatternSlots.setItem(j, patternSlotsForRecipe.getItem(j));

        extractItems(thread);
        craftItem(thread, copyOfPatternSlots);
    }

    public void resetProgressAndMarkAsChanged(int thread) {
        resetProgress(thread);
        setChanged();
    }

    public void cycleRecipe() {
        SimpleContainer patternSlotsForRecipe = ignoreNBT[currentRecipeIndex]?
                replaceCraftingPatternWithCurrentNBTItems(patternSlots[currentRecipeIndex]):patternSlots[currentRecipeIndex];
        CraftingContainer copyOfPatternSlots = new TransientCraftingContainer(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            copyOfPatternSlots.setItem(i, patternSlotsForRecipe.getItem(i));

        List<RecipeHolder<CraftingRecipe>> recipes = getRecipesFor(copyOfPatternSlots, level);

        //No recipe found
        if(recipes.isEmpty()) {
            updateRecipe(currentRecipeIndex);

            return;
        }

        if(recipeIdForSetRecipe[currentRecipeIndex] == null)
            recipeIdForSetRecipe[currentRecipeIndex] = (craftingRecipe[currentRecipeIndex] == null ||
                    craftingRecipe[currentRecipeIndex].id() == null)?recipes.get(0).id():craftingRecipe[currentRecipeIndex].id();

        for(int i = 0;i < recipes.size();i++) {
            if(Objects.equals(recipes.get(i).id().identifier(), recipeIdForSetRecipe[currentRecipeIndex].identifier())) {
                recipeIdForSetRecipe[currentRecipeIndex] = recipes.get((i + 1) % recipes.size()).id();

                break;
            }
        }

        updateRecipe(currentRecipeIndex);
    }

    public void setRecipeIdForSetRecipe(ResourceKey<Recipe<?>> recipeIdForSetRecipe) {
        this.recipeIdForSetRecipe[currentRecipeIndex] = recipeIdForSetRecipe;

        updateRecipe(currentRecipeIndex);
    }

    private void updateRecipe(int index) {
        if(level == null)
            return;

        RecipeHolder<CraftingRecipe> oldRecipe = null;
        ItemStack oldResult = null;
        if(hasRecipeLoaded[index] && craftingRecipe[index] != null && oldCopyOfRecipe[index] != null) {
            oldRecipe = craftingRecipe[index];

            oldResult = craftingRecipe[index].value().assemble(oldCopyOfRecipe[index].asCraftInput());
        }

        hasRecipeLoaded[index] = true;

        SimpleContainer patternSlotsForRecipe = ignoreNBT[index]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[index]):patternSlots[index];
        CraftingContainer copyOfPatternSlots = new TransientCraftingContainer(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            copyOfPatternSlots.setItem(i, patternSlotsForRecipe.getItem(i));

        Optional<Pair<ResourceKey<Recipe<?>>, RecipeHolder<CraftingRecipe>>> recipe = getRecipeFor(copyOfPatternSlots, level, recipeIdForSetRecipe[index]);
        if(recipe.isPresent()) {
            craftingRecipe[index] = recipe.get().getSecond();

            //Recipe with saved recipe id does not exist or pattern items are not compatible with recipe
            if(recipeIdForSetRecipe[index] != null && !Objects.equals(craftingRecipe[index].id().identifier(), recipeIdForSetRecipe[index].identifier())) {
                recipeIdForSetRecipe[index] = craftingRecipe[index].id();
                resetProgress(index);
            }

            ItemStack resultItemStack = craftingRecipe[index].value().assemble(copyOfPatternSlots.asCraftInput());

            patternResultSlots[index].setItem(0, resultItemStack);

            if(oldRecipe != null && oldResult != null && oldCopyOfRecipe[index] != null &&
                    (craftingRecipe[index] != oldRecipe || !ItemStack.isSameItemSameComponents(resultItemStack, oldResult)))
                resetProgress(index);

            oldCopyOfRecipe[index] = new TransientCraftingContainer(dummyContainerMenu, 3, 3);
            for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
                oldCopyOfRecipe[index].setItem(i, copyOfPatternSlots.getItem(i).copy());
        }else {
            recipeIdForSetRecipe[index] = null;

            craftingRecipe[index] = null;

            patternResultSlots[index].setItem(0, ItemStack.EMPTY);

            oldCopyOfRecipe[index] = null;

            resetProgress(index);
        }
    }

    private void extractItems(int index) {
        SimpleContainer patternSlotsForRecipe = ignoreNBT[index]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[index]):
                patternSlots[index];
        List<ItemStack> patternItemStacks = new ArrayList<>(9);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            if(!patternSlotsForRecipe.getItem(i).isEmpty())
                patternItemStacks.add(patternSlotsForRecipe.getItem(i));

        List<ItemStack> itemStacksExtract = ItemStackUtils.combineItemStacks(patternItemStacks);

        for(ItemStack itemStack:itemStacksExtract) {
            for(int i = 0;i < itemHandler.size();i++) {
                ItemStack testItemStack = itemHandler.getStackInSlot(i);
                if(ItemStack.isSameItemSameComponents(itemStack, testItemStack)) {
                    ItemStack ret = itemHandler.extractItem(i, itemStack.getCount());
                    if(!ret.isEmpty()) {
                        int amount = ret.getCount();
                        if(amount == itemStack.getCount())
                            break;

                        itemStack.shrink(amount);
                    }
                }
            }
        }
    }

    private void craftItem(int index, CraftingContainer copyOfPatternSlots) {
        if(craftingRecipe[index] == null) {
            resetProgress(index);

            return;
        }

        List<ItemStack> outputItemStacks = new ArrayList<>(10);

        ItemStack resultItemStack = craftingRecipe[index].value().assemble(copyOfPatternSlots.asCraftInput());

        outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe[index].value().getRemainingItems(copyOfPatternSlots.asCraftInput()))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacksInsert = ItemStackUtils.combineItemStacks(outputItemStacks);

        int outputSlotCount = allowOutputOverflow?itemHandler.size():outputOnlySlotCount;
        List<Integer> emptyIndices = new ArrayList<>(outputSlotCount);
        outer:
        for(ItemStack itemStack:itemStacksInsert) {
            for(int i = 0;i < outputSlotCount;i++) {
                ItemStack testItemStack = itemHandler.getStackInSlot(i);
                if(emptyIndices.contains(i))
                    continue;

                if(testItemStack.isEmpty()) {
                    emptyIndices.add(i);

                    continue;
                }

                if(ItemStack.isSameItemSameComponents(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxStackSize() - testItemStack.getCount());
                    if(amount > 0) {
                        itemHandler.setStackInSlot(i, itemHandler.getStackInSlot(i).copyWithCount(testItemStack.getCount() + amount));

                        itemStack.setCount(itemStack.getCount() - amount);

                        if(itemStack.isEmpty())
                            continue outer;
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                continue; //Should not happen

            itemHandler.setStackInSlot(emptyIndices.remove(0), itemStack);
        }

        if(ignoreNBT[index])
            updateRecipe(index);

        resetProgress(index);
    }

    private boolean canExtractItemsFromInput(int index) {
        if(craftingRecipe[index] == null)
            return false;

        SimpleContainer patternSlotsForRecipe = ignoreNBT[index]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[index]):
                patternSlots[index];
        List<ItemStack> patternItemStacks = new ArrayList<>(9);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            if(!patternSlotsForRecipe.getItem(i).isEmpty())
                patternItemStacks.add(patternSlotsForRecipe.getItem(i));

        List<ItemStack> itemStacks = ItemStackUtils.combineItemStacks(patternItemStacks);

        List<Integer> checkedIndices = new ArrayList<>(27);
        outer:
        for(int i = itemStacks.size() - 1;i >= 0;i--) {
            ItemStack itemStack = itemStacks.get(i);

            for(int j = 0;j < itemHandler.size();j++) {
                if(checkedIndices.contains(j))
                    continue;

                ItemStack testItemStack = itemHandler.getStackInSlot(j);
                if(testItemStack.isEmpty()) {
                    checkedIndices.add(j);

                    continue;
                }

                if(ItemStack.isSameItemSameComponents(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getCount());
                    checkedIndices.add(j);

                    if(amount == itemStack.getCount()) {
                        itemStacks.remove(i);

                        continue outer;
                    }else {
                        itemStack.shrink(amount);
                    }
                }
            }

            return false;
        }

        return itemStacks.isEmpty();
    }

    private boolean canInsertItemsIntoOutputSlots(int index) {
        if(craftingRecipe[index] == null)
            return false;

        SimpleContainer patternSlotsForRecipe = ignoreNBT[index]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[index]):
                patternSlots[index];
        CraftingContainer copyOfPatternSlots = new TransientCraftingContainer(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            copyOfPatternSlots.setItem(i, patternSlotsForRecipe.getItem(i));

        List<ItemStack> outputItemStacks = new ArrayList<>(10);
        ItemStack resultItemStack = craftingRecipe[index].value().
                assemble(copyOfPatternSlots.asCraftInput());

        if(!resultItemStack.isEmpty())
            outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe[index].value().getRemainingItems(copyOfPatternSlots.asCraftInput()))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacks = ItemStackUtils.combineItemStacks(outputItemStacks);

        int outputSlotCount = allowOutputOverflow?itemHandler.size():outputOnlySlotCount;
        List<Integer> checkedIndices = new ArrayList<>(outputSlotCount);
        List<Integer> emptyIndices = new ArrayList<>(outputSlotCount);
        outer:
        for(int i = itemStacks.size() - 1;i >= 0;i--) {
            ItemStack itemStack = itemStacks.get(i);
            for(int j = 0;j < outputSlotCount;j++) {
                if(checkedIndices.contains(j) || emptyIndices.contains(j))
                    continue;

                ItemStack testItemStack = itemHandler.getStackInSlot(j);
                if(testItemStack.isEmpty()) {
                    emptyIndices.add(j);

                    continue;
                }

                if(ItemStack.isSameItemSameComponents(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxStackSize() - testItemStack.getCount());

                    if(amount + testItemStack.getCount() == testItemStack.getMaxStackSize())
                        checkedIndices.add(j);

                    if(amount == itemStack.getCount()) {
                        itemStacks.remove(i);

                        continue outer;
                    }else {
                        itemStack.shrink(amount);
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                return false;

            int emptyIndex = emptyIndices.remove(0);
            if(itemStack.getCount() == itemStack.getMaxStackSize())
                checkedIndices.add(emptyIndex);

            itemStacks.remove(i);
        }

        return itemStacks.isEmpty();
    }

    private boolean isOutputOrCraftingRemainderOfInput(ItemStack itemStack) {
        for(int i = 0;i < workerThreadCount;i++) {
            if(craftingRecipe[i] == null)
                continue;

            SimpleContainer patternSlotsForRecipe = ignoreNBT[i]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[i]):
                    patternSlots[i];
            CraftingContainer copyOfPatternSlots = new TransientCraftingContainer(dummyContainerMenu, 3, 3);
            for(int j = 0;j < patternSlotsForRecipe.getContainerSize();j++)
                copyOfPatternSlots.setItem(j, patternSlotsForRecipe.getItem(j));

            ItemStack resultItemStack = craftingRecipe[i].value().assemble(copyOfPatternSlots.asCraftInput());

            if(ItemStack.isSameItemSameComponents(itemStack, resultItemStack))
                return true;

            for(ItemStack remainingItem:craftingRecipe[i].value().getRemainingItems(copyOfPatternSlots.asCraftInput()))
                if(ItemStack.isSameItemSameComponents(itemStack, remainingItem))
                    return true;
        }

        return false;
    }


    private boolean isInput(ItemStack itemStack) {
        for(int i = 0;i < workerThreadCount;i++) {
            if(craftingRecipe[i] == null)
                continue;

            for(int j = 0;j < patternSlots[i].getContainerSize();j++)
                if(ignoreNBT[i]?ItemStack.isSameItem(itemStack, patternSlots[i].getItem(j)):
                        ItemStack.isSameItemSameComponents(itemStack, patternSlots[i].getItem(j)))
                    return true;
        }

        return false;
    }

    private SimpleContainer replaceCraftingPatternWithCurrentNBTItems(SimpleContainer container) {
        SimpleContainer copyOfContainer = new SimpleContainer(container.getContainerSize());
        for(int i = 0;i < container.getContainerSize();i++)
            copyOfContainer.setItem(i, container.getItem(i).copy());

        Map<Integer, Integer> usedItemCounts = new HashMap<>(); //slotIndex: usedCount
        outer:
        for(int i = 0;i < copyOfContainer.getContainerSize();i++) {
            ItemStack itemStack = copyOfContainer.getItem(i);
            if(itemStack.isEmpty())
                continue;

            for(int j = 0;j < itemHandler.size();j++) {
                ItemStack testItemStack = itemHandler.getStackInSlot(j).copy();
                int usedCount = usedItemCounts.getOrDefault(j, 0);
                testItemStack.setCount(testItemStack.getCount() - usedCount);
                if(testItemStack.getCount() <= 0)
                    continue;

                if(ItemStack.isSameItemSameComponents(itemStack, testItemStack)) {
                    usedItemCounts.put(j, usedCount + 1);
                    continue outer;
                }
            }

            //Same item with same tag was not found -> check for same item without same tag and change if found
            for(int j = 0;j < itemHandler.size();j++) {
                ItemStack testItemStack = itemHandler.getStackInSlot(j).copy();
                int usedCount = usedItemCounts.getOrDefault(j, 0);
                testItemStack.setCount(testItemStack.getCount() - usedCount);
                if(testItemStack.getCount() <= 0)
                    continue;

                if(ItemStack.isSameItem(itemStack, testItemStack)) {
                    usedItemCounts.put(j, usedCount + 1);

                    copyOfContainer.setItem(i, testItemStack.copyWithCount(1));

                    continue outer;
                }
            }

            //Not found at all -> Mot enough input items are present
            return copyOfContainer;
        }

        return copyOfContainer;
    }

    private List<RecipeHolder<CraftingRecipe>> getRecipesFor(CraftingContainer patternSlots, Level level) {
        if(!(level instanceof ServerLevel serverLevel))
            return List.of();

        return RecipeUtils.getAllRecipesFor(serverLevel, RecipeType.CRAFTING).
                stream().filter(recipe -> !recipeBlacklist.contains(recipe.id().identifier())).
                filter(recipe -> recipe.value().matches(patternSlots.asCraftInput(), level)).
                sorted(Comparator.comparing(recipe -> recipe.id().identifier())).
                toList();
    }

    private Optional<Pair<ResourceKey<Recipe<?>>, RecipeHolder<CraftingRecipe>>> getRecipeFor(CraftingContainer patternSlots, Level level, ResourceKey<Recipe<?>> recipeId) {
        List<RecipeHolder<CraftingRecipe>> recipes = getRecipesFor(patternSlots, level);
        Optional<RecipeHolder<CraftingRecipe>> recipe = recipes.stream().filter(r -> recipeId != null && r.id().identifier().equals(recipeId.identifier())).findFirst();

        return recipe.or(() -> recipes.stream().findFirst()).map(r -> Pair.of(r.id(), r));
    }

    public int getCurrentRecipeIndex() {
        return currentRecipeIndex;
    }

    public void setCurrentRecipeIndex(int currentRecipeIndex) {
        if(currentRecipeIndex < 0 || currentRecipeIndex >= workerThreadCount)
            currentRecipeIndex = 0;

        this.currentRecipeIndex = currentRecipeIndex;
        setChanged();
    }

    public void setIgnoreNBT(boolean ignoreNBT) {
        this.ignoreNBT[currentRecipeIndex] = ignoreNBT;
        updateRecipe(currentRecipeIndex);
        setChanged();
    }

    public void setSecondaryExtractMode(boolean secondaryExtractMode) {
        this.secondaryExtractMode = secondaryExtractMode;
        setChanged();
    }

    public void setAllowOutputOverflow(boolean allowOutputOverflow) {
        this.allowOutputOverflow = allowOutputOverflow;
        setChanged();
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Ignore NBT
            case 0 -> setIgnoreNBT(checked);

            //Secondary extract mode
            case 1 -> setSecondaryExtractMode(checked);

            //Allow Output Overflow
            case 2 -> setAllowOutputOverflow(checked);
        }
    }
}