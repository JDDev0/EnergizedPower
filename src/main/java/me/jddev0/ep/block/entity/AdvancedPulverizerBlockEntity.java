package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedPulverizerBlock;
import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.recipe.PulverizerRecipe;
import me.jddev0.ep.screen.AdvancedPulverizerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
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
import team.reborn.energy.api.base.LimitingEnergyStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class AdvancedPulverizerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate, RedstoneModeUpdate, ComparatorModeUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_ADVANCED_PULVERIZER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_ADVANCED_PULVERIZER_TRANSFER_RATE.getValue();

    public static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_ADVANCED_PULVERIZER_ENERGY_CONSUMPTION_PER_TICK.getValue();
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(
            1000 * ModConfigs.COMMON_ADVANCED_PULVERIZER_TANK_CAPACITY.getValue());
    public static final long WATER_CONSUMPTION_PER_RECIPE = FluidUtils.convertMilliBucketsToDroplets(
            ModConfigs.COMMON_ADVANCED_PULVERIZER_WATER_USAGE_PER_RECIPE.getValue());

    final CachedSidedInventoryStorage<UnchargerBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    final CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage;

    protected final PropertyDelegate data;
    private int progress;
    private int maxProgress = ModConfigs.COMMON_ADVANCED_PULVERIZER_RECIPE_DURATION.getValue();
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public AdvancedPulverizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ADVANCED_PULVERIZER_ENTITY, blockPos, blockState);

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

                AdvancedPulverizerBlockEntity.this.markDirty();
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
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            new EnergySyncS2CPacket(amount, capacity, getPos())
                    );
                }
            }
        };
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);

        fluidStorage = new CombinedStorage<>(List.of(
                new SimpleFluidStorage(TANK_CAPACITY) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();

                        if(world != null && !world.isClient()) {
                            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                                    getPos(), (ServerWorld)world, 32,
                                    new FluidSyncS2CPacket(0, getFluid(), capacity, getPos())
                            );
                        }
                    }

                    private boolean isFluidValid(FluidVariant variant) {
                        return variant.isOf(Fluids.WATER);
                    }

                    @Override
                    protected boolean canInsert(FluidVariant variant) {
                        return isFluidValid(variant);
                    }

                    @Override
                    protected boolean canExtract(FluidVariant variant) {
                        return isFluidValid(variant);
                    }
                },
                new SimpleFluidStorage(TANK_CAPACITY) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();

                        if(world != null && !world.isClient()) {
                            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                                    getPos(), (ServerWorld)world, 32,
                                    new FluidSyncS2CPacket(1, getFluid(), capacity, getPos())
                            );
                        }
                    }

                    private boolean isFluidValid(FluidVariant variant) {
                        return variant.isOf(ModFluids.DIRTY_WATER);
                    }

                    @Override
                    protected boolean canInsert(FluidVariant variant) {
                        return isFluidValid(variant);
                    }

                    @Override
                    protected boolean canExtract(FluidVariant variant) {
                        return isFluidValid(variant);
                    }
                }
        ));

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(AdvancedPulverizerBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(AdvancedPulverizerBlockEntity.this.maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(AdvancedPulverizerBlockEntity.this.energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> redstoneMode.ordinal();
                    case 10 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> AdvancedPulverizerBlockEntity.this.progress = ByteUtils.with2Bytes(
                            AdvancedPulverizerBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> AdvancedPulverizerBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            AdvancedPulverizerBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6, 7, 8 -> {}
                    case 9 -> AdvancedPulverizerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 10 -> AdvancedPulverizerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
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
        return Text.translatable("container.energizedpower.advanced_pulverizer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, new EnergySyncS2CPacket(internalEnergyStorage.amount, internalEnergyStorage.capacity, getPos()));
        for(int i = 0;i < 2;i++)
            ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, new FluidSyncS2CPacket(i,
                    fluidStorage.parts.get(i).getFluid(), fluidStorage.parts.get(i).getCapacity(), getPos()));

        return new AdvancedPulverizerMenu(id, this, inventory, internalInventory, this.data);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> ScreenHandler.calculateComparatorOutput(internalInventory);
            case FLUID -> FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.heldStacks, registries));
        nbt.putLong("energy", internalEnergyStorage.amount);
        for(int i = 0;i < fluidStorage.parts.size();i++)
            nbt.put("fluid." + i, fluidStorage.parts.get(i).getFluid().toNBT(new NbtCompound(), registries));

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.heldStacks, registries);
        internalEnergyStorage.amount = nbt.getLong("energy");
        for(int i = 0;i < fluidStorage.parts.size();i++)
            fluidStorage.parts.get(i).setFluid(FluidStack.fromNbt(nbt.getCompound("fluid." + i), registries));

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.heldStacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AdvancedPulverizerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.redstoneMode.isActive(state.get(AdvancedPulverizerBlock.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            Optional<RecipeEntry<PulverizerRecipe>> recipe = level.getRecipeManager().getFirstMatch(PulverizerRecipe.Type.INSTANCE, blockEntity.internalInventory, level);
            if(recipe.isEmpty())
                return;

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = ENERGY_USAGE_PER_TICK * blockEntity.maxProgress;

            if(ENERGY_USAGE_PER_TICK <= blockEntity.internalEnergyStorage.amount) {
                blockEntity.hasEnoughEnergy = true;

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    markDirty(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.internalEnergyStorage.extract(ENERGY_USAGE_PER_TICK, transaction);
                    transaction.commit();
                }
                blockEntity.energyConsumptionLeft -= ENERGY_USAGE_PER_TICK;

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
        energyConsumptionLeft = -1;
        hasEnoughEnergy = true;
    }

    private static void craftItem(BlockPos blockPos, BlockState state, AdvancedPulverizerBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<RecipeEntry<PulverizerRecipe>> recipe = level.getRecipeManager().getFirstMatch(PulverizerRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        ItemStack[] outputs = recipe.get().value().generateOutputs(level.random, true);

        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.fluidStorage.extract(FluidVariant.of(Fluids.WATER), WATER_CONSUMPTION_PER_RECIPE, transaction);
            blockEntity.fluidStorage.insert(FluidVariant.of(ModFluids.DIRTY_WATER), WATER_CONSUMPTION_PER_RECIPE, transaction);

            transaction.commit();
        }

        blockEntity.internalInventory.removeStack(0, 1);
        blockEntity.internalInventory.setStack(1, outputs[0].copyWithCount(
                blockEntity.internalInventory.getStack(1).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            blockEntity.internalInventory.setStack(2, outputs[1].copyWithCount(
                    blockEntity.internalInventory.getStack(2).getCount() + outputs[1].getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(AdvancedPulverizerBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<RecipeEntry<PulverizerRecipe>> recipe = level.getRecipeManager().getFirstMatch(PulverizerRecipe.Type.INSTANCE, blockEntity.internalInventory, level);
        if(recipe.isEmpty())
            return false;

        ItemStack[] maxOutputs = recipe.get().value().getMaxOutputCounts(true);

        return blockEntity.fluidStorage.parts.get(0).getAmount() >= WATER_CONSUMPTION_PER_RECIPE &&
                blockEntity.fluidStorage.parts.get(1).getCapacity() - blockEntity.fluidStorage.parts.get(1).getAmount() >= WATER_CONSUMPTION_PER_RECIPE &&
                canInsertItemIntoOutputSlot(blockEntity.internalInventory, maxOutputs[0]) &&
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

    public FluidStack getFluid(int tank) {
        return fluidStorage.parts.get(tank).getFluid();
    }

    public long getTankCapacity(int tank) {
        return fluidStorage.parts.get(tank).getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        internalEnergyStorage.capacity = capacity;
    }

    public long getEnergy() {
        return internalEnergyStorage.amount;
    }

    public long getCapacity() {
        return internalEnergyStorage.capacity;
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.parts.get(tank).setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        //Does nothing (capacity is final)
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        markDirty();
    }

    @Override
    public void setNextComparatorMode() {
        comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        markDirty();
    }
}