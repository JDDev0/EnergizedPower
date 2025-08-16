package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.AutoCrafterBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AutoCrafterMenu;
import me.jddev0.ep.util.ItemStackUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.*;

public class AutoCrafterBlockEntity extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleInventory>
        implements CheckboxUpdate {
    private static final List<@NotNull Identifier> RECIPE_BLACKLIST = ModConfigs.COMMON_AUTO_CRAFTER_RECIPE_BLACKLIST.getValue();

    public final static long ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT =
            ModConfigs.COMMON_AUTO_CRAFTER_ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT.getValue();

    public static final int RECIPE_DURATION = ModConfigs.COMMON_AUTO_CRAFTER_RECIPE_DURATION.getValue();

    private boolean secondaryExtractMode;
    private boolean allowOutputOverflow = true;

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 3,
            i -> secondaryExtractMode?!isInput(itemHandler.getStack(i)):isOutputOrCraftingRemainderOfInput(itemHandler.getStack(i)));

    private final SimpleInventory patternSlots = new SimpleInventory(3 * 3) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    private final SimpleInventory patternResultSlots = new SimpleInventory(1);
    private final InventoryChangedListener updatePatternListener = container -> updateRecipe();
    private boolean hasRecipeLoaded = false;
    private RegistryKey<Recipe<?>> recipeIdForSetRecipe;
    private RecipeEntry<CraftingRecipe> craftingRecipe;
    private CraftingInventory oldCopyOfRecipe;
    private final ScreenHandler dummyContainerMenu = new ScreenHandler(null, -1) {
        @Override
        public ItemStack quickMove(PlayerEntity player, int index) {
            return null;
        }
        @Override
        public boolean canUse(PlayerEntity player) {
            return false;
        }
        @Override
        public void onContentChanged(Inventory container) {}
    };

    private int progress;
    private int maxProgress;
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;
    private boolean ignoreNBT;

    public AutoCrafterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.AUTO_CRAFTER_ENTITY, blockPos, blockState,

                "auto_crafter",

                ModConfigs.COMMON_AUTO_CRAFTER_CAPACITY.getValue(),
                ModConfigs.COMMON_AUTO_CRAFTER_TRANSFER_RATE.getValue(),

                18,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );

        patternSlots.addListener(updatePatternListener);
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
            @Override
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                markDirty();
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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot < 0 || slot >= 18)
                    return super.isValid(slot, stack);

                //Slot 0, 1, and 2 are for output items only
                return slot >= 3;
            }

            @Override
            public void markDirty() {
                super.markDirty();

                AutoCrafterBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(this::getEnergyConsumptionPerTick, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft, value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy, value -> {}),
                new BooleanValueContainerData(() -> ignoreNBT, value -> ignoreNBT = value),
                new BooleanValueContainerData(() -> secondaryExtractMode, value -> secondaryExtractMode = value),
                new BooleanValueContainerData(() -> allowOutputOverflow, value -> allowOutputOverflow = value),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);

        return new AutoCrafterMenu(id, this, inventory, itemHandler, upgradeModuleInventory, patternSlots, patternResultSlots, data);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        Inventories.writeData(view.get("pattern"), patternSlots.heldStacks);

        if(craftingRecipe != null)
            view.putString("recipe.id", craftingRecipe.id().getValue().toString());

        view.putInt("recipe.progress", progress);
        view.putInt("recipe.max_progress", maxProgress);
        view.putLong("recipe.energy_consumption_left", energyConsumptionLeft);

        view.putBoolean("ignore_nbt", ignoreNBT);
        view.putBoolean("secondary_extract_mode", secondaryExtractMode);
        view.putBoolean("allow_output_overflow", allowOutputOverflow);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        patternSlots.removeListener(updatePatternListener);
        Inventories.readData(view.getReadView("pattern"), patternSlots.heldStacks);
        patternSlots.addListener(updatePatternListener);

        view.getOptionalString("recipe.id").ifPresent(recipeId ->
                recipeIdForSetRecipe = RegistryKey.of(RegistryKeys.RECIPE, Identifier.tryParse(recipeId)));

        progress = view.getInt("recipe.progress", 0);
        maxProgress = view.getInt("recipe.max_progress", 0);
        energyConsumptionLeft = view.getLong("recipe.energy_consumption_left", 0);

        ignoreNBT = view.getBoolean("ignore_nbt", false);
        secondaryExtractMode = view.getBoolean("secondary_extract_mode", false);
        allowOutputOverflow = view.getBoolean("allow_output_overflow", true);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AutoCrafterBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.hasRecipeLoaded) {
            blockEntity.updateRecipe();

            if(blockEntity.craftingRecipe == null)
                blockEntity.resetProgress();

            markDirty(level, blockPos, state);
        }

        if(!blockEntity.redstoneMode.isActive(state.get(AutoCrafterBlock.POWERED)))
            return;

        int itemCount = 0;
        for(int i = 0;i < blockEntity.patternSlots.size();i++)
            if(!blockEntity.patternSlots.getStack(i).isEmpty())
                itemCount++;

        //Ignore empty recipes
        if(itemCount == 0)
            return;

        if(blockEntity.craftingRecipe != null && (blockEntity.progress > 0 || (blockEntity.canInsertIntoOutputSlots() && blockEntity.canExtractItemsFromInput()))) {
            if(!blockEntity.canInsertIntoOutputSlots() || !blockEntity.canExtractItemsFromInput())
                return;

            if(blockEntity.maxProgress == 0)
                blockEntity.maxProgress = Math.max(1, (int)Math.ceil(RECIPE_DURATION /
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));

            long energyConsumptionPerTick = Math.max(1, (long)Math.ceil(itemCount * ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.progress == 0) {
                if(!blockEntity.canExtractItemsFromInput())
                    return;

                blockEntity.energyConsumptionLeft = energyConsumptionPerTick * blockEntity.maxProgress;
            }

            if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                //Reset progress for invalid values

                blockEntity.resetProgress();
                markDirty(level, blockPos, state);

                return;
            }

            if(energyConsumptionPerTick <= blockEntity.energyStorage.getAmount()) {
                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);
                    transaction.commit();
                }

                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

                blockEntity.progress++;

                if(blockEntity.progress >= blockEntity.maxProgress) {
                    SimpleInventory patternSlotsForRecipe = blockEntity.ignoreNBT?
                            blockEntity.replaceCraftingPatternWithCurrentNBTItems(blockEntity.patternSlots):blockEntity.patternSlots;
                    CraftingInventory copyOfPatternSlots = new CraftingInventory(blockEntity.dummyContainerMenu, 3, 3);
                    for(int i = 0;i < patternSlotsForRecipe.size();i++)
                        copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));

                    blockEntity.extractItems();
                    blockEntity.craftItem(copyOfPatternSlots);
                }

                markDirty(level, blockPos, state);
            }else {
                blockEntity.hasEnoughEnergy = false;
                markDirty(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress();
            markDirty(level, blockPos, state);
        }
    }
    
    protected final long getEnergyConsumptionPerTick() {
        int itemCount = 0;
        for(int i = 0;i < patternSlots.size();i++)
            if(!patternSlots.getStack(i).isEmpty())
                itemCount++;

        //Ignore empty recipes
        if(itemCount == 0 || craftingRecipe == null || progress <= 0)
            return -1;

        return Math.max(1, (long)Math.ceil(itemCount * ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = true;
    }

    public void resetProgressAndMarkAsChanged() {
        resetProgress();
        markDirty(world, getPos(), getCachedState());
    }

    public void cycleRecipe() {
        SimpleInventory patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        CraftingInventory copyOfPatternSlots = new CraftingInventory(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));

        List<RecipeEntry<CraftingRecipe>> recipes = getRecipesFor(copyOfPatternSlots, world);

        //No recipe found
        if(recipes.isEmpty()) {
            updateRecipe();

            return;
        }

        if(recipeIdForSetRecipe == null)
            recipeIdForSetRecipe = (craftingRecipe == null || craftingRecipe.id() == null)?recipes.get(0).id():
                    craftingRecipe.id();

        for(int i = 0;i < recipes.size();i++) {
            if(Objects.equals(recipes.get(i).id().getValue(), recipeIdForSetRecipe.getValue())) {
                recipeIdForSetRecipe = recipes.get((i + 1) % recipes.size()).id();

                break;
            }
        }

        updateRecipe();
    }

    public void setRecipeIdForSetRecipe(RegistryKey<Recipe<?>> recipeIdForSetRecipe) {
        this.recipeIdForSetRecipe = recipeIdForSetRecipe;

        updateRecipe();
    }

    private void updateRecipe() {
        if(world == null)
            return;

        RecipeEntry<CraftingRecipe> oldRecipe = null;
        ItemStack oldResult = null;
        if(hasRecipeLoaded && craftingRecipe != null && oldCopyOfRecipe != null) {
            oldRecipe = craftingRecipe;

            oldResult = craftingRecipe.value().craft(oldCopyOfRecipe.createRecipeInput(), world.getRegistryManager());
        }

        hasRecipeLoaded = true;

        SimpleInventory patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        CraftingInventory copyOfPatternSlots = new CraftingInventory(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));

        Optional<Pair<RegistryKey<Recipe<?>>, RecipeEntry<CraftingRecipe>>> recipe = getRecipeFor(copyOfPatternSlots, world, recipeIdForSetRecipe);
        if(recipe.isPresent()) {
            craftingRecipe = recipe.get().getSecond();

            //Recipe with saved recipe id does not exist or pattern items are not compatible with recipe
            if(recipeIdForSetRecipe != null && !Objects.equals(craftingRecipe.id().getValue(), recipeIdForSetRecipe.getValue())) {
                recipeIdForSetRecipe = craftingRecipe.id();
                resetProgress();
            }

            ItemStack resultItemStack = craftingRecipe.value().craft(copyOfPatternSlots.createRecipeInput(), world.getRegistryManager());

            patternResultSlots.setStack(0, resultItemStack);

            if(oldRecipe != null && oldResult != null && oldCopyOfRecipe != null && (craftingRecipe != oldRecipe || ItemStack.areItemsAndComponentsEqual(resultItemStack, oldResult)))
                resetProgress();

            oldCopyOfRecipe = new CraftingInventory(dummyContainerMenu, 3, 3);
            for(int i = 0;i < patternSlotsForRecipe.size();i++)
                oldCopyOfRecipe.setStack(i, copyOfPatternSlots.getStack(i).copy());
        }else {
            recipeIdForSetRecipe = null;

            craftingRecipe = null;

            patternResultSlots.setStack(0, ItemStack.EMPTY);

            oldCopyOfRecipe = null;

            resetProgress();
        }
    }

    private void extractItems() {
        SimpleInventory patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        List<ItemStack> patternItemStacks = new ArrayList<>(9);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            if(!patternSlotsForRecipe.getStack(i).isEmpty())
                patternItemStacks.add(patternSlotsForRecipe.getStack(i));

        List<ItemStack> itemStacksExtract = ItemStackUtils.combineItemStacks(patternItemStacks);

        for(ItemStack itemStack:itemStacksExtract) {
            for(int i = 0;i < itemHandler.size();i++) {
                ItemStack testItemStack = itemHandler.getStack(i);
                if(ItemStack.areItemsAndComponentsEqual(itemStack, testItemStack)) {
                    ItemStack ret = itemHandler.removeStack(i, itemStack.getCount());
                    if(!ret.isEmpty()) {
                        int amount = ret.getCount();
                        if(amount == itemStack.getCount())
                            break;

                        itemStack.decrement(amount);
                    }
                }
            }
        }
    }

    private void craftItem(CraftingInventory copyOfPatternSlots) {
        if(craftingRecipe == null) {
            resetProgress();

            return;
        }

        List<ItemStack> outputItemStacks = new ArrayList<>(10);

        ItemStack resultItemStack = craftingRecipe.value().
                craft(copyOfPatternSlots.createRecipeInput(), world.getRegistryManager());

        outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe.value().getRecipeRemainders(copyOfPatternSlots.createRecipeInput()))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacksInsert = ItemStackUtils.combineItemStacks(outputItemStacks);

        int outputSlotCount = allowOutputOverflow?itemHandler.size():3;
        List<Integer> emptyIndices = new ArrayList<>(outputSlotCount);
        outer:
        for(ItemStack itemStack:itemStacksInsert) {
            for(int i = 0;i < outputSlotCount;i++) {
                ItemStack testItemStack = itemHandler.getStack(i);
                if(emptyIndices.contains(i))
                    continue;

                if(testItemStack.isEmpty()) {
                    emptyIndices.add(i);

                    continue;
                }

                if(ItemStack.areItemsAndComponentsEqual(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxCount() - testItemStack.getCount());
                    if(amount > 0) {
                        itemHandler.setStack(i, itemHandler.getStack(i).copyWithCount(testItemStack.getCount() + amount));

                        itemStack.setCount(itemStack.getCount() - amount);

                        if(itemStack.isEmpty())
                            continue outer;
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                continue; //Should not happen

            itemHandler.setStack(emptyIndices.remove(0), itemStack);
        }

        if(ignoreNBT)
            updateRecipe();

        resetProgress();
    }

    private boolean canExtractItemsFromInput() {
        if(craftingRecipe == null)
            return false;

        SimpleInventory patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        List<ItemStack> patternItemStacks = new ArrayList<>(9);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            if(!patternSlotsForRecipe.getStack(i).isEmpty())
                patternItemStacks.add(patternSlotsForRecipe.getStack(i));

        List<ItemStack> itemStacks = ItemStackUtils.combineItemStacks(patternItemStacks);

        List<Integer> checkedIndices = new ArrayList<>(18);
        outer:
        for(int i = itemStacks.size() - 1;i >= 0;i--) {
            ItemStack itemStack = itemStacks.get(i);

            for(int j = 0;j < itemHandler.size();j++) {
                if(checkedIndices.contains(j))
                    continue;

                ItemStack testItemStack = itemHandler.getStack(j);
                if(testItemStack.isEmpty()) {
                    checkedIndices.add(j);

                    continue;
                }

                if(ItemStack.areItemsAndComponentsEqual(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getCount());
                    checkedIndices.add(j);

                    if(amount == itemStack.getCount()) {
                        itemStacks.remove(i);

                        continue outer;
                    }else {
                        itemStack.decrement(amount);
                    }
                }
            }

            return false;
        }

        return itemStacks.isEmpty();
    }

    private boolean canInsertIntoOutputSlots() {
        if(craftingRecipe == null)
            return false;

        SimpleInventory patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        CraftingInventory copyOfPatternSlots = new CraftingInventory(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));


        List<ItemStack> outputItemStacks = new ArrayList<>(10);
        ItemStack resultItemStack = craftingRecipe.value().craft(copyOfPatternSlots.createRecipeInput(), world.getRegistryManager());

        if(!resultItemStack.isEmpty())
            outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe.value().getRecipeRemainders(copyOfPatternSlots.createRecipeInput()))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacks = ItemStackUtils.combineItemStacks(outputItemStacks);

        int outputSlotCount = allowOutputOverflow?itemHandler.size():3;
        List<Integer> checkedIndices = new ArrayList<>(outputSlotCount);
        List<Integer> emptyIndices = new ArrayList<>(outputSlotCount);
        outer:
        for(int i = itemStacks.size() - 1;i >= 0;i--) {
            ItemStack itemStack = itemStacks.get(i);
            for(int j = 0;j < outputSlotCount;j++) {
                if(checkedIndices.contains(j) || emptyIndices.contains(j))
                    continue;

                ItemStack testItemStack = itemHandler.getStack(j);
                if(testItemStack.isEmpty()) {
                    emptyIndices.add(j);

                    continue;
                }

                if(ItemStack.areItemsAndComponentsEqual(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxCount() - testItemStack.getCount());

                    if(amount + testItemStack.getCount() == testItemStack.getMaxCount())
                        checkedIndices.add(j);

                    if(amount == itemStack.getCount()) {
                        itemStacks.remove(i);

                        continue outer;
                    }else {
                        itemStack.decrement(amount);
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                return false;

            int index = emptyIndices.remove(0);
            if(itemStack.getCount() == itemStack.getMaxCount())
                checkedIndices.add(index);

            itemStacks.remove(i);
        }

        return itemStacks.isEmpty();
    }

    private boolean isOutputOrCraftingRemainderOfInput(ItemStack itemStack) {
        if(craftingRecipe == null)
            return false;

        SimpleInventory patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        CraftingInventory copyOfPatternSlots = new CraftingInventory(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));

        ItemStack resultItemStack = craftingRecipe.value().craft(copyOfPatternSlots.createRecipeInput(), world.getRegistryManager());

        if(ItemStack.areItemsEqual(itemStack, resultItemStack) && ItemStack.areItemsAndComponentsEqual(itemStack, resultItemStack))
            return true;

        for(ItemStack remainingItem:craftingRecipe.value().getRecipeRemainders(copyOfPatternSlots.createRecipeInput()))
            if(ItemStack.areItemsEqual(itemStack, remainingItem) && ItemStack.areItemsAndComponentsEqual(itemStack, remainingItem))
                return true;

        return false;
    }

    private boolean isInput(ItemStack itemStack) {
        if(craftingRecipe == null)
            return false;

        for(int i = 0;i < patternSlots.size();i++)
            if(ignoreNBT?ItemStack.areItemsEqual(itemStack, patternSlots.getStack(i)):
                    (ItemStack.areItemsEqual(itemStack, patternSlots.getStack(i)) &&
                            ItemStack.areItemsAndComponentsEqual(itemStack, patternSlots.getStack(i))))
                return true;

        return false;
    }

    private SimpleInventory replaceCraftingPatternWithCurrentNBTItems(SimpleInventory container) {
        SimpleInventory copyOfContainer = new SimpleInventory(container.size());
        for(int i = 0;i < container.size();i++)
            copyOfContainer.setStack(i, container.getStack(i).copy());

        Map<Integer, Integer> usedItemCounts = new HashMap<>(); //slotIndex: usedCount
        outer:
        for(int i = 0;i < copyOfContainer.size();i++) {
            ItemStack itemStack = copyOfContainer.getStack(i);
            if(itemStack.isEmpty())
                continue;

            for(int j = 0;j < itemHandler.size();j++) {
                ItemStack testItemStack = itemHandler.getStack(j).copy();
                int usedCount = usedItemCounts.getOrDefault(j, 0);
                testItemStack.setCount(testItemStack.getCount() - usedCount);
                if(testItemStack.getCount() <= 0)
                    continue;

                if(ItemStack.areItemsAndComponentsEqual(itemStack, testItemStack)) {
                    usedItemCounts.put(j, usedCount + 1);
                    continue outer;
                }
            }

            //Same item with same tag was not found -> check for same item without same tag and change if found
            for(int j = 0;j < itemHandler.size();j++) {
                ItemStack testItemStack = itemHandler.getStack(j).copy();
                int usedCount = usedItemCounts.getOrDefault(j, 0);
                testItemStack.setCount(testItemStack.getCount() - usedCount);
                if(testItemStack.getCount() <= 0)
                    continue;

                if(ItemStack.areItemsEqual(itemStack, testItemStack)) {
                    usedItemCounts.put(j, usedCount + 1);

                    copyOfContainer.setStack(i, testItemStack.copyWithCount(1));

                    continue outer;
                }
            }

            //Not found at all -> Mot enough input items are present
            return copyOfContainer;
        }

        return copyOfContainer;
    }

    private List<RecipeEntry<CraftingRecipe>> getRecipesFor(CraftingInventory patternSlots, World level) {
        if(!(level instanceof ServerWorld serverWorld))
            return List.of();

        return RecipeUtils.getAllRecipesFor(serverWorld, RecipeType.CRAFTING).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.id().getValue())).
                filter(recipe -> recipe.value().matches(patternSlots.createRecipeInput(), level)).
                sorted(Comparator.comparing(recipe -> recipe.id().getValue())).
                toList();
    }

    private Optional<Pair<RegistryKey<Recipe<?>>, RecipeEntry<CraftingRecipe>>> getRecipeFor(CraftingInventory patternSlots, World level, RegistryKey<Recipe<?>> recipeId) {
        List<RecipeEntry<CraftingRecipe>> recipes = getRecipesFor(patternSlots, level);
        Optional<RecipeEntry<CraftingRecipe>> recipe = recipes.stream().filter(r -> recipeId != null && r.id().getValue().equals(recipeId.getValue())).findFirst();

        return recipe.or(() -> recipes.stream().findFirst()).map(r -> Pair.of(r.id(), r));
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress();

        super.updateUpgradeModules();
    }

    public void setIgnoreNBT(boolean ignoreNBT) {
        this.ignoreNBT = ignoreNBT;
        updateRecipe();
        markDirty(world, getPos(), getCachedState());
    }

    public void setSecondaryExtractMode(boolean secondaryExtractMode) {
        this.secondaryExtractMode = secondaryExtractMode;
        markDirty(world, getPos(), getCachedState());
    }

    public void setAllowOutputOverflow(boolean allowOutputOverflow) {
        this.allowOutputOverflow = allowOutputOverflow;
        markDirty(world, getPos(), getCachedState());
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