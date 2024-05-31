package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.WorkerFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.FluidPumpMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;

public class FluidPumpBlockEntity
        extends WorkerFluidMachineBlockEntity<SimpleFluidStorage, BlockPos> {
    public static final int NEXT_BLOCK_COOLDOWN = ModConfigs.COMMON_FLUID_PUMP_NEXT_BLOCK_COOLDOWN.getValue();
    public static final int EXTRACTION_DURATION = ModConfigs.COMMON_FLUID_PUMP_EXTRACTION_DURATION.getValue();

    public static final int RANGE = ModConfigs.COMMON_FLUID_PUMP_EXTRACTION_RANGE.getValue();
    public static final int DEPTH = ModConfigs.COMMON_FLUID_PUMP_EXTRACTION_DEPTH.getValue();

    private int xOffset = -1;
    private int yOffset = 0;
    private int zOffset = -1;
    private boolean extractingFluid = false;

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> false);

    public FluidPumpBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.FLUID_PUMP_ENTITY, blockPos, blockState,

                "fluid_pump",

                1, 1,

                ModConfigs.COMMON_FLUID_PUMP_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_PUMP_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_FLUID_PUMP_CONSUMPTION_PER_TICK.getValue(),

                FluidStorageSingleTankMethods.INSTANCE,
                FluidUtils.convertMilliBucketsToDroplets(ModConfigs.COMMON_FLUID_PUMP_FLUID_TANK_CAPACITY.getValue() * 1000),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.RANGE,
                UpgradeModuleModifier.EXTRACTION_DEPTH
        );
    }

    @Override
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
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
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.canCombine(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                FluidPumpBlockEntity.this.markDirty();
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
    protected PropertyDelegate initContainerData() {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> redstoneMode.ordinal();
                    case 10 -> comparatorMode.ordinal();
                    case 11, 12 -> ByteUtils.get2Bytes(xOffset, index - 9);
                    case 13, 14 -> ByteUtils.get2Bytes(yOffset, index - 11);
                    case 15, 16 -> ByteUtils.get2Bytes(zOffset, index - 13);
                    case 17 -> extractingFluid?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> FluidPumpBlockEntity.this.progress = ByteUtils.with2Bytes(
                            FluidPumpBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> FluidPumpBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            FluidPumpBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6, 7, 8 -> {}
                    case 9 -> FluidPumpBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 10 -> FluidPumpBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                    case 11, 12, 13, 14, 15, 16, 17 -> {}
                }
            }

            @Override
            public int size() {
                return 18;
            }
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);

        return new FluidPumpMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }
    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putInt("target.xOffset", xOffset);
        nbt.putInt("target.yOffset", yOffset);
        nbt.putInt("target.zOffset", zOffset);

        nbt.putBoolean("recipe.extractingFluid", extractingFluid);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

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