package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidFillerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.FluidFillerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

public class FluidFillerBlockEntity
        extends ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <EnergizedPowerEnergyStorage, SimpleInventory, SimpleFluidStorage>
        implements ExtendedScreenHandlerFactory<BlockPos> {
    /**
     * MAX_FLUID_DRAINING_PER_TICK is in Milli Buckets
     */
    public static final long MAX_FLUID_FILLING_PER_TICK = ModConfigs.COMMON_FLUID_FILLER_FLUID_ITEM_TRANSFER_RATE.getValue();
    public static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FLUID_FILLER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i != 0)
            return false;

        ItemStack itemStack = itemHandler.getStack(i);

        if(ContainerItemContext.withConstant(itemStack).find(FluidStorage.ITEM) == null)
            return true;

        Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(itemStack, ContainerItemContext.
                ofSingleSlot(InventoryStorage.of(itemHandler, null).getSlots().get(i)));
        if(fluidStorage == null)
            return true;

        if(!fluidStorage.supportsInsertion())
            return true;

        boolean isFluidValid = false;
        if(!FluidFillerBlockEntity.this.fluidStorage.isEmpty()) {
            FluidFillerBlockEntity.this.forceAllowStackUpdateFlag = true;
            //Get current transaction for simulation only [Transaction is necessary to find out if a fluid variant is valid]
            try(Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
                long inserted = fluidStorage.insert(FluidFillerBlockEntity.this.fluidStorage.getResource(),
                        Long.MAX_VALUE, transaction);
                isFluidValid = inserted > 0;

                transaction.abort();
            }finally {
                FluidFillerBlockEntity.this.forceAllowStackUpdateFlag = false;
            }
        }

        for(StorageView<FluidVariant> fluidView:fluidStorage) {
            FluidVariant fluidVariant = fluidView.getResource();

            if(fluidView.getCapacity() > fluidView.getAmount() && (FluidFillerBlockEntity.this.fluidStorage.isEmpty() ||
                    (fluidVariant.isBlank() && isFluidValid) ||
                    fluidVariant.equals(FluidFillerBlockEntity.this.fluidStorage.getResource())))
                return false;
        }

        return true;
    });

    protected final PropertyDelegate data;
    private long fluidFillingLeft = -1;
    private long fluidFillingSumPending = 0;

    private boolean forceAllowStackUpdateFlag = false;

    public FluidFillerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.FLUID_FILLER_ENTITY, blockPos, blockState,

                ModConfigs.COMMON_FLUID_FILLER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_FILLER_TRANSFER_RATE.getValue(),

                1,

                FluidStorageSingleTankMethods.INSTANCE,
                FluidUtils.convertMilliBucketsToDroplets(ModConfigs.COMMON_FLUID_FILLER_FLUID_TANK_CAPACITY.getValue() * 1000),

                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(FluidFillerBlockEntity.this.fluidFillingLeft, index);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(FluidFillerBlockEntity.this.fluidFillingSumPending, index - 4);
                    case 8 -> redstoneMode.ordinal();
                    case 9 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3, 4, 5, 6, 7 -> {}
                    case 8 -> FluidFillerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 9 -> FluidFillerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 10;
            }
        };
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
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0)
                    return ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null;

                return super.isValid(slot, stack);
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(!forceAllowStackUpdateFlag && !stack.isEmpty() && !itemStack.isEmpty() &&
                            (!ItemStack.areItemsEqual(stack, itemStack) || (!ItemStack.areItemsAndComponentsEqual(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no fluid item
                                    !(ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null &&
                                            ContainerItemContext.withConstant(itemStack).find(FluidStorage.ITEM) != null))))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                FluidFillerBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected SimpleFluidStorage initFluidStorage() {
        return new SimpleFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                markDirty();
                syncFluidToPlayers(32);
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.fluid_filler");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);

        return new FluidFillerMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidFillingLeft,
                "recipe.fluid_filling_left", "recipe.fluid_filling_left_leftover_droplets", nbt);
        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidFillingSumPending,
                "recipe.fluid_filling_sum_pending", "recipe.fluid_filling_sum_pending_leftover_droplets", nbt);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        super.readNbt(nbt, registries);

        fluidFillingLeft = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_filling_left",
                "recipe.fluid_filling_left_leftover_droplets", nbt);
        fluidFillingSumPending = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_filling_sum_pending",
                "recipe.fluid_filling_sum_pending_leftover_droplets", nbt);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, FluidFillerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.redstoneMode.isActive(state.get(FluidFillerBlock.POWERED)))
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack itemStack = blockEntity.itemHandler.getStack(0);
            long fluidFillingSum = 0;
            long fluidFillingLeftSum = 0;

            if(blockEntity.fluidStorage.getAmount() - blockEntity.fluidFillingSumPending <= 0)
                return;

            long energyConsumptionPerTick = Math.max(1, (long)Math.ceil(ENERGY_USAGE_PER_TICK *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyStorage.getAmount() < energyConsumptionPerTick)
                return;

            if(ContainerItemContext.withConstant(itemStack).find(FluidStorage.ITEM) == null)
                return;

            Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(itemStack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(blockEntity.itemHandler, null).getSlots().get(0)));
            if(fluidStorage == null)
                return;

            boolean isFluidValid = false;
            if(!blockEntity.fluidStorage.isEmpty()) {
                blockEntity.forceAllowStackUpdateFlag = true;
                try(Transaction transaction = Transaction.openOuter()) {
                    long inserted = fluidStorage.insert(blockEntity.fluidStorage.getResource(),
                            Long.MAX_VALUE, transaction);
                    isFluidValid = inserted > 0;

                    transaction.abort();
                }finally {
                    blockEntity.forceAllowStackUpdateFlag = false;
                }
            }

            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(fluidView.getCapacity() > fluidView.getAmount() && ((blockEntity.fluidStorage.isEmpty() ||
                        (fluidVariant.isBlank() && isFluidValid)) ||
                        fluidVariant.equals(blockEntity.fluidStorage.getResource()))) {
                    fluidFillingSum += Math.min(blockEntity.fluidStorage.getAmount() -
                                    blockEntity.fluidFillingSumPending - fluidFillingSum,
                            Math.min(fluidView.getCapacity() - fluidView.getAmount(),
                                    FluidUtils.convertMilliBucketsToDroplets(MAX_FLUID_FILLING_PER_TICK) - fluidFillingSum));

                    fluidFillingLeftSum += fluidView.getCapacity() - fluidView.getAmount();
                }
            }

            if(fluidFillingSum == 0)
                return;

            blockEntity.fluidFillingLeft = fluidFillingLeftSum;
            blockEntity.fluidFillingSumPending += fluidFillingSum;

            blockEntity.forceAllowStackUpdateFlag = true;
            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);

                long fluidSumFillable = Math.min(blockEntity.fluidStorage.getAmount(),
                        blockEntity.fluidFillingSumPending);

                FluidVariant fluidVariantToFill = blockEntity.fluidStorage.getResource();

                long fluidSumFilled = fluidStorage.insert(fluidVariantToFill, fluidSumFillable, transaction);

                if(fluidSumFilled > 0) {
                    blockEntity.fluidStorage.extract(fluidVariantToFill, fluidSumFilled, transaction);
                    blockEntity.fluidFillingSumPending -= fluidSumFilled;
                    blockEntity.fluidFillingLeft = fluidFillingLeftSum - fluidSumFilled;
                }

                transaction.commit();
            }finally {
                blockEntity.forceAllowStackUpdateFlag = false;
            }

            if(blockEntity.fluidFillingLeft <= 0)
                blockEntity.resetProgress();

            markDirty(level, blockPos, state);
        }else {
            blockEntity.resetProgress();
            markDirty(level, blockPos, state);
        }
    }

    private void resetProgress() {
        fluidFillingLeft = -1;
        fluidFillingSumPending = 0;
    }

    private boolean hasRecipe() {
        ItemStack itemStack = itemHandler.getStack(0);
        if(ContainerItemContext.withConstant(itemStack).find(FluidStorage.ITEM) != null) {
            Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(itemStack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(itemHandler, null).getSlots().get(0)));
            if(fluidStorage == null)
                return false;

            boolean isFluidValid = false;
            if(!FluidFillerBlockEntity.this.fluidStorage.isEmpty()) {
                forceAllowStackUpdateFlag = true;
                try(Transaction transaction = Transaction.openOuter()) {
                    long inserted = fluidStorage.insert(FluidFillerBlockEntity.this.fluidStorage.getResource(),
                            Long.MAX_VALUE, transaction);
                    isFluidValid = inserted > 0;

                    transaction.abort();
                }finally {
                    forceAllowStackUpdateFlag = false;
                }
            }

            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(fluidView.getCapacity() > fluidView.getAmount() && (FluidFillerBlockEntity.this.fluidStorage.isEmpty() ||
                        (fluidVariant.isBlank() && isFluidValid) ||
                        fluidVariant.equals(FluidFillerBlockEntity.this.fluidStorage.getResource())))
                    return true;
            }
        }

        return false;
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress();

        super.updateUpgradeModules();
    }
}