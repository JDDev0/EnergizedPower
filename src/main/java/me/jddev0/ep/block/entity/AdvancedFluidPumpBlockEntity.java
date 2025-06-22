package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMultiTankMethods;
import me.jddev0.ep.block.entity.base.WorkerFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AdvancedFluidPumpMenu;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class AdvancedFluidPumpBlockEntity
        extends WorkerFluidMachineBlockEntity<CombinedStorage<FluidVariant, SimpleFluidStorage>, BlockPos> {
    public static final int NEXT_BLOCK_COOLDOWN = ModConfigs.COMMON_ADVANCED_FLUID_PUMP_NEXT_BLOCK_COOLDOWN.getValue();
    public static final int EXTRACTION_DURATION = ModConfigs.COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_DURATION.getValue();

    public static final int RANGE = ModConfigs.COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_RANGE.getValue();
    public static final int DEPTH = ModConfigs.COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_DEPTH.getValue();

    private int xOffset = -1;
    private int yOffset = 0;
    private int zOffset = -1;
    private boolean extractingFluid = false;

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> false);

    public AdvancedFluidPumpBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_FLUID_PUMP_ENTITY, blockPos, blockState,

                "advanced_fluid_pump",

                1, 1,

                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ADVANCED_FLUID_PUMP_CONSUMPTION_PER_TICK.getValue(),

                FluidStorageMultiTankMethods.INSTANCE,
                FluidUtils.convertMilliBucketsToDroplets(ModConfigs.COMMON_ADVANCED_FLUID_PUMP_FLUID_TANK_CAPACITY.getValue() * 1000),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.EXTRACTION_RANGE,
                UpgradeModuleModifier.EXTRACTION_DEPTH
        );
    }

    @Override
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public void markDirty() {
                super.markDirty();

                AdvancedFluidPumpBlockEntity.this.markDirty();
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0) {
                    return stack.isOf(Items.COBBLESTONE);
                }

                return super.isValid(slot, stack);
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.areItemsAndComponentsEqual(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }
        };
    }

    @Override
    protected CombinedStorage<FluidVariant, SimpleFluidStorage> initFluidStorage() {
        return new CombinedStorage<>(List.of(
                new SimpleFluidStorage(baseTankCapacity) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();
                        syncFluidToPlayers(32);
                    }
                },
                new SimpleFluidStorage(baseTankCapacity) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();
                        syncFluidToPlayers(32);
                    }
                },
                new SimpleFluidStorage(baseTankCapacity) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();
                        syncFluidToPlayers(32);
                    }
                },
                new SimpleFluidStorage(baseTankCapacity) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();
                        syncFluidToPlayers(32);
                    }
                }
        ));
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(() -> hasWork()?getCurrentWorkData().map(this::getEnergyConsumptionFor).orElse(-1L):-1, value -> {}),
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
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);

        return new AdvancedFluidPumpMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        view.putInt("target.xOffset", xOffset);
        view.putInt("target.yOffset", yOffset);
        view.putInt("target.zOffset", zOffset);

        view.putBoolean("recipe.extractingFluid", extractingFluid);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        xOffset = view.getInt("target.xOffset", 0);
        yOffset = view.getInt("target.yOffset", 0);
        zOffset = view.getInt("target.zOffset", 0);

        extractingFluid = view.getBoolean("recipe.extractingFluid", false);
    }

    @Override
    protected void onTickStart() {
        super.onTickStart();

        if(yOffset == 0) {
            goToNextOffset();
            markDirty();
        }
    }

    @Override
    protected boolean hasWork() {
        return yOffset != 0 && itemHandler.getStack(0).isOf(Items.COBBLESTONE);
    }

    @Override
    protected Optional<BlockPos> getCurrentWorkData() {
        return Optional.of(pos.add(xOffset, yOffset, zOffset));
    }

    @Override
    protected double getWorkDataDependentWorkDuration(BlockPos targetPos) {
        return extractingFluid?EXTRACTION_DURATION:NEXT_BLOCK_COOLDOWN;
    }

    @Override
    protected void onWorkStarted(BlockPos targetPos) {
        BlockState targetState = world.getBlockState(targetPos);
        if(!(targetState.getBlock() instanceof FluidDrainable))
            return;

        FluidState targetFluidState = world.getFluidState(targetPos);
        if(targetFluidState.isEmpty())
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            if(fluidStorage.insert(FluidVariant.of(targetFluidState.getFluid()), FluidConstants.BLOCK,
                    transaction) != FluidConstants.BLOCK)
                return;
        }

        extractingFluid = true;
    }

    @Override
    protected void onWorkCompleted(BlockPos targetPos) {
        BlockState targetState = world.getBlockState(targetPos);
        if(extractingFluid && targetState.getBlock() instanceof FluidDrainable targetBlock) {
            ItemStack bucketItemStack = targetBlock.tryDrainFluid(null, world, targetPos, targetState);

            if(!bucketItemStack.isEmpty()) {
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, targetPos);

                Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(bucketItemStack, ContainerItemContext.withConstant(bucketItemStack));
                if(fluidStorage != null) {
                    Iterator<StorageView<FluidVariant>> iterator = fluidStorage.iterator();
                    if(iterator.hasNext()) {
                        StorageView<FluidVariant> fluidView = iterator.next();
                        if(!iterator.hasNext()) {
                            FluidVariant fluidVariant = fluidView.getResource();
                            if(!fluidVariant.isBlank()) {
                                try(Transaction transaction = Transaction.openOuter()) {
                                    this.fluidStorage.insert(fluidVariant, fluidView.getAmount(), transaction);
                                    transaction.commit();
                                }

                                BlockState newTargetState = world.getBlockState(targetPos);
                                if(newTargetState.isAir() || newTargetState.isReplaceable()) {
                                    itemHandler.removeStack(0, 1);

                                    world.setBlockState(targetPos, Blocks.COBBLESTONE.getDefaultState(), 3);
                                }
                            }
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
                if(-yOffset >= depth || (getPos().getY() + yOffset) < world.getBottomY())
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