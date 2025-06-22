package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.BlockPlacerBlock;
import me.jddev0.ep.block.entity.base.NoWorkData;
import me.jddev0.ep.block.entity.base.WorkerMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.BlockPlacerMenu;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BlockPlacerBlockEntity
        extends WorkerMachineBlockEntity<NoWorkData>
        implements CheckboxUpdate {
    private static final List<@NotNull Identifier> PLACEMENT_BLACKLIST = ModConfigs.COMMON_BLOCK_PLACER_PLACEMENT_BLACKLIST.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> false);

    private boolean inverseRotation;

    public BlockPlacerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.BLOCK_PLACER_ENTITY, blockPos, blockState,

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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0) {
                    return stack.getItem() instanceof BlockItem;
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

            @Override
            public void markDirty() {
                super.markDirty();

                BlockPlacerBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(() -> hasWork()?getCurrentWorkData().map(this::getEnergyConsumptionFor).orElse(-1L):-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft, value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy, value -> {}),
                new BooleanValueContainerData(() -> inverseRotation, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);

        return new BlockPlacerMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        view.putBoolean("inverse_rotation", inverseRotation);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        inverseRotation = view.getBoolean("inverse_rotation", false);
    }

    @Override
    protected boolean hasWork() {
        ItemStack itemStack = itemHandler.getStack(0);
        if(itemStack.isEmpty() || !(itemStack.getItem() instanceof BlockItem blockItemStack))
            return false;

        return !PLACEMENT_BLACKLIST.contains(Registries.BLOCK.getId(blockItemStack.getBlock()));
    }

    @Override
    protected Optional<NoWorkData> getCurrentWorkData() {
        return Optional.of(NoWorkData.INSTANCE);
    }

    @Override
    protected void onWorkStarted(NoWorkData workData) {}

    @Override
    protected void onWorkCompleted(NoWorkData workData) {
        long energyConsumptionPerTick = getEnergyConsumptionFor(workData);

        ItemStack itemStack = itemHandler.getStack(0);
        if(itemStack.isEmpty()) {
            energyConsumptionLeft = energyConsumptionPerTick;
            markDirty();

            return;
        }

        BlockPos blockPosPlacement = getPos().offset(getCachedState().get(BlockPlacerBlock.FACING));

        BlockItem blockItem = (BlockItem)itemStack.getItem();
        final Direction direction;

        if(inverseRotation) {
            direction = switch(getCachedState().get(BlockPlacerBlock.FACING)) {
                case DOWN -> Direction.UP;
                case UP -> Direction.DOWN;
                case NORTH -> Direction.SOUTH;
                case SOUTH -> Direction.NORTH;
                case WEST -> Direction.EAST;
                case EAST -> Direction.WEST;
            };
        }else {
            direction = getCachedState().get(BlockPlacerBlock.FACING);
        }

        ActionResult result = blockItem.place(new AutomaticItemPlacementContext(world, blockPosPlacement, direction, itemStack, direction) {
            @Override
            public @NotNull Direction getPlayerLookDirection() {
                return direction;
            }

            @Override
            public @NotNull Direction @NotNull [] getPlacementDirections() {
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
            public boolean canReplaceExisting() {
                return false;
            }
        });

        if(result == ActionResult.FAIL) {
            energyConsumptionLeft = energyConsumptionPerTick;
            markDirty();

            return;
        }

        itemHandler.setStack(0, itemStack);
        resetProgress();
    }

    public void setInverseRotation(boolean inverseRotation) {
        this.inverseRotation = inverseRotation;
        markDirty(world, getPos(), getCachedState());
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Inverse rotation
            case 0 -> setInverseRotation(checked);
        }
    }
}