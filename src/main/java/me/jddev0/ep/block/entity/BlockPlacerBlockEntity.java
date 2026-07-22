package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.BlockPlacerBlock;
import me.jddev0.ep.block.entity.base.NoWorkData;
import me.jddev0.ep.block.entity.base.WorkerMachineBlockEntity;
import me.jddev0.ep.component.MachineConfigurationComponent;
import me.jddev0.ep.inventory.data.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.BlockPlacerMenu;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.*;
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
import team.reborn.energy.api.EnergyStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockPlacerBlockEntity
        extends WorkerMachineBlockEntity<NoWorkData>
        implements CheckboxUpdate {
    private static final List<@NotNull Identifier> PLACEMENT_BLACKLIST = ModConfigs.COMMON_BLOCK_PLACER_PLACEMENT_BLACKLIST.getValue();

    private static final int ITEM_SLOT_INPUT = 0;

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
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_PULLING
        );
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public long getCapacity(int index, ItemVariant resource) {
                return 1;
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemVariant stack) {
                if(slot == 0) {
                    return stack.getItem() instanceof BlockItem;
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
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress[0], value -> progress[0] = value),
                new ProgressValueContainerData(() -> maxProgress[0], value -> maxProgress[0] = value),
                new EnergyValueContainerData(() -> hasWork(0)?getCurrentWorkData(0).
                        map(workData -> getEnergyConsumptionFor(0, workData)).orElse(-1L):-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[0], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[0], value -> {}),
                new BooleanValueContainerData(() -> inverseRotation, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncIOConfigurationToPlayer(player);

        return new BlockPlacerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        if(slotType == SlotType.ITEM) {
            return List.of(
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT))
            );
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.ITEM) {
            for(RelativeDirection direction: RelativeDirection.values())
                conf.setSlotGroupId(direction, 0);
        }

        return conf;
    }

    //TODO remove after io configuration is fully supported for BlockPlacer
    @Override
    public boolean onApplyMachineConfiguration(@NotNull MachineConfigurationComponent machineConfiguration) {
        if(Arrays.stream(getAvailableRedstoneModes()).
                noneMatch(redstoneMode -> redstoneMode == machineConfiguration.getRedstoneMode()))
            return false;

        if(Arrays.stream(getAvailableComparatorModes()).
                noneMatch(comparatorMode -> comparatorMode == machineConfiguration.getComparatorMode()))
            return false;

        //IO configuration is not yet supported: Invalid if present
        if(!machineConfiguration.getIOConfigurations().isEmpty())
            return false;

        boolean configChanged = redstoneMode != machineConfiguration.getRedstoneMode() ||
                comparatorMode != machineConfiguration.getComparatorMode();
        if(configChanged) {
            this.redstoneMode = machineConfiguration.getRedstoneMode();
            this.comparatorMode = machineConfiguration.getComparatorMode();
            setChanged();
        }

        return true;
    }

    //TODO remove after io configuration is fully supported for BlockPlacer
    @Override
    public @NotNull MachineConfigurationComponent onStoreMachineConfiguration() {
        return new MachineConfigurationComponent(redstoneMode, comparatorMode, Map.of());
    }

    //TODO remove and replace with io configuration
    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> false);
    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
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
    protected boolean hasWork(int thread) {
        ItemStack itemStack = itemHandler.getStackInSlot(0);
        if(itemStack.isEmpty() || !(itemStack.getItem() instanceof BlockItem blockItemStack))
            return false;

        return !PLACEMENT_BLACKLIST.contains(BuiltInRegistries.BLOCK.getKey(blockItemStack.getBlock()));
    }

    @Override
    protected Optional<NoWorkData> getCurrentWorkData(int thread) {
        return Optional.of(NoWorkData.INSTANCE);
    }

    @Override
    protected void onWorkStarted(int thread, NoWorkData workData) {}

    @Override
    protected void onWorkCompleted(int thread, NoWorkData workData) {
        long energyConsumptionPerTick = getEnergyConsumptionFor(thread, workData);

        ItemStack itemStack = itemHandler.getStackInSlot(0);
        if(itemStack.isEmpty()) {
            energyConsumptionLeft[thread] = energyConsumptionPerTick;
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
            energyConsumptionLeft[thread] = energyConsumptionPerTick;
            setChanged();

            return;
        }

        itemHandler.setStackInSlot(0, itemStack);
        resetProgress(thread);
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