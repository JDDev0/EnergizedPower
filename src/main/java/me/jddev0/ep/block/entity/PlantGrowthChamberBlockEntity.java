package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.PlantGrowthChamberBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.PlantGrowthChamberFertilizerRecipe;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import me.jddev0.ep.screen.PlantGrowthChamberMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class PlantGrowthChamberBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate, RedstoneModeUpdate,
        ComparatorModeUpdate {
    private static final long CAPACITY = ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_CAPACITY.getValue();

    private static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER.getValue();

    final CachedSidedInventoryStorage<PlantGrowthChamberBlockEntity> cachedSidedInventoryStorageSides;
    final InputOutputItemHandler sidedInventorySides;

    final CachedSidedInventoryStorage<PlantGrowthChamberBlockEntity> cachedSidedInventoryStorageTopBottom;
    final InputOutputItemHandler sidedInventoryTopBottom;
    private final SimpleInventory internalInventory;

    private final UpgradeModuleInventory upgradeModuleInventory = new UpgradeModuleInventory(
            UpgradeModuleModifier.SPEED,
            UpgradeModuleModifier.ENERGY_CONSUMPTION,
            UpgradeModuleModifier.ENERGY_CAPACITY
    );
    private final InventoryChangedListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    final EnergizedPowerLimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    protected final PropertyDelegate data;
    private int progress;
    private int maxProgress;
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;
    private double speedMultiplier = 1;
    private double energyConsumptionMultiplier = 1;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public PlantGrowthChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.PLANT_GROWTH_CHAMBER_ENTITY, blockPos, blockState);

        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

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
                            !ItemStack.canCombine(stack, itemStack)))
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

        internalEnergyStorage = new EnergizedPowerEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(getAmount());
                    buffer.writeLong(getCapacity());
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.ENERGY_SYNC_ID, buffer
                    );
                }
            }
        };
        energyStorage = new EnergizedPowerLimitingEnergyStorage(internalEnergyStorage, ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_TRANSFER_RATE.getValue(), 0) {
            @Override
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(PlantGrowthChamberBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(PlantGrowthChamberBlockEntity.this.maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(PlantGrowthChamberBlockEntity.this.energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> redstoneMode.ordinal();
                    case 10 -> comparatorMode.ordinal();
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
                    case 4, 5, 6, 7, 8 -> {}
                    case 9 -> PlantGrowthChamberBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 10 -> PlantGrowthChamberBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 11;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.plant_growth_chamber");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.getAmount());
        buffer.writeLong(internalEnergyStorage.getCapacity());
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.ENERGY_SYNC_ID, buffer);
        
        return new PlantGrowthChamberMenu(id, this, inventory, internalInventory, upgradeModuleInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> ScreenHandler.calculateComparatorOutput(internalInventory);
            case FLUID -> 0;
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        //Save Upgrade Module Inventory first
        nbt.put("upgrade_module_inventory", upgradeModuleInventory.saveToNBT());

        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.stacks));
        nbt.putLong("energy", internalEnergyStorage.getAmount());

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));
        nbt.put("recipe.speed_multiplier", NbtDouble.of(speedMultiplier));
        nbt.put("recipe.energy_consumption_multiplier", NbtDouble.of(energyConsumptionMultiplier));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.loadFromNBT(nbt.getCompound("upgrade_module_inventory"));
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
        internalEnergyStorage.setAmountWithoutUpdate(nbt.getLong("energy"));

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");
        speedMultiplier = nbt.getDouble("recipe.speed_multiplier");
        energyConsumptionMultiplier = nbt.getDouble("recipe.energy_consumption_multiplier");

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory);
        ItemScatterer.spawn(level, worldPosition, upgradeModuleInventory);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, PlantGrowthChamberBlockEntity blockEntity) {
        if(level.isClient())
            return;
        
        
        if(!blockEntity.redstoneMode.isActive(state.get(PlantGrowthChamberBlock.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            Optional<RecipeEntry<PlantGrowthChamberRecipe>> recipe = level.getRecipeManager().getFirstMatch(PlantGrowthChamberRecipe.Type.INSTANCE, blockEntity.internalInventory, level);
            if(recipe.isEmpty())
                return;

            Optional<RecipeEntry<PlantGrowthChamberFertilizerRecipe>> fertilizerRecipe = level.getRecipeManager().getFirstMatch(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE,
                    blockEntity.internalInventory, level);

            if(blockEntity.maxProgress == 0) {
                if(fertilizerRecipe.isPresent()) {
                    blockEntity.speedMultiplier = fertilizerRecipe.get().value().getSpeedMultiplier();
                    blockEntity.energyConsumptionMultiplier = fertilizerRecipe.get().value().getEnergyConsumptionMultiplier();

                    blockEntity.internalInventory.removeStack(1, 1);
                }

                blockEntity.maxProgress = Math.max(1, (int)(recipe.get().value().getTicks() * RECIPE_DURATION_MULTIPLIER / blockEntity.speedMultiplier /
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));
            }

            final long fertilizedEnergyUsagePerTick = Math.max(1, (long)Math.ceil(ENERGY_USAGE_PER_TICK * blockEntity.energyConsumptionMultiplier *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = fertilizedEnergyUsagePerTick * blockEntity.maxProgress;

            if(fertilizedEnergyUsagePerTick <= blockEntity.internalEnergyStorage.getAmount()) {
                blockEntity.hasEnoughEnergy = true;

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    markDirty(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.internalEnergyStorage.extract(fertilizedEnergyUsagePerTick, transaction);
                    transaction.commit();
                }
                blockEntity.energyConsumptionLeft -= fertilizedEnergyUsagePerTick;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    craftItem(blockPos, state, blockEntity);

                markDirty(level, blockPos, state);
            }else {
                blockEntity.hasEnoughEnergy = false;
                markDirty(level, blockPos, state);
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

        Optional<RecipeEntry<PlantGrowthChamberRecipe>> recipe = level.getRecipeManager().getFirstMatch(PlantGrowthChamberRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.internalInventory.removeStack(0, 1);

        List<ItemStack> itemStacksInsert = new ArrayList<>(Arrays.asList(recipe.get().value().generateOutputs(level.random)));

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

                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.canCombine(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxCount() - testItemStack.getCount());
                    if(amount > 0) {
                        blockEntity.internalInventory.setStack(i, blockEntity.internalInventory.getStack(i).copyWithCount(testItemStack.getCount() + amount));

                        itemStack.setCount(itemStack.getCount() - amount);

                        if(itemStack.isEmpty())
                            continue outer;
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                continue; //Excess items will be vanished

            blockEntity.internalInventory.setStack(emptyIndices.remove(0), itemStack);
        }

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(PlantGrowthChamberBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<RecipeEntry<PlantGrowthChamberRecipe>> recipe = level.getRecipeManager().getFirstMatch(PlantGrowthChamberRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        return recipe.isPresent() && canInsertItemsIntoOutputSlots(blockEntity.internalInventory, new ArrayList<>(Arrays.asList(recipe.get().value().getMaxOutputCounts())));
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

                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.canCombine(itemStack, testItemStack)) {
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

    private void updateUpgradeModules() {
        resetProgress(getPos(), getCachedState());
        markDirty();
        if(world != null && !world.isClient()) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeLong(internalEnergyStorage.getAmount());
            buffer.writeLong(internalEnergyStorage.getCapacity());
            buffer.writeBlockPos(getPos());

            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getPos(), (ServerWorld)world, 32,
                    ModMessages.ENERGY_SYNC_ID, buffer
            );
        }
    }

    public long getEnergy() {
        return internalEnergyStorage.getAmount();
    }

    public long getCapacity() {
        return internalEnergyStorage.getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.setAmountWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(long capacity) {
        internalEnergyStorage.setCapacityWithoutUpdate(capacity);
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        markDirty();
    }

    @Override
    public void setNextComparatorMode() {
        do {
            comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        }while(comparatorMode == ComparatorMode.FLUID); //Prevent the FLUID comparator mode from being selected
        markDirty();
    }
}