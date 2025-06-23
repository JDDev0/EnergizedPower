package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMultiTankMethods;
import me.jddev0.ep.block.entity.base.WorkerFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AdvancedFluidPumpMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AdvancedFluidPumpBlockEntity
        extends WorkerFluidMachineBlockEntity<EnergizedPowerFluidStorage, BlockPos> {
    public static final int NEXT_BLOCK_COOLDOWN = ModConfigs.COMMON_ADVANCED_FLUID_PUMP_NEXT_BLOCK_COOLDOWN.getValue();
    public static final int EXTRACTION_DURATION = ModConfigs.COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_DURATION.getValue();

    public static final int RANGE = ModConfigs.COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_RANGE.getValue();
    public static final int DEPTH = ModConfigs.COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_DEPTH.getValue();

    private int xOffset = -1;
    private int yOffset = 0;
    private int zOffset = -1;
    private boolean extractingFluid = false;

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> false);

    public AdvancedFluidPumpBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_FLUID_PUMP_ENTITY.get(), blockPos, blockState,

                "advanced_fluid_pump",

                1, 1,

                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_CONSUMPTION_PER_TICK.getValue(),

                FluidStorageMultiTankMethods.INSTANCE,
                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_FLUID_TANK_CAPACITY.getValue() * 1000,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.EXTRACTION_RANGE,
                UpgradeModuleModifier.EXTRACTION_DEPTH
        );
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
                if(slot == 0) {
                    return stack.is(Items.COBBLESTONE);
                }

                return super.isItemValid(slot, stack);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setStackInSlot(slot, stack);
            }
        };
    }

    @Override
    protected EnergizedPowerFluidStorage initFluidStorage() {
        return new EnergizedPowerFluidStorage(new int[] {
                baseTankCapacity, baseTankCapacity, baseTankCapacity, baseTankCapacity
        }) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                syncFluidToPlayers(32);
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(() -> hasWork()?getCurrentWorkData().map(this::getEnergyConsumptionFor).orElse(-1):-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft, value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy, value -> {}),
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

        return new AdvancedFluidPumpMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("target.xOffset", xOffset);
        view.putInt("target.yOffset", yOffset);
        view.putInt("target.zOffset", zOffset);

        view.putBoolean("recipe.extractingFluid", extractingFluid);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        xOffset = view.getIntOr("target.xOffset", 0);
        yOffset = view.getIntOr("target.yOffset", 0);
        zOffset = view.getIntOr("target.zOffset", 0);

        extractingFluid = view.getBooleanOr("recipe.extractingFluid", false);
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
    protected boolean hasWork() {
        return yOffset != 0 && itemHandler.getStackInSlot(0).is(Items.COBBLESTONE);
    }

    @Override
    protected Optional<BlockPos> getCurrentWorkData() {
        return Optional.of(worldPosition.offset(xOffset, yOffset, zOffset));
    }

    @Override
    protected double getWorkDataDependentWorkDuration(BlockPos targetPos) {
        return extractingFluid?EXTRACTION_DURATION:NEXT_BLOCK_COOLDOWN;
    }

    @Override
    protected void onWorkStarted(BlockPos targetPos) {
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
    protected void onWorkCompleted(BlockPos targetPos) {
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

        resetProgress();

        goToNextOffset();
    }

    @Override
    protected void resetProgress() {
        super.resetProgress();

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
        int range = (int)Math.ceil(RANGE *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.EXTRACTION_RANGE));
        int depth = (int)Math.ceil(DEPTH *
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.EXTRACTION_DEPTH));

        if(yOffset == 0) {
            yOffset = -1;
            xOffset = range;
            zOffset = 0;
        }else if(zOffset >= range - Math.abs(xOffset)) {
            if(-xOffset >= range) {
                //Last position in depth = y was reached -> Go to depth = y - 1 or to depth = -1

                yOffset--;
                if(-yOffset >= depth || (getBlockPos().getY() + yOffset) < level.getMinY())
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