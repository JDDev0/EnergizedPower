package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.TeleporterBlock;
import me.jddev0.ep.block.entity.base.InventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import me.jddev0.ep.screen.TeleporterMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TeleporterBlockEntity
        extends InventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleInventory>
        implements ExtendedScreenHandlerFactory {
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

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> true);

    public TeleporterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.TELEPORTER_ENTITY, blockPos, blockState,

                CAPACITY,
                ModConfigs.COMMON_TELEPORTER_TRANSFER_RATE.getValue(),

                1
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
            @Override
            protected void onFinalCommit() {
                setChangedAndUpdateReadyState();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0);
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
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.teleporter");
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(itemHandler);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);

        return new TeleporterMenu(id, this, inventory, itemHandler);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void setChangedAndUpdateReadyState() {
        boolean oldPowered = world.getBlockState(pos).contains(TeleporterBlock.POWERED) &&
                world.getBlockState(pos).get(TeleporterBlock.POWERED);

        ItemStack teleporterMatrixItemStack = itemHandler.getStack(0);

        boolean powered = energyStorage.getAmount() == energyStorage.getCapacity() &&
                teleporterMatrixItemStack.isOf(ModItems.TELEPORTER_MATRIX) &&
                TeleporterMatrixItem.isLinked(teleporterMatrixItemStack);

        if(oldPowered ^ powered)
            world.setBlockState(pos, getCachedState().with(TeleporterBlock.POWERED, powered), 3);

        markDirty();
    }

    public void onRedstoneTriggered() {
        if(Transaction.isOpen())
            return;

        Optional<PlayerEntity> player = world.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), Box.from(BlockBox.create(
                new Vec3i(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2),
                new Vec3i(pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2))), EntityPredicates.EXCEPT_SPECTATOR.
                        and(entity -> entity.squaredDistanceTo(new Vec3d(pos.getX() + .5,
                                pos.getY() - .5, pos.getZ() + .5)) <= 4)).stream().
                min(Comparator.comparing(entity -> entity.squaredDistanceTo(new Vec3d(pos.getX() + .5,
                        pos.getY() - .5, pos.getZ() + .5))));

        if(player.isEmpty())
            return;

        if(!(player.get() instanceof ServerPlayerEntity serverPlayer))
            return;

        teleportPlayer(serverPlayer);
    }

    public void teleportPlayer(ServerPlayerEntity player) {
        EnergyStorage limitingEnergyStorage = EnergyStorage.SIDED.find(player.getWorld(), pos, null);
        if(limitingEnergyStorage == null)
            return;

        if(limitingEnergyStorage.getAmount() < TeleporterBlockEntity.CAPACITY) {
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

        Vec3d toPosCenter = new Vec3d(toPos.getX() + .5, toPos.getY() - .5, toPos.getZ() + .5);

        player.teleport((ServerWorld)toDimension, toPosCenter.getX(), toPos.getY() + 1, toPosCenter.getZ(),
                0, 0);

        player.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.BLOCKS, toPosCenter.getX(), toPos.getY(), toPosCenter.getZ(), 1.f, 1.f,
                toDimension.getRandom().nextLong()));
    }

    public int getSlotCount() {
        return itemHandler.size();
    }

    public ItemStack getStack(int slot) {
        return itemHandler.getStack(slot);
    }

    public void clearEnergy() {
        try(Transaction transaction = Transaction.openOuter()) {
            energyStorage.extract(CAPACITY, transaction);

            transaction.commit();
        }
    }
}