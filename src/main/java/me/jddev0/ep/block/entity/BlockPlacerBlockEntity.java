package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.BlockPlacerBlock;
import me.jddev0.ep.block.entity.base.NoWorkData;
import me.jddev0.ep.block.entity.base.WorkerMachineBlockEntity;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.BlockPlacerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BlockPlacerBlockEntity
        extends WorkerMachineBlockEntity<NoWorkData>
        implements CheckboxUpdate {
    private static final List<@NotNull ResourceLocation> PLACEMENT_BLACKLIST = ModConfigs.COMMON_BLOCK_PLACER_PLACEMENT_BLACKLIST.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> false);

    private boolean inverseRotation;

    public BlockPlacerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.BLOCK_PLACER_ENTITY.get(), blockPos, blockState,

                "block_placer",

                1, ModConfigs.COMMON_BLOCK_PLACER_PLACEMENT_DURATION.getValue(),

                ModConfigs.COMMON_BLOCK_PLACER_CAPACITY.getValue(),
                ModConfigs.COMMON_BLOCK_PLACER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_BLOCK_PLACER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
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
                    return stack.getItem() instanceof BlockItem;
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

            @Override
            public int getSlotLimit(int slot) {
                return 1;
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
                new BooleanValueContainerData(() -> inverseRotation, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new BlockPlacerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putBoolean("inverse_rotation", inverseRotation);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        inverseRotation = nbt.getBoolean("inverse_rotation");
    }

    @Override
    protected boolean hasWork() {
        ItemStack itemStack = itemHandler.getStackInSlot(0);
        if(itemStack.isEmpty() || !(itemStack.getItem() instanceof BlockItem blockItemStack))
            return false;

        return !PLACEMENT_BLACKLIST.contains(BuiltInRegistries.BLOCK.getKey(blockItemStack.getBlock()));
    }

    @Override
    protected Optional<NoWorkData> getCurrentWorkData() {
        return Optional.of(NoWorkData.INSTANCE);
    }

    @Override
    protected void onWorkStarted(NoWorkData workData) {}

    @Override
    protected void onWorkCompleted(NoWorkData workData) {
        int energyConsumptionPerTick = getEnergyConsumptionFor(workData);

        ItemStack itemStack = itemHandler.getStackInSlot(0);
        if(itemStack.isEmpty()) {
            energyConsumptionLeft = energyConsumptionPerTick;
            setChanged();

            return;
        }

        BlockPos blockPosPlacement = getBlockPos().relative(getBlockState().getValue(BlockPlacerBlock.FACING));

        BlockItem blockItem = (BlockItem)itemStack.getItem();
        final Direction direction;

        if(inverseRotation) {
            direction = switch(getBlockState().getValue(BlockPlacerBlock.FACING)) {
                case DOWN -> Direction.UP;
                case UP -> Direction.DOWN;
                case NORTH -> Direction.SOUTH;
                case SOUTH -> Direction.NORTH;
                case WEST -> Direction.EAST;
                case EAST -> Direction.WEST;
            };
        }else {
            direction = getBlockState().getValue(BlockPlacerBlock.FACING);
        }

        InteractionResult result = blockItem.place(new DirectionalPlaceContext(level, blockPosPlacement, direction, itemStack, direction) {
            @Override
            public @NotNull Direction getNearestLookingDirection() {
                return direction;
            }

            @Override
            public @NotNull Direction @NotNull [] getNearestLookingDirections() {
                return switch (direction) {
                    case DOWN ->
                            new Direction[] { Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP };
                    case UP ->
                            new Direction[] { Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
                    case NORTH ->
                            new Direction[] { Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN, Direction.SOUTH };
                    case SOUTH ->
                            new Direction[] { Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN, Direction.NORTH };
                    case WEST ->
                            new Direction[] { Direction.WEST, Direction.SOUTH, Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST };
                    case EAST ->
                            new Direction[] { Direction.EAST, Direction.SOUTH, Direction.UP, Direction.DOWN, Direction.NORTH, Direction.WEST };
                };
            }

            @Override
            public boolean replacingClickedOnBlock() {
                return false;
            }
        });

        if(result == InteractionResult.FAIL) {
            energyConsumptionLeft = energyConsumptionPerTick;
            setChanged();

            return;
        }

        itemHandler.setStackInSlot(0, itemStack);
        resetProgress();
    }

    public void setInverseRotation(boolean inverseRotation) {
        this.inverseRotation = inverseRotation;
        setChanged(level, getBlockPos(), getBlockState());
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Inverse rotation
            case 0 -> setInverseRotation(checked);
        }
    }
}