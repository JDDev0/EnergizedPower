package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.AdvancedAutoCrafterBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.*;

public class AdvancedAutoCrafterBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleInventory>
        implements CheckboxUpdate {
    private static final List<@NotNull Identifier> RECIPE_BLACKLIST = ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_RECIPE_BLACKLIST.getValue();

    public final static long ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT = ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT.getValue();

    private final static int RECIPE_DURATION = ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_RECIPE_DURATION.getValue();

    private boolean secondaryExtractMode = false;

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 5,
            i -> secondaryExtractMode?!isInput(itemHandler.getStack(i)):isOutputOrCraftingRemainderOfInput(itemHandler.getStack(i)));

    private final SimpleInventory[] patternSlots = new SimpleInventory[] {
            new SimpleInventory(3 * 3) {
                @Override
                public int getMaxCountPerStack() {
                    return 1;
                }
            },
            new SimpleInventory(3 * 3) {
                @Override
                public int getMaxCountPerStack() {
                    return 1;
                }
            },
            new SimpleInventory(3 * 3) {
                @Override
                public int getMaxCountPerStack() {
                    return 1;
                }
            }
    };
    private final SimpleInventory[] patternResultSlots = new SimpleInventory[] {
            new SimpleInventory(1),
            new SimpleInventory(1),
            new SimpleInventory(1)
    };
    private final InventoryChangedListener[] updatePatternListener = new InventoryChangedListener[] {
            container -> updateRecipe(0),
            container -> updateRecipe(1),
            container -> updateRecipe(2)
    };
    private final boolean[] hasRecipeLoaded = new boolean[] {
            false, false, false
    };
    private final Identifier[] recipeIdForSetRecipe = new Identifier[] {
            null, null, null
    };
    @SuppressWarnings("unchecked")
    private final RecipeEntry<CraftingRecipe>[] craftingRecipe = new RecipeEntry[] {
            null, null, null
    };
    private final CraftingInventory[] oldCopyOfRecipe = new CraftingInventory[] {
            null, null, null
    };
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

    private final int[] progress = new int[] {
            0, 0, 0
    };
    private final int[] maxProgress = new int[] {
            0, 0, 0
    };
    private final long[] energyConsumptionLeft = new long[] {
            -1, -1, -1
    };
    private final boolean[] hasEnoughEnergy = new boolean[] {
            false, false, false
    };
    private final boolean[] ignoreNBT = new boolean[] {
            false, false, false
    };
    private int currentRecipeIndex = 0;

    public AdvancedAutoCrafterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.ADVANCED_AUTO_CRAFTER_ENTITY, blockPos, blockState,

                "advanced_auto_crafter",

                ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_TRANSFER_RATE.getValue(),

                27,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );

        for(int i = 0;i < 3;i++)
            patternSlots[i].addListener(updatePatternListener[i]);
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
                if(slot < 0 || slot >= 27)
                    return super.isValid(slot, stack);

                //Slot 0, 1, 2, 3, and 4 are for output items only
                return slot >= 5;
            }

            @Override
            public void markDirty() {
                super.markDirty();

                AdvancedAutoCrafterBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(AdvancedAutoCrafterBlockEntity.this.progress[0], index);
                    case 2, 3 -> ByteUtils.get2Bytes(AdvancedAutoCrafterBlockEntity.this.maxProgress[0], index - 2);
                    case 4, 5 -> ByteUtils.get2Bytes(AdvancedAutoCrafterBlockEntity.this.progress[1], index - 4);
                    case 6, 7 -> ByteUtils.get2Bytes(AdvancedAutoCrafterBlockEntity.this.maxProgress[1], index - 6);
                    case 8, 9 -> ByteUtils.get2Bytes(AdvancedAutoCrafterBlockEntity.this.progress[2], index - 8);
                    case 10, 11 -> ByteUtils.get2Bytes(AdvancedAutoCrafterBlockEntity.this.maxProgress[2], index - 10);
                    case 12, 13, 14, 15 -> ByteUtils.get2Bytes(AdvancedAutoCrafterBlockEntity.this.energyConsumptionLeft[0], index - 12);
                    case 16, 17, 18, 19 -> ByteUtils.get2Bytes(AdvancedAutoCrafterBlockEntity.this.energyConsumptionLeft[1], index - 16);
                    case 20, 21, 22, 23 -> ByteUtils.get2Bytes(AdvancedAutoCrafterBlockEntity.this.energyConsumptionLeft[2], index - 20);
                    case 24 -> hasEnoughEnergy[0]?1:0;
                    case 25 -> hasEnoughEnergy[1]?1:0;
                    case 26 -> hasEnoughEnergy[2]?1:0;
                    case 27 -> ignoreNBT[0]?1:0;
                    case 28 -> ignoreNBT[1]?1:0;
                    case 29 -> ignoreNBT[2]?1:0;
                    case 30 -> secondaryExtractMode?1:0;
                    case 31 -> currentRecipeIndex;
                    case 32 -> redstoneMode.ordinal();
                    case 33 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> AdvancedAutoCrafterBlockEntity.this.progress[0] = ByteUtils.with2Bytes(
                            AdvancedAutoCrafterBlockEntity.this.progress[0], (short)value, index
                    );
                    case 2, 3 -> AdvancedAutoCrafterBlockEntity.this.maxProgress[0] = ByteUtils.with2Bytes(
                            AdvancedAutoCrafterBlockEntity.this.maxProgress[0], (short)value, index - 2
                    );
                    case 4, 5 -> AdvancedAutoCrafterBlockEntity.this.progress[1] = ByteUtils.with2Bytes(
                            AdvancedAutoCrafterBlockEntity.this.progress[1], (short)value, index - 4
                    );
                    case 6, 7 -> AdvancedAutoCrafterBlockEntity.this.maxProgress[1] = ByteUtils.with2Bytes(
                            AdvancedAutoCrafterBlockEntity.this.maxProgress[1], (short)value, index - 6
                    );
                    case 8, 9 -> AdvancedAutoCrafterBlockEntity.this.progress[2] = ByteUtils.with2Bytes(
                            AdvancedAutoCrafterBlockEntity.this.progress[2], (short)value, index - 8
                    );
                    case 10, 11 -> AdvancedAutoCrafterBlockEntity.this.maxProgress[2] = ByteUtils.with2Bytes(
                            AdvancedAutoCrafterBlockEntity.this.maxProgress[2], (short)value, index - 10
                    );
                    case 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 -> {}
                    case 27 -> AdvancedAutoCrafterBlockEntity.this.ignoreNBT[0] = value != 0;
                    case 28 -> AdvancedAutoCrafterBlockEntity.this.ignoreNBT[1] = value != 0;
                    case 29 -> AdvancedAutoCrafterBlockEntity.this.ignoreNBT[2] = value != 0;
                    case 30 -> AdvancedAutoCrafterBlockEntity.this.secondaryExtractMode = value != 0;
                    case 31 -> AdvancedAutoCrafterBlockEntity.this.currentRecipeIndex = value;
                    case 32 -> AdvancedAutoCrafterBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 33 -> AdvancedAutoCrafterBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 34;
            }
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);

        return new AdvancedAutoCrafterMenu(id, this, inventory, itemHandler, upgradeModuleInventory, patternSlots, patternResultSlots, data);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        for(int i = 0;i < 3;i++)
            nbt.put("pattern." + i, savePatternContainer(i, registries));

        for(int i = 0;i < 3;i++) {
            if(craftingRecipe[i] != null)
                nbt.put("recipe.id." + i, NbtString.of(craftingRecipe[i].id().toString()));

            nbt.put("recipe.progress." + i, NbtInt.of(progress[i]));
            nbt.put("recipe.max_progress." + i, NbtInt.of(maxProgress[i]));
            nbt.put("recipe.energy_consumption_left." + i, NbtLong.of(energyConsumptionLeft[i]));

            nbt.putBoolean("ignore_nbt." + i, ignoreNBT[i]);
        }

        nbt.putBoolean("secondary_extract_mode", secondaryExtractMode);

        nbt.putInt("current_recipe_index", currentRecipeIndex);

    }

    private NbtElement savePatternContainer(int index, RegistryWrapper.WrapperLookup registries) {
        return Inventories.writeNbt(new NbtCompound(), patternSlots[index].heldStacks, registries);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        super.readNbt(nbt, registries);

        for(int i = 0;i < 3;i++)
            loadPatternContainer(i, nbt.getCompound("pattern." + i), registries);

        for(int i = 0;i < 3;i++) {
            if(nbt.contains("recipe.id." + i)) {
                NbtElement tag = nbt.get("recipe.id." + i);

                if(!(tag instanceof NbtString stringTag))
                    throw new IllegalArgumentException("Tag must be of type StringTag!");

                recipeIdForSetRecipe[i] = Identifier.tryParse(stringTag.asString());
            }

            progress[i] = nbt.getInt("recipe.progress." + i);
            maxProgress[i] = nbt.getInt("recipe.max_progress." + i);
            energyConsumptionLeft[i] = nbt.getLong("recipe.energy_consumption_left." + i);

            ignoreNBT[i] = nbt.getBoolean("ignore_nbt." + i);
        }

        secondaryExtractMode = nbt.getBoolean("secondary_extract_mode");

        currentRecipeIndex = nbt.getInt("current_recipe_index");
        if(currentRecipeIndex < 0 || currentRecipeIndex >= 3)
            currentRecipeIndex = 0;
    }

    private void loadPatternContainer(int index, NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
        patternSlots[index].removeListener(updatePatternListener[index]);

        Inventories.readNbt(tag, patternSlots[index].heldStacks, registries);

        patternSlots[index].addListener(updatePatternListener[index]);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AdvancedAutoCrafterBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.redstoneMode.isActive(state.get(AdvancedAutoCrafterBlock.POWERED)))
            return;

        for(int i = 0;i < 3;i++) {
            if(!blockEntity.hasRecipeLoaded[i]) {
                blockEntity.updateRecipe(i);

                if(blockEntity.craftingRecipe[i] == null)
                    blockEntity.resetProgress(i);

                markDirty(level, blockPos, state);
            }

            int itemCount = 0;
            for(int j = 0;j < blockEntity.patternSlots[i].size();j++)
                if(!blockEntity.patternSlots[i].getStack(j).isEmpty())
                    itemCount++;

            //Ignore empty recipes
            if(itemCount == 0)
                continue;

            if(blockEntity.craftingRecipe[i] != null && (blockEntity.progress[i] > 0 ||
                    (blockEntity.canInsertItemsIntoOutputSlots(i) && blockEntity.canExtractItemsFromInput(i)))) {
                if(!blockEntity.canInsertItemsIntoOutputSlots(i) || !blockEntity.canExtractItemsFromInput(i))
                    continue;

                if(blockEntity.maxProgress[i] == 0)
                    blockEntity.maxProgress[i] = Math.max(1, (int)Math.ceil(RECIPE_DURATION /
                            blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));

                long energyConsumptionPerTick = Math.max(1, (long)Math.ceil(itemCount * ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT *
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

                if(blockEntity.progress[i] == 0) {
                    if(!blockEntity.canExtractItemsFromInput(i))
                        continue;

                    blockEntity.energyConsumptionLeft[i] = energyConsumptionPerTick * blockEntity.maxProgress[i];
                }

                if(blockEntity.progress[i] < 0 || blockEntity.maxProgress[i] < 0 || blockEntity.energyConsumptionLeft[i] < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    markDirty(level, blockPos, state);

                    continue;
                }

                if(energyConsumptionPerTick <= blockEntity.limitingEnergyStorage.getAmount()) {
                    try(Transaction transaction = Transaction.openOuter()) {
                        blockEntity.limitingEnergyStorage.extract(energyConsumptionPerTick, transaction);
                        transaction.commit();
                    }

                    blockEntity.energyConsumptionLeft[i] -= energyConsumptionPerTick;

                    blockEntity.progress[i]++;

                    if(blockEntity.progress[i] >= blockEntity.maxProgress[i]) {
                        SimpleInventory patternSlotsForRecipe = blockEntity.ignoreNBT[i]?
                                blockEntity.replaceCraftingPatternWithCurrentNBTItems(blockEntity.patternSlots[i]):blockEntity.patternSlots[i];
                        CraftingInventory copyOfPatternSlots = new CraftingInventory(blockEntity.dummyContainerMenu, 3, 3);
                        for(int j = 0;j < patternSlotsForRecipe.size();j++)
                            copyOfPatternSlots.setStack(j, patternSlotsForRecipe.getStack(j));

                        blockEntity.extractItems(i);
                        blockEntity.craftItem(i, copyOfPatternSlots);
                    }

                    markDirty(level, blockPos, state);
                }else {
                    blockEntity.hasEnoughEnergy[i] = false;
                    markDirty(level, blockPos, state);
                }
            }else {
                blockEntity.resetProgress(i);
                markDirty(level, blockPos, state);
            }
        }
    }

    private void resetProgress(int index) {
        progress[index] = 0;
        maxProgress[index] = 0;
        energyConsumptionLeft[index] = -1;
        hasEnoughEnergy[index] = true;
    }

    public void resetProgressAndMarkAsChanged(int index) {
        resetProgress(index);
        markDirty(world, getPos(), getCachedState());
    }

    public void cycleRecipe() {
        SimpleInventory patternSlotsForRecipe = ignoreNBT[currentRecipeIndex]?
                replaceCraftingPatternWithCurrentNBTItems(patternSlots[currentRecipeIndex]):patternSlots[currentRecipeIndex];
        CraftingInventory copyOfPatternSlots = new CraftingInventory(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));

        List<RecipeEntry<CraftingRecipe>> recipes = getRecipesFor(copyOfPatternSlots, world);

        //No recipe found
        if(recipes.isEmpty()) {
            updateRecipe(currentRecipeIndex);

            return;
        }

        if(recipeIdForSetRecipe[currentRecipeIndex] == null)
            recipeIdForSetRecipe[currentRecipeIndex] = (craftingRecipe[currentRecipeIndex] == null ||
                    craftingRecipe[currentRecipeIndex].id() == null)?recipes.get(0).id():craftingRecipe[currentRecipeIndex].id();

        for(int i = 0;i < recipes.size();i++) {
            if(Objects.equals(recipes.get(i).id(), recipeIdForSetRecipe[currentRecipeIndex])) {
                recipeIdForSetRecipe[currentRecipeIndex] = recipes.get((i + 1) % recipes.size()).id();

                break;
            }
        }

        updateRecipe(currentRecipeIndex);
    }

    public void setRecipeIdForSetRecipe(Identifier recipeIdForSetRecipe) {
        this.recipeIdForSetRecipe[currentRecipeIndex] = recipeIdForSetRecipe;

        updateRecipe(currentRecipeIndex);
    }

    private void updateRecipe(int index) {
        if(world == null)
            return;

        RecipeEntry<CraftingRecipe> oldRecipe = null;
        ItemStack oldResult = null;
        if(hasRecipeLoaded[index] && craftingRecipe[index] != null && oldCopyOfRecipe[index] != null) {
            oldRecipe = craftingRecipe[index];

            oldResult = craftingRecipe[index].value() instanceof SpecialCraftingRecipe ?craftingRecipe[index].value().
                    craft(oldCopyOfRecipe[index], world.getRegistryManager()):
                    craftingRecipe[index].value().getResult(world.getRegistryManager());
        }

        hasRecipeLoaded[index] = true;

        SimpleInventory patternSlotsForRecipe = ignoreNBT[index]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[index]):patternSlots[index];
        CraftingInventory copyOfPatternSlots = new CraftingInventory(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));

        Optional<Pair<Identifier, RecipeEntry<CraftingRecipe>>> recipe = getRecipeFor(copyOfPatternSlots, world, recipeIdForSetRecipe[index]);
        if(recipe.isPresent()) {
            craftingRecipe[index] = recipe.get().getSecond();

            //Recipe with saved recipe id does not exist or pattern items are not compatible with recipe
            if(recipeIdForSetRecipe[index] != null && !Objects.equals(craftingRecipe[index].id(), recipeIdForSetRecipe[index])) {
                recipeIdForSetRecipe[index] = craftingRecipe[index].id();
                resetProgress(index);
            }

            ItemStack resultItemStack = craftingRecipe[index].value() instanceof SpecialCraftingRecipe?craftingRecipe[index].value().
                    craft(copyOfPatternSlots, world.getRegistryManager()):
                    craftingRecipe[index].value().getResult(world.getRegistryManager());

            patternResultSlots[index].setStack(0, resultItemStack);

            if(oldRecipe != null && oldResult != null && oldCopyOfRecipe[index] != null &&
                    (craftingRecipe[index] != oldRecipe || ItemStack.areItemsAndComponentsEqual(resultItemStack, oldResult)))
                resetProgress(index);

            oldCopyOfRecipe[index] = new CraftingInventory(dummyContainerMenu, 3, 3);
            for(int i = 0;i < patternSlotsForRecipe.size();i++)
                oldCopyOfRecipe[index].setStack(i, copyOfPatternSlots.getStack(i).copy());
        }else {
            recipeIdForSetRecipe[index] = null;

            craftingRecipe[index] = null;

            patternResultSlots[index].setStack(0, ItemStack.EMPTY);

            oldCopyOfRecipe[index] = null;

            resetProgress(index);
        }
    }

    private void extractItems(int index) {
        SimpleInventory patternSlotsForRecipe = ignoreNBT[index]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[index]):
                patternSlots[index];
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

    private void craftItem(int index, CraftingInventory copyOfPatternSlots) {
        if(craftingRecipe[index] == null) {
            resetProgress(index);

            return;
        }

        List<ItemStack> outputItemStacks = new ArrayList<>(10);

        ItemStack resultItemStack = craftingRecipe[index].value() instanceof SpecialCraftingRecipe?craftingRecipe[index].value().
                craft(copyOfPatternSlots, world.getRegistryManager()):
                craftingRecipe[index].value().getResult(world.getRegistryManager());

        outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe[index].value().getRemainder(copyOfPatternSlots))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacksInsert = ItemStackUtils.combineItemStacks(outputItemStacks);

        List<Integer> emptyIndices = new ArrayList<>(27);
        outer:
        for(ItemStack itemStack:itemStacksInsert) {
            for(int i = 0;i < itemHandler.size();i++) {
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

        if(ignoreNBT[index])
            updateRecipe(index);

        resetProgress(index);
    }

    private boolean canExtractItemsFromInput(int index) {
        if(craftingRecipe[index] == null)
            return false;

        SimpleInventory patternSlotsForRecipe = ignoreNBT[index]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[index]):
                patternSlots[index];
        List<ItemStack> patternItemStacks = new ArrayList<>(9);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            if(!patternSlotsForRecipe.getStack(i).isEmpty())
                patternItemStacks.add(patternSlotsForRecipe.getStack(i));

        List<ItemStack> itemStacks = ItemStackUtils.combineItemStacks(patternItemStacks);

        List<Integer> checkedIndices = new ArrayList<>(27);
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

    private boolean canInsertItemsIntoOutputSlots(int index) {
        if(craftingRecipe[index] == null)
            return false;

        SimpleInventory patternSlotsForRecipe = ignoreNBT[index]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[index]):
                patternSlots[index];
        CraftingInventory copyOfPatternSlots = new CraftingInventory(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));

        List<ItemStack> outputItemStacks = new ArrayList<>(10);
        ItemStack resultItemStack = craftingRecipe[index].value() instanceof SpecialCraftingRecipe?craftingRecipe[index].value().
                craft(copyOfPatternSlots, world.getRegistryManager()):
                craftingRecipe[index].value().getResult(world.getRegistryManager());

        if(!resultItemStack.isEmpty())
            outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe[index].value().getRemainder(copyOfPatternSlots))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacks = ItemStackUtils.combineItemStacks(outputItemStacks);

        List<Integer> checkedIndices = new ArrayList<>(27);
        List<Integer> emptyIndices = new ArrayList<>(27);
        outer:
        for(int i = itemStacks.size() - 1;i >= 0;i--) {
            ItemStack itemStack = itemStacks.get(i);
            for(int j = 0;j < itemHandler.size();j++) {
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

            int emptyIndex = emptyIndices.remove(0);
            if(itemStack.getCount() == itemStack.getMaxCount())
                checkedIndices.add(emptyIndex);

            itemStacks.remove(i);
        }

        return itemStacks.isEmpty();
    }

    private boolean isOutputOrCraftingRemainderOfInput(ItemStack itemStack) {
        for(int i = 0;i < 3;i++) {
            if(craftingRecipe[i] == null)
                continue;

            SimpleInventory patternSlotsForRecipe = ignoreNBT[i]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[i]):
                    patternSlots[i];
            CraftingInventory copyOfPatternSlots = new CraftingInventory(dummyContainerMenu, 3, 3);
            for(int j = 0;j < patternSlotsForRecipe.size();j++)
                copyOfPatternSlots.setStack(j, patternSlotsForRecipe.getStack(j));

            ItemStack resultItemStack = craftingRecipe[i].value() instanceof SpecialCraftingRecipe?
                    craftingRecipe[i].value().craft(copyOfPatternSlots, world.getRegistryManager()):
                    craftingRecipe[i].value().getResult(world.getRegistryManager());

            if(ItemStack.areItemsEqual(itemStack, resultItemStack) && ItemStack.areItemsAndComponentsEqual(itemStack, resultItemStack))
                return true;

            for(ItemStack remainingItem:craftingRecipe[i].value().getRemainder(copyOfPatternSlots))
                if(ItemStack.areItemsEqual(itemStack, remainingItem) && ItemStack.areItemsAndComponentsEqual(itemStack, remainingItem))
                    return true;
        }

        return false;
    }


    private boolean isInput(ItemStack itemStack) {
        for(int i = 0;i < 3;i++) {
            if(craftingRecipe[i] == null)
                continue;

            for(int j = 0;j < patternSlots[i].size();j++)
                if(ignoreNBT[i]?ItemStack.areItemsEqual(itemStack, patternSlots[i].getStack(j)):
                        (ItemStack.areItemsEqual(itemStack, patternSlots[i].getStack(j)) &&
                                ItemStack.areItemsAndComponentsEqual(itemStack, patternSlots[i].getStack(j))))
                    return true;
        }

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
        return level.getRecipeManager().listAllOfType(RecipeType.CRAFTING).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.id())).
                filter(recipe -> recipe.value().matches(patternSlots, level)).
                sorted(Comparator.comparing(recipe -> recipe.value().getResult(level.getRegistryManager()).getTranslationKey())).
                toList();
    }

    private Optional<Pair<Identifier, RecipeEntry<CraftingRecipe>>> getRecipeFor(CraftingInventory patternSlots, World level, Identifier recipeId) {
        List<RecipeEntry<CraftingRecipe>> recipes = getRecipesFor(patternSlots, level);
        Optional<RecipeEntry<CraftingRecipe>> recipe = recipes.stream().filter(r -> r.id().equals(recipeId)).findFirst();

        return recipe.or(() -> recipes.stream().findFirst()).map(r -> Pair.of(r.id(), r));
    }

    @Override
    protected void updateUpgradeModules() {
        for(int i = 0;i < 3;i++)
            resetProgress(i);

        super.updateUpgradeModules();
    }

    public void setCurrentRecipeIndex(int currentRecipeIndex) {
        if(currentRecipeIndex < 0 || currentRecipeIndex >= 3)
            currentRecipeIndex = 0;

        this.currentRecipeIndex = currentRecipeIndex;
        markDirty(world, getPos(), getCachedState());
    }

    public void setIgnoreNBT(boolean ignoreNBT) {
        this.ignoreNBT[currentRecipeIndex] = ignoreNBT;
        updateRecipe(currentRecipeIndex);
        markDirty(world, getPos(), getCachedState());
    }

    public void setSecondaryExtractMode(boolean secondaryExtractMode) {
        this.secondaryExtractMode = secondaryExtractMode;
        markDirty(world, getPos(), getCachedState());
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Ignore NBT
            case 0 -> setIgnoreNBT(checked);

            //Secondary extract mode
            case 1 -> setSecondaryExtractMode(checked);
        }
    }
}