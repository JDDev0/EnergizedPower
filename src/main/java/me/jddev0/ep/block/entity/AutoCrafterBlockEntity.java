package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.screen.AutoCrafterMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AutoCrafterBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate {
    private final ReceiveOnlyEnergyStorage energyStorage;

    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();
    private final ItemStackHandler itemHandler = new ItemStackHandler(18) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if(slot < 0 || slot >= 18)
                return super.isItemValid(slot, stack);

            //Slot 0, 1, and 2 are for output items only
            return slot >= 3;
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 3, i -> isOutputOrCraftingRemainderOfInput(itemHandler.getStackInSlot(i))));

    private final SimpleContainer patternSlots = new SimpleContainer(3 * 3) {
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };
    private final SimpleContainer patternResultSlots = new SimpleContainer(1);
    private final ContainerListener updatePatternListener = container -> updateRecipe();
    private boolean hasRecipeLoaded = false;
    private CraftingRecipe craftingRecipe;
    private CraftingContainer oldCopyOfRecipe;
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

    public final static int ENERGY_CONSUMPTION_PER_ITEM = 2;

    protected  final ContainerData data;
    private int progress;
    private int maxProgress = 100;
    private int energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;
    private boolean ignoreNBT;

    public AutoCrafterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.AUTO_CRAFTER_ENTITY.get(), blockPos, blockState);

        patternSlots.addListener(updatePatternListener);

        energyStorage = new ReceiveOnlyEnergyStorage(0, 2048, 256) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
            }
        };
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> AutoCrafterBlockEntity.this.progress;
                    case 1 -> AutoCrafterBlockEntity.this.maxProgress;
                    case 2, 3 -> ByteUtils.get2Bytes(AutoCrafterBlockEntity.this.energyStorage.getEnergy(), index - 2);
                    case 4, 5 -> ByteUtils.get2Bytes(AutoCrafterBlockEntity.this.energyStorage.getCapacity(), index - 4);
                    case 6, 7 -> ByteUtils.get2Bytes(AutoCrafterBlockEntity.this.energyConsumptionLeft, index - 6);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> ignoreNBT?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> AutoCrafterBlockEntity.this.progress = value;
                    case 1 -> AutoCrafterBlockEntity.this.maxProgress = value;
                    case 2, 3 -> AutoCrafterBlockEntity.this.energyStorage.setEnergyWithoutUpdate(ByteUtils.with2Bytes(
                            AutoCrafterBlockEntity.this.energyStorage.getEnergy(), (short)value, index - 2
                    ));
                    case 4, 5 -> AutoCrafterBlockEntity.this.energyStorage.setCapacityWithoutUpdate(ByteUtils.with2Bytes(
                            AutoCrafterBlockEntity.this.energyStorage.getCapacity(), (short)value, index - 4
                    ));
                    case 6, 7, 8 -> {}
                    case 9 -> AutoCrafterBlockEntity.this.ignoreNBT = value != 0;
                }
            }

            @Override
            public int getCount() {
                return 10;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container.energizedpower.auto_crafter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new AutoCrafterMenu(id, inventory, this, patternSlots, patternResultSlots, data);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == CapabilityEnergy.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.put("pattern", savePatternContainer());
        nbt.put("energy", energyStorage.saveNBT());

        nbt.put("recipe.progress", IntTag.valueOf(progress));
        nbt.put("recipe.energy_consumption_left", IntTag.valueOf(energyConsumptionLeft));

        nbt.putBoolean("ignore_nbt", ignoreNBT);

        super.saveAdditional(nbt);
    }

    private Tag savePatternContainer() {
        ListTag nbtTagList = new ListTag();
        for(int i = 0;i < patternSlots.getContainerSize();i++)  {
            if(!patternSlots.getItem(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                patternSlots.getItem(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }

        return nbtTagList;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        loadPatternContainer(nbt.get("pattern"));
        energyStorage.loadNBT(nbt.get("energy"));

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");

        ignoreNBT = nbt.getBoolean("ignore_nbt");
    }

    private void loadPatternContainer(Tag tag) {
        if(!(tag instanceof ListTag))
            throw new IllegalArgumentException("Tag must be of type ListTag!");

        patternSlots.removeListener(updatePatternListener);
        ListTag tagList = (ListTag)tag;
        for(int i = 0;i < tagList.size();i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if(slot >= 0 && slot < patternSlots.getContainerSize()) {
                patternSlots.setItem(slot, ItemStack.of(itemTags));
            }
        }
        patternSlots.addListener(updatePatternListener);
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AutoCrafterBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(!blockEntity.hasRecipeLoaded) {
            blockEntity.updateRecipe();

            if(blockEntity.craftingRecipe == null)
                blockEntity.resetProgress();
        }

        if(blockEntity.craftingRecipe != null && (blockEntity.progress > 0 || (blockEntity.canInsertIntoOutputSlot() && blockEntity.canExtractItemsFromInput()))) {
            if(!blockEntity.canInsertIntoOutputSlot() || !blockEntity.canExtractItemsFromInput())
                return;

            int itemCount = 0;
            for(int i = 0;i < blockEntity.patternSlots.getContainerSize();i++)
                if(!blockEntity.patternSlots.getItem(i).isEmpty())
                    itemCount++;

            int energyConsumptionPerTick = itemCount * ENERGY_CONSUMPTION_PER_ITEM;

            if(blockEntity.progress == 0) {
                if(!blockEntity.canExtractItemsFromInput())
                    return;

                blockEntity.energyConsumptionLeft = energyConsumptionPerTick * blockEntity.maxProgress;
            }

            if(energyConsumptionPerTick <= blockEntity.energyStorage.getEnergy()) {
                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);
                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

                blockEntity.progress++;
                setChanged(level, blockPos, state);

                if(blockEntity.progress >= blockEntity.maxProgress) {
                    SimpleContainer patternSlotsForRecipe = blockEntity.ignoreNBT?
                            blockEntity.replaceCraftingPatternWithCurrentNBTItems(blockEntity.patternSlots):blockEntity.patternSlots;
                    CraftingContainer copyOfPatternSlots = new CraftingContainer(blockEntity.dummyContainerMenu, 3, 3);
                    for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
                        copyOfPatternSlots.setItem(i, patternSlotsForRecipe.getItem(i));

                    blockEntity.extractItems();
                    blockEntity.craftItem(copyOfPatternSlots);
                }
            }else {
                blockEntity.hasEnoughEnergy = false;
            }
        }else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, state);
        }
    }

    private void resetProgress() {
        progress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = true;
    }

    public void resetProgressAndMarkAsChanged() {
        resetProgress();
        setChanged(level, getBlockPos(), getBlockState());
    }

    private void updateRecipe() {
        if(level == null)
            return;

        CraftingRecipe oldRecipe = null;
        ItemStack oldResult = null;
        if(hasRecipeLoaded && craftingRecipe != null && oldCopyOfRecipe != null) {
            oldRecipe = craftingRecipe;

            oldResult = craftingRecipe instanceof CustomRecipe?craftingRecipe.assemble(oldCopyOfRecipe):craftingRecipe.getResultItem();
        }

        hasRecipeLoaded = true;

        SimpleContainer patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        CraftingContainer copyOfPatternSlots = new CraftingContainer(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            copyOfPatternSlots.setItem(i, patternSlotsForRecipe.getItem(i));

        Optional<CraftingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, copyOfPatternSlots, level);
        if(recipe.isPresent()) {
            craftingRecipe = recipe.get();

            ItemStack resultItemStack = craftingRecipe instanceof CustomRecipe?craftingRecipe.assemble(copyOfPatternSlots):craftingRecipe.getResultItem();

            patternResultSlots.setItem(0, resultItemStack);

            if(oldRecipe != null && oldResult != null && oldCopyOfRecipe != null && (craftingRecipe != oldRecipe || !ItemStack.isSameItemSameTags(resultItemStack, oldResult)))
                resetProgress();

            oldCopyOfRecipe = new CraftingContainer(dummyContainerMenu, 3, 3);
            for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
                oldCopyOfRecipe.setItem(i, copyOfPatternSlots.getItem(i).copy());
        }else {
            craftingRecipe = null;

            patternResultSlots.setItem(0, ItemStack.EMPTY);

            oldCopyOfRecipe = null;
        }
    }

    private void extractItems() {
        SimpleContainer patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
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

    private void craftItem(CraftingContainer copyOfPatternSlots) {
        if(craftingRecipe == null) {
            resetProgress();

            return;
        }

        List<ItemStack> outputItemStacks = new ArrayList<>(10);

        ItemStack resultItemStack = craftingRecipe instanceof CustomRecipe?craftingRecipe.assemble(copyOfPatternSlots):craftingRecipe.getResultItem();

        outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe.getRemainingItems(copyOfPatternSlots))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacksInsert = ItemStackUtils.combineItemStacks(outputItemStacks);

        List<Integer> emptyIndices = new ArrayList<>(18);
        for(ItemStack itemStack:itemStacksInsert) {
            for(int i = 0;i < itemHandler.getSlots();i++) {
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
                        ItemStack itemStackCopy = itemHandler.getStackInSlot(i).copy();
                        itemStackCopy.setCount(testItemStack.getCount() + amount);
                        itemHandler.setStackInSlot(i, itemStackCopy);

                        itemStack.setCount(itemStack.getCount() - amount);
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                continue; //Should not happen

            itemHandler.setStackInSlot(emptyIndices.remove(0), itemStack);
        }

        if(ignoreNBT)
            updateRecipe();

        resetProgress();
    }

    private boolean canExtractItemsFromInput() {
        if(craftingRecipe == null)
            return false;

        SimpleContainer patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        List<ItemStack> patternItemStacks = new ArrayList<>(9);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            if(!patternSlotsForRecipe.getItem(i).isEmpty())
                patternItemStacks.add(patternSlotsForRecipe.getItem(i));

        List<ItemStack> itemStacks = ItemStackUtils.combineItemStacks(patternItemStacks);

        List<Integer> checkedIndices = new ArrayList<>(18);
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

    private boolean canInsertIntoOutputSlot() {
        if(craftingRecipe == null)
            return false;

        SimpleContainer patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        CraftingContainer copyOfPatternSlots = new CraftingContainer(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            copyOfPatternSlots.setItem(i, patternSlotsForRecipe.getItem(i));

        List<ItemStack> outputItemStacks = new ArrayList<>(10);
        ItemStack resultItemStack = craftingRecipe instanceof CustomRecipe?craftingRecipe.assemble(copyOfPatternSlots):craftingRecipe.getResultItem();

        if(!resultItemStack.isEmpty())
            outputItemStacks.add(resultItemStack);

        for(ItemStack remainingItem:craftingRecipe.getRemainingItems(copyOfPatternSlots))
            if(!remainingItem.isEmpty())
                outputItemStacks.add(remainingItem);

        List<ItemStack> itemStacks = ItemStackUtils.combineItemStacks(outputItemStacks);

        List<Integer> checkedIndices = new ArrayList<>(18);
        List<Integer> emptyIndices = new ArrayList<>(18);
        outer:
        for(int i = itemStacks.size() - 1;i >= 0;i--) {
            ItemStack itemStack = itemStacks.get(i);
            for(int j = 0;j < itemHandler.getSlots();j++) {
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

            int index = emptyIndices.remove(0);
            if(itemStack.getCount() == itemStack.getMaxStackSize())
                checkedIndices.add(index);

            itemStacks.remove(i);
        }

        return itemStacks.isEmpty();
    }

    private boolean isOutputOrCraftingRemainderOfInput(ItemStack itemStack) {
        if(craftingRecipe == null)
            return false;

        SimpleContainer patternSlotsForRecipe = ignoreNBT?replaceCraftingPatternWithCurrentNBTItems(patternSlots):patternSlots;
        CraftingContainer copyOfPatternSlots = new CraftingContainer(dummyContainerMenu, 3, 3);
        for(int i = 0;i < patternSlotsForRecipe.getContainerSize();i++)
            copyOfPatternSlots.setItem(i, patternSlotsForRecipe.getItem(i));

        ItemStack resultItemStack = craftingRecipe instanceof CustomRecipe?craftingRecipe.assemble(copyOfPatternSlots):craftingRecipe.getResultItem();

        if(ItemStack.isSameItemSameTags(itemStack, resultItemStack))
            return true;

        for(ItemStack remainingItem:craftingRecipe.getRemainingItems(copyOfPatternSlots))
            if(ItemStack.isSameItemSameTags(itemStack, remainingItem))
                return true;

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

                if(ItemStack.isSame(itemStack, testItemStack)) {
                    usedItemCounts.put(j, usedCount + 1);

                    ItemStack newItemStack = testItemStack.copy();
                    newItemStack.setCount(1);
                    copyOfContainer.setItem(i, newItemStack);

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
        setChanged(level, getBlockPos(), getBlockState());
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }
}