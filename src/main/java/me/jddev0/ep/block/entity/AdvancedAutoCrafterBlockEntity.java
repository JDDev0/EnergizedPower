package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.AdvancedAutoCrafterBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AdvancedAutoCrafterBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage, ItemStackHandler>
        implements CheckboxUpdate {
    private static final List<@NotNull ResourceLocation> RECIPE_BLACKLIST = ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_RECIPE_BLACKLIST.getValue();

    private final static int RECIPE_DURATION = ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_RECIPE_DURATION.getValue();

    private boolean secondaryExtractMode = false;
    private boolean allowOutputOverflow = true;

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 5,
                    i -> secondaryExtractMode?!isInput(itemHandler.getStackInSlot(i)):isOutputOrCraftingRemainderOfInput(itemHandler.getStackInSlot(i))));

    private final SimpleContainer[] patternSlots = new SimpleContainer[] {
            new SimpleContainer(3 * 3) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            },
            new SimpleContainer(3 * 3) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            },
            new SimpleContainer(3 * 3) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            }
    };
    private final SimpleContainer[] patternResultSlots = new SimpleContainer[] {
            new SimpleContainer(1),
            new SimpleContainer(1),
            new SimpleContainer(1)
    };
    private final ContainerListener[] updatePatternListener = new ContainerListener[] {
            container -> updateRecipe(0),
            container -> updateRecipe(1),
            container -> updateRecipe(2)
    };
    private final boolean[] hasRecipeLoaded = new boolean[] {
            false, false, false
    };
    private final ResourceLocation[] recipeIdForSetRecipe = new ResourceLocation[] {
            null, null, null
    };
    private final CraftingRecipe[] craftingRecipe = new CraftingRecipe[] {
            null, null, null
    };
    private final CraftingContainer[] oldCopyOfRecipe = new CraftingContainer[] {
            null, null, null
    };
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

    public final static int ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT =
            ModConfigs.COMMON_ADVANCED_AUTO_CRAFTER_ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT.getValue();

    private final int[] progress = new int[] {
            0, 0, 0
    };
    private final int[] maxProgress = new int[] {
            0, 0, 0
    };
    private final int[] energyConsumptionLeft = new int[] {
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
                EPBlockEntities.ADVANCED_AUTO_CRAFTER_ENTITY.get(), blockPos, blockState,

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
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            public int getMaxReceive() {
                return Math.max(1, (int)Math.ceil(maxReceive * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }

            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if(slot < 0 || slot >= 27)
                    return super.isItemValid(slot, stack);

                //Slot 0, 1, 2, 3, and 4 are for output items only
                return slot >= 5;
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress[0], value -> progress[0] = value),
                new ProgressValueContainerData(() -> progress[1], value -> progress[1] = value),
                new ProgressValueContainerData(() -> progress[2], value -> progress[2] = value),
                new ProgressValueContainerData(() -> maxProgress[0], value -> maxProgress[0] = value),
                new ProgressValueContainerData(() -> maxProgress[1], value -> maxProgress[1] = value),
                new ProgressValueContainerData(() -> maxProgress[2], value -> maxProgress[2] = value),
                new EnergyValueContainerData(this::getEnergyConsumptionPerTickSum, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[0], value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[1], value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[2], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[0], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[1], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[2], value -> {}),
                new BooleanValueContainerData(() -> ignoreNBT[0], value -> ignoreNBT[0] = value),
                new BooleanValueContainerData(() -> ignoreNBT[1], value -> ignoreNBT[1] = value),
                new BooleanValueContainerData(() -> ignoreNBT[2], value -> ignoreNBT[2] = value),
                new BooleanValueContainerData(() -> secondaryExtractMode, value -> secondaryExtractMode = value),
                new BooleanValueContainerData(() -> allowOutputOverflow, value -> allowOutputOverflow = value),
                new ShortValueContainerData(() -> (short)currentRecipeIndex, value -> currentRecipeIndex = value),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new AdvancedAutoCrafterMenu(id, inventory, this, upgradeModuleInventory, patternSlots, patternResultSlots, data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        for(int i = 0;i < 3;i++) {
            NonNullList<ItemStack> items = NonNullList.withSize(patternSlots[i].getContainerSize(), ItemStack.EMPTY);
            for(int j = 0;j < patternSlots[i].getContainerSize();j++)
                items.set(j, patternSlots[i].getItem(j));
            nbt.put("pattern." + i, ContainerHelper.saveAllItems(new CompoundTag(), items));
        }

        for(int i = 0;i < 3;i++) {
            if(craftingRecipe[i] != null)
                nbt.put("recipe.id." + i, StringTag.valueOf(craftingRecipe[i].getId().toString()));

            nbt.put("recipe.progress." + i, IntTag.valueOf(progress[i]));
            nbt.put("recipe.max_progress." + i, IntTag.valueOf(maxProgress[i]));
            nbt.put("recipe.energy_consumption_left." + i, IntTag.valueOf(energyConsumptionLeft[i]));

            nbt.putBoolean("ignore_nbt." + i, ignoreNBT[i]);
        }

        nbt.putBoolean("secondary_extract_mode", secondaryExtractMode);
        nbt.putBoolean("allow_output_overflow", allowOutputOverflow);

        nbt.putInt("current_recipe_index", currentRecipeIndex);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        for(int i = 0;i < 3;i++) {
            patternSlots[i].removeListener(updatePatternListener[i]);
            NonNullList<ItemStack> items = NonNullList.withSize(patternSlots[i].getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(nbt.getCompound("pattern." + i), items);
            for(int j = 0;j < patternSlots[i].getContainerSize();j++)
                patternSlots[i].setItem(j, items.get(j));
            patternSlots[i].addListener(updatePatternListener[i]);
        }

        for(int i = 0;i < 3;i++) {
            if(nbt.contains("recipe.id." + i)) {
                Tag tag = nbt.get("recipe.id." + i);

                if(!(tag instanceof StringTag stringTag))
                    throw new IllegalArgumentException("Tag must be of type StringTag!");

                recipeIdForSetRecipe[i] = ResourceLocation.tryParse(stringTag.getAsString());
            }

            progress[i] = nbt.getInt("recipe.progress." + i);
            maxProgress[i] = nbt.getInt("recipe.max_progress." + i);
            energyConsumptionLeft[i] = nbt.getInt("recipe.energy_consumption_left." + i);

            ignoreNBT[i] = nbt.getBoolean("ignore_nbt." + i);
        }

        secondaryExtractMode = nbt.getBoolean("secondary_extract_mode");
        allowOutputOverflow = !nbt.contains("allow_output_overflow") || nbt.getBoolean("allow_output_overflow");

        currentRecipeIndex = nbt.getInt("current_recipe_index");
        if(currentRecipeIndex < 0 || currentRecipeIndex >= 3)
            currentRecipeIndex = 0;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AdvancedAutoCrafterBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(AdvancedAutoCrafterBlock.POWERED)))
            return;

        for(int i = 0;i < 3;i++) {
            if(!blockEntity.hasRecipeLoaded[i]) {
                blockEntity.updateRecipe(i);

                if(blockEntity.craftingRecipe[i] == null)
                    blockEntity.resetProgress(i);

                setChanged(level, blockPos, state);
            }

            int itemCount = 0;
            for(int j = 0;j < blockEntity.patternSlots[i].getContainerSize();j++)
                if(!blockEntity.patternSlots[i].getItem(j).isEmpty())
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

                int energyConsumptionPerTick = Math.max(1, (int)Math.ceil(itemCount * ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT *
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

                if(blockEntity.progress[i] == 0) {
                    if(!blockEntity.canExtractItemsFromInput(i))
                        continue;

                    blockEntity.energyConsumptionLeft[i] = energyConsumptionPerTick * blockEntity.maxProgress[i];
                }

                if(blockEntity.progress[i] < 0 || blockEntity.maxProgress[i] < 0 || blockEntity.energyConsumptionLeft[i] < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    setChanged(level, blockPos, state);

                    continue;
                }

                if(energyConsumptionPerTick <= blockEntity.energyStorage.getEnergy()) {
                    blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);
                    blockEntity.energyConsumptionLeft[i] -= energyConsumptionPerTick;

                    blockEntity.progress[i]++;

                    if(blockEntity.progress[i] >= blockEntity.maxProgress[i]) {
                        SimpleContainer patternSlotsForRecipe = blockEntity.ignoreNBT[i]?
                                blockEntity.replaceCraftingPatternWithCurrentNBTItems(blockEntity.patternSlots[i]):blockEntity.patternSlots[i];
                        CraftingContainer copyOfPatternSlots = new TransientCraftingContainer(blockEntity.dummyContainerMenu, 3, 3);
                        for(int j = 0;j < patternSlotsForRecipe.getContainerSize();j++)
                            copyOfPatternSlots.setItem(j, patternSlotsForRecipe.getItem(j));

                        blockEntity.extractItems(i);
                        blockEntity.craftItem(i, copyOfPatternSlots);
                    }

                    setChanged(level, blockPos, state);
                }else {
                    blockEntity.hasEnoughEnergy[i] = false;
                    setChanged(level, blockPos, state);
                }
            }else {
                blockEntity.resetProgress(i);
                setChanged(level, blockPos, state);
            }
        }
    }
    
    protected final int getEnergyConsumptionPerTickSum() {
        int energyConsumptionSum = -1;

        for(int i = 0;i < 3;i++) {
            int itemCount = 0;
            for(int j = 0;j < patternSlots[i].getContainerSize();j++)
                if(!patternSlots[i].getItem(j).isEmpty())
                    itemCount++;

            //Ignore empty recipes
            if(itemCount == 0 || craftingRecipe[i] == null || progress[i] <= 0)
                continue;

            int energyConsumption = Math.max(1, (int)Math.ceil(itemCount * ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT *
                    upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

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
        progress[index] = 0;
        maxProgress[index] = 0;
        energyConsumptionLeft[index] = -1;
        hasEnoughEnergy[index] = true;
    }

    public void resetProgressAndMarkAsChanged(int index) {
        resetProgress(index);
        setChanged(level, getBlockPos(), getBlockState());
    }

    public void cycleRecipe() {
        SimpleContainer patternSlotsForRecipe = ignoreNBT[currentRecipeIndex]?
                replaceCraftingPatternWithCurrentNBTItems(patternSlots[currentRecipeIndex]):patternSlots[currentRecipeIndex];
        CraftingContainer copyOfPatternSlots = new TransientCraftingContainer(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            copyOfPatternSlots.setItem(i, patternSlotsForRecipe.getItem(i));

        List<CraftingRecipe> recipes = getRecipesFor(copyOfPatternSlots, level);

        //No recipe found
        if(recipes.isEmpty()) {
            updateRecipe(currentRecipeIndex);

            return;
        }

        if(recipeIdForSetRecipe[currentRecipeIndex] == null)
            recipeIdForSetRecipe[currentRecipeIndex] = (craftingRecipe[currentRecipeIndex] == null ||
                    craftingRecipe[currentRecipeIndex].getId() == null)?recipes.get(0).getId():craftingRecipe[currentRecipeIndex].getId();

        for(int i = 0;i < recipes.size();i++) {
            if(Objects.equals(recipes.get(i).getId(), recipeIdForSetRecipe[currentRecipeIndex])) {
                recipeIdForSetRecipe[currentRecipeIndex] = recipes.get((i + 1) % recipes.size()).getId();

                break;
            }
        }

        updateRecipe(currentRecipeIndex);
    }

    public void setRecipeIdForSetRecipe(ResourceLocation recipeIdForSetRecipe) {
        this.recipeIdForSetRecipe[currentRecipeIndex] = recipeIdForSetRecipe;

        updateRecipe(currentRecipeIndex);
    }

    private void updateRecipe(int index) {
        if(level == null)
            return;

        CraftingRecipe oldRecipe = null;
        ItemStack oldResult = null;
        if(hasRecipeLoaded[index] && craftingRecipe[index] != null && oldCopyOfRecipe[index] != null) {
            oldRecipe = craftingRecipe[index];

            oldResult = craftingRecipe[index] instanceof CustomRecipe?craftingRecipe[index].
                    assemble(oldCopyOfRecipe[index], level.registryAccess()):
                    craftingRecipe[index].getResultItem(level.registryAccess());
        }

        hasRecipeLoaded[index] = true;

        SimpleContainer patternSlotsForRecipe = ignoreNBT[index]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[index]):patternSlots[index];
        CraftingContainer copyOfPatternSlots = new TransientCraftingContainer(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            copyOfPatternSlots.setItem(i, patternSlotsForRecipe.getItem(i));

        Optional<Pair<ResourceLocation, CraftingRecipe>> recipe = getRecipeFor(copyOfPatternSlots, level, recipeIdForSetRecipe[index]);
        if(recipe.isPresent()) {
            craftingRecipe[index] = recipe.get().getSecond();

            //Recipe with saved recipe id does not exist or pattern items are not compatible with recipe
            if(recipeIdForSetRecipe[index] != null && !Objects.equals(craftingRecipe[index].getId(), recipeIdForSetRecipe[index])) {
                recipeIdForSetRecipe[index] = craftingRecipe[index].getId();
                resetProgress(index);
            }

            ItemStack resultItemStack = craftingRecipe[index] instanceof CustomRecipe?craftingRecipe[index].
                    assemble(copyOfPatternSlots, level.registryAccess()):
                    craftingRecipe[index].getResultItem(level.registryAccess());

            patternResultSlots[index].setItem(0, resultItemStack);

            if(oldRecipe != null && oldResult != null && oldCopyOfRecipe[index] != null &&
                    (craftingRecipe[index] != oldRecipe || !ItemStack.isSameItemSameTags(resultItemStack, oldResult)))
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
            for(int i = 0;i < itemHandler.getSlots();i++) {
                ItemStack testItemStack = itemHandler.getStackInSlot(i);
                if(ItemStack.isSameItemSameTags(itemStack, testItemStack)) {
                    ItemStack ret = itemHandler.extractItem(i, itemStack.getCount(), false);
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

        ItemStack resultItemStack = craftingRecipe[index] instanceof CustomRecipe?craftingRecipe[index].
                assemble(copyOfPatternSlots, level.registryAccess()):
                craftingRecipe[index].getResultItem(level.registryAccess());

        outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe[index].getRemainingItems(copyOfPatternSlots))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacksInsert = ItemStackUtils.combineItemStacks(outputItemStacks);

        int outputSlotCount = allowOutputOverflow?itemHandler.getSlots():5;
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

                if(ItemStack.isSameItemSameTags(itemStack, testItemStack)) {
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

            for(int j = 0;j < itemHandler.getSlots();j++) {
                if(checkedIndices.contains(j))
                    continue;

                ItemStack testItemStack = itemHandler.getStackInSlot(j);
                if(testItemStack.isEmpty()) {
                    checkedIndices.add(j);

                    continue;
                }

                if(ItemStack.isSameItemSameTags(itemStack, testItemStack)) {
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
        ItemStack resultItemStack = craftingRecipe[index] instanceof CustomRecipe?craftingRecipe[index].
                assemble(copyOfPatternSlots, level.registryAccess()):
                craftingRecipe[index].getResultItem(level.registryAccess());

        if(!resultItemStack.isEmpty())
            outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe[index].getRemainingItems(copyOfPatternSlots))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacks = ItemStackUtils.combineItemStacks(outputItemStacks);

        int outputSlotCount = allowOutputOverflow?itemHandler.getSlots():5;
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

                if(ItemStack.isSameItemSameTags(itemStack, testItemStack)) {
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
        for(int i = 0;i < 3;i++) {
            if(craftingRecipe[i] == null)
                continue;

            SimpleContainer patternSlotsForRecipe = ignoreNBT[i]?replaceCraftingPatternWithCurrentNBTItems(patternSlots[i]):
                    patternSlots[i];
            CraftingContainer copyOfPatternSlots = new TransientCraftingContainer(dummyContainerMenu, 3, 3);
            for(int j = 0;j < patternSlotsForRecipe.getContainerSize();j++)
                copyOfPatternSlots.setItem(j, patternSlotsForRecipe.getItem(j));

            ItemStack resultItemStack = craftingRecipe[i] instanceof CustomRecipe?
                    craftingRecipe[i].assemble(copyOfPatternSlots, level.registryAccess()):
                    craftingRecipe[i].getResultItem(level.registryAccess());

            if(ItemStack.isSameItemSameTags(itemStack, resultItemStack))
                return true;

            for(ItemStack remainingItem:craftingRecipe[i].getRemainingItems(copyOfPatternSlots))
                if(ItemStack.isSameItemSameTags(itemStack, remainingItem))
                    return true;
        }

        return false;
    }


    private boolean isInput(ItemStack itemStack) {
        for(int i = 0;i < 3;i++) {
            if(craftingRecipe[i] == null)
                continue;

            for(int j = 0;j < patternSlots[i].getContainerSize();j++)
                if(ignoreNBT[i]?ItemStack.isSameItem(itemStack, patternSlots[i].getItem(j)):
                        ItemStack.isSameItemSameTags(itemStack, patternSlots[i].getItem(j)))
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

            for(int j = 0;j < itemHandler.getSlots();j++) {
                ItemStack testItemStack = itemHandler.getStackInSlot(j).copy();
                int usedCount = usedItemCounts.getOrDefault(j, 0);
                testItemStack.setCount(testItemStack.getCount() - usedCount);
                if(testItemStack.getCount() <= 0)
                    continue;

                if(ItemStack.isSameItemSameTags(itemStack, testItemStack)) {
                    usedItemCounts.put(j, usedCount + 1);
                    continue outer;
                }
            }

            //Same item with same tag was not found -> check for same item without same tag and change if found
            for(int j = 0;j < itemHandler.getSlots();j++) {
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

    private List<CraftingRecipe> getRecipesFor(CraftingContainer patternSlots, Level level) {
        return level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.getId())).
                filter(recipe -> recipe.matches(patternSlots, level)).
                sorted(Comparator.comparing(recipe -> recipe.getResultItem(level.registryAccess()).getDescriptionId())).
                toList();
    }

    private Optional<Pair<ResourceLocation, CraftingRecipe>> getRecipeFor(CraftingContainer patternSlots, Level level, ResourceLocation recipeId) {
        List<CraftingRecipe> recipes = getRecipesFor(patternSlots, level);
        Optional<CraftingRecipe> recipe = recipes.stream().filter(r -> r.getId().equals(recipeId)).findFirst();

        return recipe.or(() -> recipes.stream().findFirst()).map(r -> Pair.of(r.getId(), r));
    }

    @Override
    protected void updateUpgradeModules() {
        for(int i = 0;i < 3;i++)
            resetProgress(i);

        super.updateUpgradeModules();
    }

    public int getCurrentRecipeIndex() {
        return currentRecipeIndex;
    }

    public void setCurrentRecipeIndex(int currentRecipeIndex) {
        if(currentRecipeIndex < 0 || currentRecipeIndex >= 3)
            currentRecipeIndex = 0;

        this.currentRecipeIndex = currentRecipeIndex;
        setChanged(level, getBlockPos(), getBlockState());
    }

    public void setIgnoreNBT(boolean ignoreNBT) {
        this.ignoreNBT[currentRecipeIndex] = ignoreNBT;
        updateRecipe(currentRecipeIndex);
        setChanged(level, getBlockPos(), getBlockState());
    }

    public void setSecondaryExtractMode(boolean secondaryExtractMode) {
        this.secondaryExtractMode = secondaryExtractMode;
        setChanged(level, getBlockPos(), getBlockState());
    }

    public void setAllowOutputOverflow(boolean allowOutputOverflow) {
        this.allowOutputOverflow = allowOutputOverflow;
        setChanged(level, getBlockPos(), getBlockState());
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