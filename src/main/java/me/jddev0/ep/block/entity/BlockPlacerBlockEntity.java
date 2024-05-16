package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.BlockPlacerBlock;
import me.jddev0.ep.block.entity.base.WorkerMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.BlockPlacerMenu;
import me.jddev0.ep.util.ByteUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static me.jddev0.ep.block.entity.base.WorkerMachineBlockEntity.NoWorkData;

public class BlockPlacerBlockEntity
        extends WorkerMachineBlockEntity<NoWorkData>
        implements ExtendedScreenHandlerFactory, CheckboxUpdate {
    private static final List<@NotNull Identifier> PLACEMENT_BLACKLIST = ModConfigs.COMMON_BLOCK_PLACER_PLACEMENT_BLACKLIST.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> false);

    protected final PropertyDelegate data;
    private boolean inverseRotation;

    public BlockPlacerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.BLOCK_PLACER_ENTITY, blockPos, blockState,

                1, ModConfigs.COMMON_BLOCK_PLACER_PLACEMENT_DURATION.getValue(),

                ModConfigs.COMMON_BLOCK_PLACER_CAPACITY.getValue(),
                ModConfigs.COMMON_BLOCK_PLACER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_BLOCK_PLACER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(BlockPlacerBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(BlockPlacerBlockEntity.this.maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(BlockPlacerBlockEntity.this.energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> inverseRotation?1:0;
                    case 10 -> redstoneMode.ordinal();
                    case 11 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> BlockPlacerBlockEntity.this.progress = ByteUtils.with2Bytes(
                            BlockPlacerBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> BlockPlacerBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            BlockPlacerBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6, 7, 8 -> {}
                    case 9 -> BlockPlacerBlockEntity.this.inverseRotation = value != 0;
                    case 10 -> BlockPlacerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 11 -> BlockPlacerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 12;
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
                if(slot == 0) {
                    return stack.getItem() instanceof BlockItem;
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

                BlockPlacerBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.block_placer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);

        return new BlockPlacerMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("inverse_rotation", inverseRotation);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        inverseRotation = nbt.getBoolean("inverse_rotation");
    }

    @Override
    protected boolean hasWork() {
        ItemStack itemStack = itemHandler.getStack(0);
        if(itemStack.isEmpty() || !(itemStack.getItem() instanceof BlockItem blockItemStack))
            return false;

        return !PLACEMENT_BLACKLIST.contains(Registry.BLOCK.getId(blockItemStack.getBlock()));
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