package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.PulverizerBlock;
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
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.recipe.PulverizerRecipe;
import me.jddev0.ep.screen.PulverizerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.RecipeUtils;
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
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
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

import java.util.Optional;
import java.util.stream.IntStream;

public class PulverizerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, EnergyStoragePacketUpdate, RedstoneModeUpdate,
        ComparatorModeUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_PULVERIZER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_PULVERIZER_TRANSFER_RATE.getValue();
    private static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_PULVERIZER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    private static final int RECIPE_DURATION = ModConfigs.COMMON_PULVERIZER_RECIPE_DURATION.getValue();

    final CachedSidedInventoryStorage<PulverizerBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
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

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public PulverizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.PULVERIZER_ENTITY, blockPos, blockState);

        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        internalInventory = new SimpleInventory(3) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> world == null || RecipeUtils.isIngredientOfAny(world, PulverizerRecipe.Type.INSTANCE, stack);
                    case 1, 2 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.areItemsAndComponentsEqual(stack, itemStack))
                        resetProgress(pos, world.getBlockState(pos));
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                PulverizerBlockEntity.this.markDirty();
            }
        };
        inventory = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 3).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 0, i -> i == 1 || i == 2);
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);

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
                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            new EnergySyncS2CPacket(getAmount(), getCapacity(), getPos())
                    );
                }
            }
        };
        energyStorage = new EnergizedPowerLimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0) {
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
                    case 0, 1 -> ByteUtils.get2Bytes(PulverizerBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(PulverizerBlockEntity.this.maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(PulverizerBlockEntity.this.energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> redstoneMode.ordinal();
                    case 10 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> PulverizerBlockEntity.this.progress = ByteUtils.with2Bytes(
                            PulverizerBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> PulverizerBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            PulverizerBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6, 7, 8 -> {}
                    case 9 -> PulverizerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 10 -> PulverizerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
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
        return Text.translatable("container.energizedpower.pulverizer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new EnergySyncS2CPacket(internalEnergyStorage.getAmount(), internalEnergyStorage.getCapacity(), getPos()));

        return new PulverizerMenu(id, this, inventory, internalInventory, upgradeModuleInventory, this.data);
    }

    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> ScreenHandler.calculateComparatorOutput(internalInventory);
            case FLUID -> 0;
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        //Save Upgrade Module Inventory first
        nbt.put("upgrade_module_inventory", upgradeModuleInventory.saveToNBT(registries));

        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.heldStacks, registries));
        nbt.putLong("energy", internalEnergyStorage.getAmount());

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.max_progress", NbtInt.of(maxProgress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.loadFromNBT(nbt.getCompound("upgrade_module_inventory"), registries);
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.heldStacks, registries);
        internalEnergyStorage.setAmountWithoutUpdate(nbt.getLong("energy"));

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory);
        ItemScatterer.spawn(level, worldPosition, upgradeModuleInventory);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, PulverizerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.redstoneMode.isActive(state.get(PulverizerBlock.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            Optional<RecipeEntry<PulverizerRecipe>> recipe = level.getRecipeManager().getFirstMatch(PulverizerRecipe.Type.INSTANCE, blockEntity.internalInventory, level);
            if(recipe.isEmpty())
                return;

            if(blockEntity.maxProgress == 0)
                blockEntity.maxProgress = Math.max(1, (int)Math.ceil(RECIPE_DURATION /
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));

            long energyConsumptionPerTick = Math.max(1, (long)Math.ceil(ENERGY_USAGE_PER_TICK *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = energyConsumptionPerTick * blockEntity.maxProgress;

            if(energyConsumptionPerTick <= blockEntity.internalEnergyStorage.getAmount()) {
                blockEntity.hasEnoughEnergy = true;

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    markDirty(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.internalEnergyStorage.extract(energyConsumptionPerTick, transaction);
                    transaction.commit();
                }
                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

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
    }

    private static void craftItem(BlockPos blockPos, BlockState state, PulverizerBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<RecipeEntry<PulverizerRecipe>> recipe = level.getRecipeManager().getFirstMatch(PulverizerRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        ItemStack[] outputs = recipe.get().value().generateOutputs(level.random, false);

        blockEntity.internalInventory.removeStack(0, 1);
        blockEntity.internalInventory.setStack(1, outputs[0].copyWithCount(
                blockEntity.internalInventory.getStack(1).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            blockEntity.internalInventory.setStack(2, outputs[1].copyWithCount(
                    blockEntity.internalInventory.getStack(2).getCount() + outputs[1].getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(PulverizerBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<RecipeEntry<PulverizerRecipe>> recipe = level.getRecipeManager().getFirstMatch(PulverizerRecipe.Type.INSTANCE, blockEntity.internalInventory, level);
        if(recipe.isEmpty())
            return false;

        ItemStack[] maxOutputs = recipe.get().value().getMaxOutputCounts(false);

        return canInsertItemIntoOutputSlot(blockEntity.internalInventory, maxOutputs[0]) &&
                (maxOutputs[1].isEmpty() || canInsertItemIntoSecondaryOutputSlot(blockEntity.internalInventory, maxOutputs[1]));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getStack(1);

        return inventoryItemStack.isEmpty() || (ItemStack.areItemsAndComponentsEqual(inventoryItemStack, itemStack) &&
                inventoryItemStack.getMaxCount() >= inventoryItemStack.getCount() + itemStack.getCount());
    }

    private static boolean canInsertItemIntoSecondaryOutputSlot(SimpleInventory inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getStack(2);

        return inventoryItemStack.isEmpty() || (ItemStack.areItemsAndComponentsEqual(inventoryItemStack, itemStack) &&
                inventoryItemStack.getMaxCount() >= inventoryItemStack.getCount() + itemStack.getCount());
    }

    private void updateUpgradeModules() {
        resetProgress(getPos(), getCachedState());
        markDirty();
        if(world != null && !world.isClient()) {
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getPos(), (ServerWorld)world, 32,
                    new EnergySyncS2CPacket(internalEnergyStorage.getAmount(), internalEnergyStorage.getCapacity(), getPos())
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