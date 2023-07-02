package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.AutoCrafterMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.LimitingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.*;
import java.util.stream.IntStream;

public class AutoCrafterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate {
    public static final long CAPACITY = 2048;
    public static final long MAX_RECEIVE = 256;
    public final static long ENERGY_CONSUMPTION_PER_ITEM = 2;

    final CachedSidedInventoryStorage<AutoCrafterBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final SimpleEnergyStorage internalEnergyStorage;

    private final SimpleInventory patternSlots = new SimpleInventory(3 * 3) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    private final SimpleInventory patternResultSlots = new SimpleInventory(1);
    private final InventoryChangedListener updatePatternListener = container -> updateRecipe();
    private boolean hasRecipeLoaded = false;
    private Identifier recipeIdForSetRecipe;
    private CraftingRecipe craftingRecipe;
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

    protected final PropertyDelegate data;
    private int progress;
    private int maxProgress = 100;
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;
    private boolean ignoreNBT;
    private boolean secondaryExtractMode;

    public AutoCrafterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.AUTO_CRAFTER_ENTITY, blockPos, blockState);

        patternSlots.addListener(updatePatternListener);

        internalInventory = new SimpleInventory(18) {
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
        inventory = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 18).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i >= 3, i -> secondaryExtractMode?!isInput(internalInventory.getStack(i)):
                isOutputOrCraftingRemainderOfInput(internalInventory.getStack(i)));
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);

        internalEnergyStorage = new SimpleEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(amount);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.broadcastServerPacket(world.getServer(), ModMessages.ENERGY_SYNC_ID, buffer);
                }
            }
        };
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> AutoCrafterBlockEntity.this.progress;
                    case 1 -> AutoCrafterBlockEntity.this.maxProgress;
                    case 2, 3, 4, 5 -> ByteUtils.get2Bytes(AutoCrafterBlockEntity.this.internalEnergyStorage.amount, index - 2);
                    case 6, 7, 8, 9 -> ByteUtils.get2Bytes(AutoCrafterBlockEntity.this.internalEnergyStorage.capacity, index - 6);
                    case 10, 11, 12, 13 -> ByteUtils.get2Bytes(AutoCrafterBlockEntity.this.energyConsumptionLeft, index - 10);
                    case 14 -> hasEnoughEnergy?1:0;
                    case 15 -> ignoreNBT?1:0;
                    case 16 -> secondaryExtractMode?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> AutoCrafterBlockEntity.this.progress = value;
                    case 1 -> AutoCrafterBlockEntity.this.maxProgress = value;
                    case 2, 3, 4, 5 -> AutoCrafterBlockEntity.this.internalEnergyStorage.amount = ByteUtils.with2Bytes(
                            AutoCrafterBlockEntity.this.internalEnergyStorage.amount, (short)value, index - 2);
                    case 6, 7, 8, 9, 10, 11, 12, 13, 14 -> {}
                    case 15 -> AutoCrafterBlockEntity.this.ignoreNBT = value != 0;
                    case 16 -> AutoCrafterBlockEntity.this.secondaryExtractMode = value != 0;
                }
            }

            @Override
            public int size() {
                return 17;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.auto_crafter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new AutoCrafterMenu(id, this, inventory, internalInventory, patternSlots, patternResultSlots, data);
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(internalInventory);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.stacks));
        nbt.put("pattern", savePatternContainer());
        nbt.putLong("energy", internalEnergyStorage.amount);

        if(craftingRecipe != null)
            nbt.put("recipe.id", NbtString.of(craftingRecipe.getId().toString()));

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));

        nbt.putBoolean("ignore_nbt", ignoreNBT);
        nbt.putBoolean("secondary_extract_mode", secondaryExtractMode);

        super.writeNbt(nbt);
    }

    private NbtElement savePatternContainer() {
        NbtList nbtTagList = new NbtList();
        for(int i = 0;i < patternSlots.size();i++)  {
            if(!patternSlots.getStack(i).isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                patternSlots.getStack(i).writeNbt(itemTag);
                nbtTagList.add(itemTag);
            }
        }

        return nbtTagList;
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
        loadPatternContainer(nbt.get("pattern"));
        internalEnergyStorage.amount = nbt.getLong("energy");

        if(nbt.contains("recipe.id")) {
            NbtElement tag = nbt.get("recipe.id");

            if(!(tag instanceof NbtString stringTag))
                throw new IllegalArgumentException("Tag must be of type StringTag!");

            recipeIdForSetRecipe = Identifier.tryParse(stringTag.asString());
        }

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");

        ignoreNBT = nbt.getBoolean("ignore_nbt");
        secondaryExtractMode = nbt.getBoolean("secondary_extract_mode");
    }

    private void loadPatternContainer(NbtElement tag) {
        if(!(tag instanceof NbtList))
            throw new IllegalArgumentException("Tag must be of type ListTag!");

        patternSlots.removeListener(updatePatternListener);
        NbtList tagList = (NbtList)tag;
        for(int i = 0;i < tagList.size();i++) {
            NbtCompound itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if(slot >= 0 && slot < patternSlots.size()) {
                patternSlots.setStack(slot, ItemStack.fromNbt(itemTags));
            }
        }
        patternSlots.addListener(updatePatternListener);
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.stacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AutoCrafterBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.hasRecipeLoaded) {
            blockEntity.updateRecipe();

            if(blockEntity.craftingRecipe == null)
                blockEntity.resetProgress();
        }

        int itemCount = 0;
        for(int i = 0;i < blockEntity.patternSlots.size();i++)
            if(!blockEntity.patternSlots.getStack(i).isEmpty())
                itemCount++;

        //Ignore empty recipes
        if(itemCount == 0)
            return;

        if(blockEntity.craftingRecipe != null && (blockEntity.progress > 0 || (blockEntity.canInsertIntoOutputSlot() && blockEntity.canExtractItemsFromInput()))) {
            if(!blockEntity.canInsertIntoOutputSlot() || !blockEntity.canExtractItemsFromInput())
                return;

            long energyConsumptionPerTick = itemCount * ENERGY_CONSUMPTION_PER_ITEM;

            if(blockEntity.progress == 0) {
                if(!blockEntity.canExtractItemsFromInput())
                    return;

                blockEntity.energyConsumptionLeft = energyConsumptionPerTick * blockEntity.maxProgress;
            }

            if(energyConsumptionPerTick <= blockEntity.internalEnergyStorage.amount) {
                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.internalEnergyStorage.extract(energyConsumptionPerTick, transaction);
                    transaction.commit();
                }

                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

                blockEntity.progress++;
                markDirty(level, blockPos, state);

                if(blockEntity.progress >= blockEntity.maxProgress) {
                    SimpleInventory patternSlotsForRecipe = blockEntity.ignoreNBT?
                            blockEntity.replaceCraftingPatternWithCurrentNBTItems(blockEntity.patternSlots):blockEntity.patternSlots;
                    CraftingInventory copyOfPatternSlots = new CraftingInventory(blockEntity.dummyContainerMenu, 3, 3);
                    for(int i = 0;i < patternSlotsForRecipe.size();i++)
                        copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));

                    blockEntity.extractItems();
                    blockEntity.craftItem(copyOfPatternSlots);
                }
            }else {
                blockEntity.hasEnoughEnergy = false;
            }
        }else {
            blockEntity.resetProgress();
            markDirty(level, blockPos, state);
        }
    }

    private void resetProgress() {
        progress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = true;
    }

    public void resetProgressAndMarkAsChanged() {
        resetProgress();
        markDirty(world, getPos(), getCachedState());
    }

    public void setRecipeIdForSetRecipe(Identifier recipeIdForSetRecipe) {
        this.recipeIdForSetRecipe = recipeIdForSetRecipe;

        updateRecipe();
    }

    private void updateRecipe() {
        if(world == null)
            return;

        CraftingRecipe oldRecipe = null;
        ItemStack oldResult = null;
        if(hasRecipeLoaded && craftingRecipe != null && oldCopyOfRecipe != null) {
            oldRecipe = craftingRecipe;

            oldResult = craftingRecipe instanceof SpecialCraftingRecipe?craftingRecipe.craft(oldCopyOfRecipe, world.getRegistryManager()):
                    craftingRecipe.getOutput(world.getRegistryManager());
        }

        hasRecipeLoaded = true;

        SimpleInventory patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        CraftingInventory copyOfPatternSlots = new CraftingInventory(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));

        Optional<Pair<Identifier, CraftingRecipe>> recipe = world.getRecipeManager().
                getFirstMatch(RecipeType.CRAFTING, copyOfPatternSlots, world, recipeIdForSetRecipe);
        if(recipe.isPresent()) {
            craftingRecipe = recipe.get().getSecond();

            //Recipe with saved recipe id does not exist or pattern items are not compatible with recipe
            if(recipeIdForSetRecipe != null && !craftingRecipe.getId().equals(recipeIdForSetRecipe))
                resetProgress();

            ItemStack resultItemStack = craftingRecipe instanceof SpecialCraftingRecipe?craftingRecipe.craft(copyOfPatternSlots, world.getRegistryManager()):
                    craftingRecipe.getOutput(world.getRegistryManager());

            patternResultSlots.setStack(0, resultItemStack);

            if(oldRecipe != null && oldResult != null && oldCopyOfRecipe != null && (craftingRecipe != oldRecipe || !ItemStack.areEqual(resultItemStack, oldResult)))
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
            for(int i = 0;i < internalInventory.size();i++) {
                ItemStack testItemStack = internalInventory.getStack(i);
                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.canCombine(itemStack, testItemStack)) {
                    ItemStack ret = internalInventory.removeStack(i, itemStack.getCount());
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

        ItemStack resultItemStack = craftingRecipe instanceof SpecialCraftingRecipe?craftingRecipe.craft(copyOfPatternSlots, world.getRegistryManager()):
                craftingRecipe.getOutput(world.getRegistryManager());

        outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe.getRemainder(copyOfPatternSlots))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacksInsert = ItemStackUtils.combineItemStacks(outputItemStacks);

        List<Integer> emptyIndices = new ArrayList<>(18);
        outer:
        for(ItemStack itemStack:itemStacksInsert) {
            for(int i = 0;i < internalInventory.size();i++) {
                ItemStack testItemStack = internalInventory.getStack(i);
                if(emptyIndices.contains(i))
                    continue;

                if(testItemStack.isEmpty()) {
                    emptyIndices.add(i);

                    continue;
                }

                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.canCombine(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxCount() - testItemStack.getCount());
                    if(amount > 0) {
                        internalInventory.setStack(i, internalInventory.getStack(i).copyWithCount(testItemStack.getCount() + amount));

                        itemStack.setCount(itemStack.getCount() - amount);

                        if(itemStack.isEmpty())
                            continue outer;
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                continue; //Should not happen

            internalInventory.setStack(emptyIndices.remove(0), itemStack);
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

            for(int j = 0;j < internalInventory.size();j++) {
                if(checkedIndices.contains(j))
                    continue;

                ItemStack testItemStack = internalInventory.getStack(j);
                if(testItemStack.isEmpty()) {
                    checkedIndices.add(j);

                    continue;
                }

                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.canCombine(itemStack, testItemStack)) {
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

    private boolean canInsertIntoOutputSlot() {
        if(craftingRecipe == null)
            return false;

        SimpleInventory patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        CraftingInventory copyOfPatternSlots = new CraftingInventory(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.size();i++)
            copyOfPatternSlots.setStack(i, patternSlotsForRecipe.getStack(i));


        List<ItemStack> outputItemStacks = new ArrayList<>(10);
        ItemStack resultItemStack = craftingRecipe instanceof SpecialCraftingRecipe?craftingRecipe.craft(copyOfPatternSlots, world.getRegistryManager()):
                craftingRecipe.getOutput(world.getRegistryManager());

        if(!resultItemStack.isEmpty())
            outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe.getRemainder(copyOfPatternSlots))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacks = ItemStackUtils.combineItemStacks(outputItemStacks);

        List<Integer> checkedIndices = new ArrayList<>(18);
        List<Integer> emptyIndices = new ArrayList<>(18);
        outer:
        for(int i = itemStacks.size() - 1;i >= 0;i--) {
            ItemStack itemStack = itemStacks.get(i);
            for(int j = 0;j < internalInventory.size();j++) {
                if(checkedIndices.contains(j) || emptyIndices.contains(j))
                    continue;

                ItemStack testItemStack = internalInventory.getStack(j);
                if(testItemStack.isEmpty()) {
                    emptyIndices.add(j);

                    continue;
                }

                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.canCombine(itemStack, testItemStack)) {
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

        ItemStack resultItemStack = craftingRecipe instanceof SpecialCraftingRecipe?craftingRecipe.craft(copyOfPatternSlots, world.getRegistryManager()):
                craftingRecipe.getOutput(world.getRegistryManager());

        if(ItemStack.areItemsEqual(itemStack, resultItemStack) && ItemStack.canCombine(itemStack, resultItemStack))
            return true;

        for(ItemStack remainingItem:craftingRecipe.getRemainder(copyOfPatternSlots))
            if(ItemStack.areItemsEqual(itemStack, remainingItem) && ItemStack.canCombine(itemStack, remainingItem))
                return true;

        return false;
    }

    private boolean isInput(ItemStack itemStack) {
        if(craftingRecipe == null)
            return false;

        for(int i = 0;i < patternSlots.size();i++)
            if(ignoreNBT?ItemStack.areItemsEqual(itemStack, patternSlots.getStack(i)):
                    (ItemStack.areItemsEqual(itemStack, patternSlots.getStack(i)) &&
                            ItemStack.canCombine(itemStack, patternSlots.getStack(i))))
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

            for(int j = 0;j < internalInventory.size();j++) {
                ItemStack testItemStack = internalInventory.getStack(j).copy();
                int usedCount = usedItemCounts.getOrDefault(j, 0);
                testItemStack.setCount(testItemStack.getCount() - usedCount);
                if(testItemStack.getCount() <= 0)
                    continue;

                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.canCombine(itemStack, testItemStack)) {
                    usedItemCounts.put(j, usedCount + 1);
                    continue outer;
                }
            }

            //Same item with same tag was not found -> check for same item without same tag and change if found
            for(int j = 0;j < internalInventory.size();j++) {
                ItemStack testItemStack = internalInventory.getStack(j).copy();
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


    public void setIgnoreNBT(boolean ignoreNBT) {
        this.ignoreNBT = ignoreNBT;
        updateRecipe();
        markDirty(world, getPos(), getCachedState());
    }

    public void setSecondaryExtractMode(boolean secondaryExtractMode) {
        this.secondaryExtractMode = secondaryExtractMode;
        markDirty(world, getPos(), getCachedState());
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        //Does nothing (capacity is final)
    }

    public long getCapacity() {
        return internalEnergyStorage.capacity;
    }
}