package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.TeleporterBlock;
import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.TeleporterMenu;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.LimitingEnergyStorage;

import java.util.HashSet;
import java.util.stream.IntStream;
import java.util.List;

public class TeleporterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate {
    public static final boolean INTRA_DIMENSIONAL_ENABLED = ModConfigs.COMMON_TELEPORTER_INTRA_DIMENSIONAL_ENABLED.getValue();
    public static final boolean INTER_DIMENSIONAL_ENABLED = ModConfigs.COMMON_TELEPORTER_INTER_DIMENSIONAL_ENABLED.getValue();
    public static final List<@NotNull Identifier> DIMENSION_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_DIMENSION_BLACKLIST.getValue();
    public static final List<@NotNull Identifier> INTRA_DIMENSIONAL_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTRA_DIMENSIONAL_BLACKLIST.getValue();
    public static final List<@NotNull Identifier> INTER_DIMENSIONAL_FROM_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTER_DIMENSIONAL_FROM_BLACKLIST.getValue();
    public static final List<@NotNull Identifier> INTER_DIMENSIONAL_TO_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTER_DIMENSIONAL_TO_BLACKLIST.getValue();
    public static final List<@NotNull Identifier> DIMENSION_TYPE_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_DIMENSION_TYPE_BLACKLIST.getValue();
    public static final List<@NotNull Identifier> INTRA_DIMENSIONAL_TYPE_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTRA_DIMENSIONAL_TYPE_BLACKLIST.getValue();
    public static final List<@NotNull Identifier> INTER_DIMENSIONAL_FROM_TYPE_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTER_DIMENSIONAL_FROM_TYPE_BLACKLIST.getValue();
    public static final List<@NotNull Identifier> INTER_DIMENSIONAL_TO_TYPE_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTER_DIMENSIONAL_TO_TYPE_BLACKLIST.getValue();

    public static final long CAPACITY = ModConfigs.COMMON_TELEPORTER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_TELEPORTER_TRANSFER_RATE.getValue();

    final CachedSidedInventoryStorage<BlockPlacerBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;


    public TeleporterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.TELEPORTER_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(1) {
            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0) {
                    return stack.isOf(ModItems.TELEPORTER_MATRIX);
                }

                return super.isValid(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                setChangedAndUpdateReadyState();
            }
        };
        inventory = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 1).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> true, i -> true);
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);

        internalEnergyStorage = new EnergizedPowerEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            protected void onFinalCommit() {
                setChangedAndUpdateReadyState();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(amount);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.ENERGY_SYNC_ID, buffer
                    );
                }
            }
        };
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.teleporter");
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(internalInventory);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.amount);
        buffer.writeLong(internalEnergyStorage.capacity);
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.ENERGY_SYNC_ID, buffer);

        return new TeleporterMenu(id, this, inventory, internalInventory);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.stacks));
        nbt.putLong("energy", internalEnergyStorage.amount);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
        internalEnergyStorage.amount = nbt.getLong("energy");
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.stacks);
    }

    public void setChangedAndUpdateReadyState() {
        boolean oldPowered = world.getBlockState(pos).contains(TeleporterBlock.POWERED) &&
                world.getBlockState(pos).get(TeleporterBlock.POWERED);

        ItemStack teleporterMatrixItemStack = internalInventory.getStack(0);

        boolean powered = internalEnergyStorage.amount == internalEnergyStorage.capacity &&
                teleporterMatrixItemStack.isOf(ModItems.TELEPORTER_MATRIX) &&
                TeleporterMatrixItem.isLinked(teleporterMatrixItemStack);

        if(oldPowered ^ powered)
            world.setBlockState(pos, getCachedState().with(TeleporterBlock.POWERED, powered), 3);

        markDirty();
    }

    public void teleportPlayer(ServerPlayerEntity player) {
        EnergyStorage energyStorage = EnergyStorage.SIDED.find(player.getWorld(), pos, null);
        if(energyStorage == null)
            return;

        if(energyStorage.getAmount() < TeleporterBlockEntity.CAPACITY) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.not_enough_energy").
                            formatted(Formatting.RED)
            ));

            return;
        }

        ItemStack teleporterMatrixItemStack = getStack(0);
        if(!teleporterMatrixItemStack.isOf(ModItems.TELEPORTER_MATRIX)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.no_teleporter_matrix").
                            formatted(Formatting.RED)
            ));

            return;
        }

        if(!TeleporterMatrixItem.isLinked(teleporterMatrixItemStack)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.teleporter_matrix_not_bound").
                            formatted(Formatting.RED)
            ));

            return;
        }

        BlockPos toPos = TeleporterMatrixItem.getBlockPos(world, teleporterMatrixItemStack);
        if(toPos == null) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.teleporter_matrix_invalid_position").
                            formatted(Formatting.RED)
            ));

            return;
        }

        World toDimension = TeleporterMatrixItem.getDimension(world, teleporterMatrixItemStack);
        if(!(toDimension instanceof ServerWorld)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.teleporter_matrix_invalid_dimension").
                            formatted(Formatting.RED)
            ));

            return;
        }

        if(pos.equals(toPos) && world.getRegistryKey().equals(toDimension.getRegistryKey())) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.teleporter_self_position").
                            formatted(Formatting.RED)
            ));

            return;
        }

        Identifier fromDimensionId = world.getRegistryKey().getValue();
        Identifier toDimensionId = toDimension.getRegistryKey().getValue();

        boolean intraDimensional = fromDimensionId.equals(toDimensionId);

        //Intra dimensional enabled
        if(intraDimensional && !TeleporterBlockEntity.INTRA_DIMENSIONAL_ENABLED) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.intra_dimensional_disabled",
                                    fromDimensionId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }

        //Inter dimensional enabled
        if(!intraDimensional && !TeleporterBlockEntity.INTER_DIMENSIONAL_ENABLED) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.inter_dimensional_disabled",
                                    fromDimensionId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }

        //Dimension Blacklist
        if(TeleporterBlockEntity.DIMENSION_BLACKLIST.contains(fromDimensionId)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.blacklist.dimension",
                                    fromDimensionId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }
        if(TeleporterBlockEntity.DIMENSION_BLACKLIST.contains(toDimensionId)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.blacklist.dimension",
                                    toDimensionId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }

        //Intra Dimension Blacklist
        if(intraDimensional && TeleporterBlockEntity.INTRA_DIMENSIONAL_BLACKLIST.contains(fromDimensionId)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.blacklist.intra_dimensional",
                                    fromDimensionId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }

        //Inter Dimension From Blacklist
        if(!intraDimensional && TeleporterBlockEntity.INTER_DIMENSIONAL_FROM_BLACKLIST.contains(fromDimensionId)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.blacklist.inter_dimensional_from",
                                    fromDimensionId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }

        //Inter Dimension To Blacklist
        if(!intraDimensional && TeleporterBlockEntity.INTER_DIMENSIONAL_TO_BLACKLIST.contains(toDimensionId)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.blacklist.inter_dimensional_to",
                                    toDimensionId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }

        Identifier fromDimensionTypeId = world.getDimensionKey().getValue();
        Identifier toDimensionTypeId = toDimension.getDimensionKey().getValue();

        //Dimension Type Blacklist
        if(TeleporterBlockEntity.DIMENSION_TYPE_BLACKLIST.contains(fromDimensionTypeId)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.blacklist.dimension_type",
                                    fromDimensionTypeId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }
        if(TeleporterBlockEntity.DIMENSION_TYPE_BLACKLIST.contains(toDimensionTypeId)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.blacklist.dimension_type",
                                    toDimensionTypeId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }

        //Intra Dimension Type Blacklist
        if(intraDimensional && TeleporterBlockEntity.INTRA_DIMENSIONAL_TYPE_BLACKLIST.contains(fromDimensionTypeId)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.blacklist.intra_dimensional_type",
                                    fromDimensionTypeId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }

        //Inter Dimension From Type Blacklist
        if(!intraDimensional && TeleporterBlockEntity.INTER_DIMENSIONAL_FROM_TYPE_BLACKLIST.contains(fromDimensionTypeId)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.blacklist.inter_dimensional_from_type",
                                    fromDimensionTypeId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }

        //Inter Dimension To Type Blacklist
        if(!intraDimensional && TeleporterBlockEntity.INTER_DIMENSIONAL_TO_TYPE_BLACKLIST.contains(toDimensionTypeId)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.blacklist.inter_dimensional_to_type",
                                    toDimensionTypeId.toString()).
                            formatted(Formatting.RED)
            ));

            return;
        }

        ///Do not check if chunk is loaded, chunk will be loaded by player after teleportation
        BlockEntity toBlockEntity = toDimension.getBlockEntity(toPos);
        if(!(toBlockEntity instanceof TeleporterBlockEntity)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter.use.teleporter_matrix_no_teleporter").
                            formatted(Formatting.RED)
            ));

            return;
        }

        clearEnergy();

        Vec3d toPosCenter = toPos.toCenterPos();

        player.teleport((ServerWorld)toDimension, toPosCenter.getX(), toPos.getY() + 1, toPosCenter.getZ(),
                new HashSet<>(), 0, 0);

        player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                RegistryEntry.of(SoundEvents.ENTITY_ENDERMAN_TELEPORT), SoundCategory.BLOCKS,
                toPosCenter.getX(), toPos.getY(), toPosCenter.getZ(), 1.f, 1.f,
                toDimension.getRandom().nextLong()));
    }

    public int getSlotCount() {
        return internalInventory.size();
    }

    public ItemStack getStack(int slot) {
        return internalInventory.getStack(slot);
    }

    public void clearEnergy() {
        try(Transaction transaction = Transaction.openOuter()) {
            internalEnergyStorage.extract(CAPACITY, transaction);

            transaction.commit();
        }
    }

    public long getEnergy() {
        return internalEnergyStorage.amount;
    }

    public long getCapacity() {
        return internalEnergyStorage.capacity;
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        internalEnergyStorage.capacity = capacity;
    }
}