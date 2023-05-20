package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.PlantGrowthChamberBlock;
import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.mixin.inventory.SimpleInventoryStacksGetterSetter;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.PlantGrowthChamberFertilizerRecipe;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import me.jddev0.ep.screen.PlantGrowthChamberMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.LimitingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class PlantGrowthChamberBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate {
    private static final int CAPACITY = 4096;
    private static final int ENERGY_USAGE_PER_TICK = 32;

    final CachedSidedInventoryStorage<PlantGrowthChamberBlockEntity> cachedSidedInventoryStorageSides;
    final InputOutputItemHandler sidedInventorySides;

    final CachedSidedInventoryStorage<PlantGrowthChamberBlockEntity> cachedSidedInventoryStorageTopBottom;
    final InputOutputItemHandler sidedInventoryTopBottom;
    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final SimpleEnergyStorage internalEnergyStorage;

    protected final PropertyDelegate data;
    private int progress;
    private int maxProgress;
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;
    private double speedMultiplier = 1;
    private double energyConsumptionMultiplier = 1;

    public PlantGrowthChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.PLANT_GROWTH_CHAMBER_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(6) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> world == null || RecipeUtils.isIngredientOfAny(world, PlantGrowthChamberRecipe.Type.INSTANCE, stack);
                    case 1 -> world == null || RecipeUtils.isIngredientOfAny(world, PlantGrowthChamberFertilizerRecipe.Type.INSTANCE, stack);
                    case 2, 3, 4, 5 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.areItemsEqual(stack, itemStack) ||
                            !ItemStack.areNbtEqual(stack, itemStack)))
                        resetProgress(pos, world.getBlockState(pos));
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                PlantGrowthChamberBlockEntity.this.markDirty();
            }
        };
        sidedInventorySides = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 6).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 0, i -> i > 1 && i < 6);
        cachedSidedInventoryStorageSides = new CachedSidedInventoryStorage<>(sidedInventorySides);
        sidedInventoryTopBottom = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 6).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 1, i -> i > 1 && i < 6);
        cachedSidedInventoryStorageTopBottom = new CachedSidedInventoryStorage<>(sidedInventoryTopBottom);

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
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, 256, 0);

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(PlantGrowthChamberBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(PlantGrowthChamberBlockEntity.this.maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(PlantGrowthChamberBlockEntity.this.internalEnergyStorage.amount, index - 4);
                    case 8, 9, 10, 11 -> ByteUtils.get2Bytes(PlantGrowthChamberBlockEntity.this.internalEnergyStorage.capacity, index - 8);
                    case 12, 13, 14, 15 -> ByteUtils.get2Bytes(PlantGrowthChamberBlockEntity.this.energyConsumptionLeft, index - 12);
                    case 16 -> hasEnoughEnergy?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> PlantGrowthChamberBlockEntity.this.progress = ByteUtils.with2Bytes(
                            PlantGrowthChamberBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> PlantGrowthChamberBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            PlantGrowthChamberBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6, 7 -> PlantGrowthChamberBlockEntity.this.internalEnergyStorage.amount = ByteUtils.with2Bytes(
                            PlantGrowthChamberBlockEntity.this.internalEnergyStorage.amount, (short)value, index - 4);
                    case 8, 9, 10, 11, 12, 13, 14, 15, 16 -> {}
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
        return new TranslatableText("container.energizedpower.plant_growth_chamber");
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(internalInventory);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new PlantGrowthChamberMenu(id, this, inventory, internalInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), ((SimpleInventoryStacksGetterSetter)internalInventory).getStacks()));
        nbt.putLong("energy", internalEnergyStorage.amount);

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));
        nbt.put("recipe.speed_multiplier", NbtDouble.of(speedMultiplier));
        nbt.put("recipe.energy_consumption_multiplier", NbtDouble.of(energyConsumptionMultiplier));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), ((SimpleInventoryStacksGetterSetter)internalInventory).getStacks());
        internalEnergyStorage.amount = nbt.getLong("energy");

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");
        speedMultiplier = nbt.getDouble("recipe.speed_multiplier");
        energyConsumptionMultiplier = nbt.getDouble("recipe.energy_consumption_multiplier");
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, ((SimpleInventoryStacksGetterSetter)internalInventory).getStacks());
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, PlantGrowthChamberBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(hasRecipe(blockEntity)) {
            Optional<PlantGrowthChamberRecipe> recipe = level.getRecipeManager().getFirstMatch(PlantGrowthChamberRecipe.Type.INSTANCE, blockEntity.internalInventory, level);
            if(recipe.isEmpty())
                return;

            Optional<PlantGrowthChamberFertilizerRecipe> fertilizerRecipe = level.getRecipeManager().getFirstMatch(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE,
                    blockEntity.internalInventory, level);

            if(blockEntity.maxProgress == 0) {
                if(fertilizerRecipe.isPresent()) {
                    blockEntity.speedMultiplier = fertilizerRecipe.get().getSpeedMultiplier();
                    blockEntity.energyConsumptionMultiplier = fertilizerRecipe.get().getEnergyConsumptionMultiplier();

                    blockEntity.internalInventory.removeStack(1, 1);
                }

                blockEntity.maxProgress = (int)(recipe.get().getTicks() / blockEntity.speedMultiplier);
            }

            final long fertilizedEnergyUsagePerTick = (long)(ENERGY_USAGE_PER_TICK * blockEntity.energyConsumptionMultiplier);
            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = fertilizedEnergyUsagePerTick * blockEntity.maxProgress;

            if(fertilizedEnergyUsagePerTick <= blockEntity.internalEnergyStorage.amount) {
                blockEntity.hasEnoughEnergy = true;

                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.internalEnergyStorage.extract(fertilizedEnergyUsagePerTick, transaction);
                    transaction.commit();
                }
                blockEntity.energyConsumptionLeft -= fertilizedEnergyUsagePerTick;

                blockEntity.progress++;
                markDirty(level, blockPos, state);

                if(blockEntity.progress >= blockEntity.maxProgress) {
                    craftItem(blockPos, state, blockEntity);
                }
            }else {
                blockEntity.hasEnoughEnergy = false;
            }
        }else {
            blockEntity.resetProgress(blockPos, state);
            markDirty(level, blockPos, state);
        }
    }

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
        maxProgress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = true;
        speedMultiplier = 1;
        energyConsumptionMultiplier = 1;
    }

    private static void craftItem(BlockPos blockPos, BlockState state, PlantGrowthChamberBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<PlantGrowthChamberRecipe> recipe = level.getRecipeManager().getFirstMatch(PlantGrowthChamberRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.internalInventory.removeStack(0, 1);
        
        List<ItemStack> itemStacksInsert = new ArrayList<>(Arrays.asList(recipe.get().generateOutputs(level.random)));

        List<Integer> emptyIndices = new ArrayList<>(4);
        outer:
        for(ItemStack itemStack:itemStacksInsert) {
            if(itemStack.isEmpty())
                continue;

            for(int i = 2;i < blockEntity.internalInventory.size();i++) {
                ItemStack testItemStack = blockEntity.internalInventory.getStack(i);
                if(emptyIndices.contains(i))
                    continue;

                if(testItemStack.isEmpty()) {
                    emptyIndices.add(i);

                    continue;
                }

                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.areNbtEqual(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxCount() - testItemStack.getCount());
                    if(amount > 0) {
                        ItemStack newItemStack = blockEntity.internalInventory.getStack(i).copy();
                        newItemStack.setCount(testItemStack.getCount() + amount);
                        blockEntity.internalInventory.setStack(i, newItemStack);

                        itemStack.setCount(itemStack.getCount() - amount);

                        if(itemStack.isEmpty())
                            continue outer;
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                continue; //Should not happen

            blockEntity.internalInventory.setStack(emptyIndices.remove(0), itemStack);
        }

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(PlantGrowthChamberBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<PlantGrowthChamberRecipe> recipe = level.getRecipeManager().getFirstMatch(PlantGrowthChamberRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        return recipe.isPresent() && canInsertItemsIntoOutputSlots(blockEntity.internalInventory, new ArrayList<>(Arrays.asList(recipe.get().getMaxOutputCounts())));
    }

    private static boolean canInsertItemsIntoOutputSlots(SimpleInventory inventory, List<ItemStack> itemsStacks) {
        List<Integer> checkedIndices = new ArrayList<>(4);
        List<Integer> emptyIndices = new ArrayList<>(4);
        outer:
        for(int i = Math.min(4, itemsStacks.size()) - 1;i >= 0;i--) {
            ItemStack itemStack = itemsStacks.get(i);
            for(int j = 2;j < inventory.size();j++) {
                if(checkedIndices.contains(j) || emptyIndices.contains(j))
                    continue;

                ItemStack testItemStack = inventory.getStack(j);
                if(testItemStack.isEmpty()) {
                    emptyIndices.add(j);

                    continue;
                }

                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.areNbtEqual(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxCount() - testItemStack.getCount());

                    if(amount + testItemStack.getCount() == testItemStack.getMaxCount())
                        checkedIndices.add(j);

                    if(amount == itemStack.getCount()) {
                        itemsStacks.remove(i);

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

            itemsStacks.remove(i);
        }

        return itemsStacks.isEmpty();
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