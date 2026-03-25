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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                if(slot == 0) {
                    return stack.getItem() instanceof BlockItem;
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getItem(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setItem(slot, stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                BlockPlacerBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
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
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new BlockPlacerMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putBoolean("inverse_rotation", inverseRotation);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        inverseRotation = view.getBooleanOr("inverse_rotation", false);
    }

    @Override
    protected boolean hasWork() {
        ItemStack itemStack = itemHandler.getItem(0);
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
        long energyConsumptionPerTick = getEnergyConsumptionFor(workData);

        ItemStack itemStack = itemHandler.getItem(0);
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

        itemHandler.setItem(0, itemStack);
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