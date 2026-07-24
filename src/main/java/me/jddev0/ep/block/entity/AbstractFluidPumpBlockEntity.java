package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.UpgradableMenuProvider;
import me.jddev0.ep.block.entity.base.WorkerFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.data.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public abstract class AbstractFluidPumpBlockEntity
        extends WorkerFluidMachineBlockEntity<BlockPos> {
    protected final UpgradableMenuProvider menuProvider;

    private final int nextBlockCooldown;
    private final int extractionDuration;

    private final int baseExtractionRange;
    private final int baseExtractionDepth;

    private int xOffset = -1;
    private int yOffset = 0;
    private int zOffset = -1;
    private boolean extractingFluid = false;

    private static final int ITEM_SLOT_INPUT = 0;

    //Fluid slot indices are dynamic

    public AbstractFluidPumpBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        String machineName, UpgradableMenuProvider menuProvider,
                                        int baseEnergyCapacity, int baseEnergyTransferRate, int baseEnergyConsumptionPerTick,
                                        int baseTankCapacity,
                                        int nextBlockCooldown, int extractionDuration,
                                        int baseExtractionRange, int baseExtractionDepth) {
        super(
                type, blockPos, blockState,

                machineName, 1, 1,

                baseEnergyCapacity, baseEnergyTransferRate, baseEnergyConsumptionPerTick,

                baseTankCapacity,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.EXTRACTION_RANGE,
                UpgradeModuleModifier.EXTRACTION_DEPTH,
                UpgradeModuleModifier.ITEM_PULLING
        );

        this.menuProvider = menuProvider;

        this.nextBlockCooldown = nextBlockCooldown;
        this.extractionDuration = extractionDuration;

        this.baseExtractionRange = baseExtractionRange;
        this.baseExtractionDepth = baseExtractionDepth;
    }

    protected abstract int initTankCount();

    @Override
    protected final int initWorkerThreadCount() {
        return 1;
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    return stack.is(Items.COBBLESTONE);
                }

                return super.isValid(slot, stack);
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(0);
                }

                setChanged();
            }
        };
    }

    @Override
    protected EnergizedPowerFluidStorage initFluidStorage() {
        return new EnergizedPowerFluidStorage(initTankCount(), baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(32);
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress[0], value -> progress[0] = value),
                new ProgressValueContainerData(() -> maxProgress[0], value -> maxProgress[0] = value),
                new EnergyValueContainerData(() -> hasWork(0)?getCurrentWorkData(0).
                        map(workData -> getEnergyConsumptionFor(0, workData)).orElse(-1):-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[0], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[0], value -> {}),
                new IntegerValueContainerData(() -> xOffset, value -> {}),
                new IntegerValueContainerData(() -> yOffset, value -> {}),
                new IntegerValueContainerData(() -> zOffset, value -> {}),
                new BooleanValueContainerData(() -> extractingFluid, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);
        syncIOConfigurationToPlayer(player);

        return menuProvider.createMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        return switch(slotType) {
            case ITEM -> List.of(
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT))
            );
            case FLUID -> {
                List<SlotEntry> allOutputs = IntStream.range(0, initTankCount()).mapToObj(SlotEntry::ofOutput).toList();

                yield List.of(
                        SlotGroup.of(allOutputs)
                );
            }
        };
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        switch(slotType) {
            case ITEM -> {
                for(RelativeDirection direction:RelativeDirection.values())
                    conf.setSlotGroupId(direction, 0);
            }
            case FLUID -> {
                for(RelativeDirection direction:RelativeDirection.values())
                    conf.setSlotGroupId(direction, 0);
            }
        }

        return conf;
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandler, facing, side);
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.FLUID);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.FLUID);
        return conf.createSidedFluidHandlerFor(slotGroups, fluidStorage, facing, side);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putInt("target.xOffset", xOffset);
        nbt.putInt("target.yOffset", yOffset);
        nbt.putInt("target.zOffset", zOffset);

        nbt.putBoolean("recipe.extractingFluid", extractingFluid);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        xOffset = nbt.getInt("target.xOffset");
        yOffset = nbt.getInt("target.yOffset");
        zOffset = nbt.getInt("target.zOffset");

        extractingFluid = nbt.getBoolean("recipe.extractingFluid");
    }

    @Override
    protected void onTickStart() {
        super.onTickStart();

        if(yOffset == 0) {
            goToNextOffset();
            setChanged();
        }
    }

    @Override
    protected boolean hasWork(int thread) {
        return yOffset != 0 && itemHandler.getStackInSlot(0).is(Items.COBBLESTONE);
    }

    @Override
    protected Optional<BlockPos> getCurrentWorkData(int thread) {
        return Optional.of(worldPosition.offset(xOffset, yOffset, zOffset));
    }

    @Override
    protected double getWorkDataDependentWorkDuration(int thread, BlockPos targetPos) {
        return extractingFluid?extractionDuration:nextBlockCooldown;
    }

    @Override
    protected void onWorkStarted(int thread, BlockPos targetPos) {
        BlockState targetState = level.getBlockState(targetPos);
        if(!(targetState.getBlock() instanceof BucketPickup))
            return;

        FluidState targetFluidState = level.getFluidState(targetPos);
        if(targetFluidState.isEmpty())
            return;

        if(fluidStorage.fill(new FluidStack(targetFluidState.getType(), 1000), IFluidHandler.FluidAction.SIMULATE) != 1000)
            return;

        extractingFluid = true;
    }

    @Override
    protected void onWorkCompleted(int thread, BlockPos targetPos) {
        BlockState targetState = level.getBlockState(targetPos);
        if(extractingFluid && targetState.getBlock() instanceof BucketPickup targetBlock) {
            ItemStack bucketItemStack = targetBlock.pickupBlock(null, level, targetPos, targetState);

            if(!bucketItemStack.isEmpty()) {
                level.gameEvent(null, GameEvent.FLUID_PICKUP, targetPos);

                IFluidHandlerItem fluidStorage = bucketItemStack.getCapability(Capabilities.FluidHandler.ITEM);
                if(fluidStorage != null && fluidStorage.getTanks() == 1) {
                    FluidStack fluidStack = fluidStorage.getFluidInTank(0);

                    if(!fluidStack.isEmpty()) {
                        this.fluidStorage.fill(fluidStack.copy(), IFluidHandler.FluidAction.EXECUTE);

                        BlockState newTargetState = level.getBlockState(targetPos);
                        if(newTargetState.isAir() || newTargetState.canBeReplaced()) {
                            itemHandler.extractItem(0, 1, false);

                            level.setBlock(targetPos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        resetProgress(thread);

        goToNextOffset();
    }

    @Override
    protected void resetProgress(int thread) {
        super.resetProgress(thread);

        extractingFluid = false;
    }

    @Override
    protected void updateUpgradeModules() {
        //Reset yOffset to start from depth = -1 again
        xOffset = -1;
        yOffset = 0;
        zOffset = -1;

        super.updateUpgradeModules();
    }

    public void goToNextOffset() {
        int range = (int)Math.ceil(baseExtractionRange *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.EXTRACTION_RANGE));
        int depth = (int)Math.ceil(baseExtractionDepth *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.EXTRACTION_DEPTH));

        if(yOffset == 0) {
            yOffset = -1;
            xOffset = range;
            zOffset = 0;
        }else if(zOffset >= range - Math.abs(xOffset)) {
            if(-xOffset >= range) {
                //Last position in depth = y was reached -> Go to depth = y - 1 or to depth = -1

                yOffset--;
                if(-yOffset >= depth || (getBlockPos().getY() + yOffset) < level.getMinBuildHeight())
                    yOffset = -1;

                xOffset = range;
                zOffset = 0;

                return;
            }

            xOffset--;
            zOffset = Math.abs(xOffset) - range;
        }else {
            zOffset++;
        }
    }
}