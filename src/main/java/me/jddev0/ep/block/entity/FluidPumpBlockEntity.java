package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.WorkerFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.FluidPumpMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FluidPumpBlockEntity
        extends WorkerFluidMachineBlockEntity<FluidTank, BlockPos> {
    public static final int NEXT_BLOCK_COOLDOWN = ModConfigs.COMMON_FLUID_PUMP_NEXT_BLOCK_COOLDOWN.getValue();
    public static final int EXTRACTION_DURATION = ModConfigs.COMMON_FLUID_PUMP_EXTRACTION_DURATION.getValue();

    public static final int RANGE = ModConfigs.COMMON_FLUID_PUMP_EXTRACTION_RANGE.getValue();
    public static final int DEPTH = ModConfigs.COMMON_FLUID_PUMP_EXTRACTION_DEPTH.getValue();

    private int xOffset = -1;
    private int yOffset = 0;
    private int zOffset = -1;
    private boolean extractingFluid = false;

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> false));

    public FluidPumpBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FLUID_PUMP_ENTITY.get(), blockPos, blockState,

                "fluid_pump",

                1, 1,

                ModConfigs.COMMON_FLUID_PUMP_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_PUMP_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_FLUID_PUMP_CONSUMPTION_PER_TICK.getValue(),

                FluidStorageSingleTankMethods.INSTANCE,
                ModConfigs.COMMON_FLUID_PUMP_FLUID_TANK_CAPACITY.getValue() * 1000,

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
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameTags(stack, itemStack))
                        resetProgress();
                }

                super.setStackInSlot(slot, stack);
            }
        };
    }

    @Override
    protected FluidTank initFluidStorage() {
        return new FluidTank(baseTankCapacity) {
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

        return new FluidPumpMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidStorage.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.putInt("target.xOffset", xOffset);
        nbt.putInt("target.yOffset", yOffset);
        nbt.putInt("target.zOffset", zOffset);

        nbt.putBoolean("recipe.extractingFluid", extractingFluid);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

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
            ItemStack bucketItemStack = targetBlock.pickupBlock(level, targetPos, targetState);

            if(!bucketItemStack.isEmpty()) {
                level.gameEvent(null, GameEvent.FLUID_PICKUP, targetPos);

                LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = bucketItemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
                if(!fluidStorageLazyOptional.isPresent())
                    return;

                IFluidHandlerItem fluidStorage = fluidStorageLazyOptional.orElse(null);
                if(fluidStorage.getTanks() == 1) {
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
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.EXTRACTION_RANGE) *
                //Also check old RANGE upgrade module for upgrade modules which were inserted before 2.13.0
                upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.RANGE));
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