package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.TeleporterBlock;
import me.jddev0.ep.block.entity.base.MenuInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import me.jddev0.ep.screen.TeleporterMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class TeleporterBlockEntity
        extends MenuInventoryEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage, ItemStackHandler> {
    public static final boolean INTRA_DIMENSIONAL_ENABLED = ModConfigs.COMMON_TELEPORTER_INTRA_DIMENSIONAL_ENABLED.getValue();
    public static final boolean INTER_DIMENSIONAL_ENABLED = ModConfigs.COMMON_TELEPORTER_INTER_DIMENSIONAL_ENABLED.getValue();
    public static final List<@NotNull ResourceLocation> DIMENSION_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_DIMENSION_BLACKLIST.getValue();
    public static final List<@NotNull ResourceLocation> INTRA_DIMENSIONAL_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTRA_DIMENSIONAL_BLACKLIST.getValue();
    public static final List<@NotNull ResourceLocation> INTER_DIMENSIONAL_FROM_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTER_DIMENSIONAL_FROM_BLACKLIST.getValue();
    public static final List<@NotNull ResourceLocation> INTER_DIMENSIONAL_TO_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTER_DIMENSIONAL_TO_BLACKLIST.getValue();
    public static final List<@NotNull ResourceLocation> DIMENSION_TYPE_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_DIMENSION_TYPE_BLACKLIST.getValue();
    public static final List<@NotNull ResourceLocation> INTRA_DIMENSIONAL_TYPE_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTRA_DIMENSIONAL_TYPE_BLACKLIST.getValue();
    public static final List<@NotNull ResourceLocation> INTER_DIMENSIONAL_FROM_TYPE_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTER_DIMENSIONAL_FROM_TYPE_BLACKLIST.getValue();
    public static final List<@NotNull ResourceLocation> INTER_DIMENSIONAL_TO_TYPE_BLACKLIST =
            ModConfigs.COMMON_TELEPORTER_INTER_DIMENSIONAL_TO_TYPE_BLACKLIST.getValue();

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> true));

    public TeleporterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.TELEPORTER_ENTITY.get(), blockPos, blockState,

                "teleporter",

                ModConfigs.COMMON_TELEPORTER_CAPACITY.getValue(),
                ModConfigs.COMMON_TELEPORTER_TRANSFER_RATE.getValue(),

                1
       );
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            protected void onChange() {
                setChangedAndUpdateReadyState();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChangedAndUpdateReadyState();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    return stack.is(EPItems.TELEPORTER_MATRIX.get());
                }

                return super.isItemValid(slot, stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new TeleporterMenu(id, inventory, this);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    public void setChangedAndUpdateReadyState() {
        boolean oldPowered = level.getBlockState(worldPosition).hasProperty(TeleporterBlock.POWERED) &&
                level.getBlockState(worldPosition).getValue(TeleporterBlock.POWERED);

        ItemStack teleporterMatrixItemStack = itemHandler.getStackInSlot(0);

        boolean powered = energyStorage.getEnergy() == energyStorage.getCapacity() &&
                teleporterMatrixItemStack.is(EPItems.TELEPORTER_MATRIX.get()) &&
                TeleporterMatrixItem.isLinked(teleporterMatrixItemStack);

        if(oldPowered ^ powered)
            level.setBlock(worldPosition, getBlockState().setValue(TeleporterBlock.POWERED, powered), 3);

        setChanged();
    }

    public void onRedstoneTriggered() {
        Optional<Player> player = level.getEntities(EntityTypeTest.forClass(Player.class), AABB.of(BoundingBox.fromCorners(
                new Vec3i(worldPosition.getX() - 2, worldPosition.getY() - 2,
                        worldPosition.getZ() - 2),
                new Vec3i(worldPosition.getX() + 2, worldPosition.getY() + 2,
                        worldPosition.getZ() + 2))), EntitySelector.NO_SPECTATORS.
                and(entity -> entity.distanceToSqr(worldPosition.getCenter()) <= 4)).stream().
                min(Comparator.comparing(entity -> entity.distanceToSqr(worldPosition.getCenter())));

        if(player.isEmpty())
            return;

        if(!(player.get() instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel serverLevel))
            return;

        serverLevel.getServer().submitAsync(() -> teleportPlayer(serverPlayer));
    }

    public void teleportPlayer(ServerPlayer player) {
        LazyOptional<IEnergyStorage> energyStorageLazyOptional = getCapability(ForgeCapabilities.ENERGY, null);
        if(!energyStorageLazyOptional.isPresent())
            return;

        IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);

        teleportPlayer(player, energyStorage, () -> this.energyStorage.setEnergy(0), getStack(0), level, worldPosition);
    }

    public static void teleportPlayer(ServerPlayer player, IEnergyStorage energyStorage, Runnable clearEnergyCallback,
                                      ItemStack teleporterMatrixItemStack, Level level, @Nullable BlockPos pos) {
        if(player.isSpectator()) {
            return;
        }

        if(energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.not_enough_energy").
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        if(!teleporterMatrixItemStack.is(EPItems.TELEPORTER_MATRIX.get())) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.no_teleporter_matrix").
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        if(!TeleporterMatrixItem.isLinked(teleporterMatrixItemStack)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.teleporter_matrix_not_bound").
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        BlockPos toPos = TeleporterMatrixItem.getBlockPos(level, teleporterMatrixItemStack);
        if(toPos == null) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.teleporter_matrix_invalid_position").
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        Level toDimension = TeleporterMatrixItem.getDimension(level, teleporterMatrixItemStack);
        if(!(toDimension instanceof ServerLevel)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.teleporter_matrix_invalid_dimension").
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        if(pos != null && pos.equals(toPos) && level.dimension().equals(toDimension.dimension())) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.teleporter_self_position").
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        ResourceLocation fromDimensionId = level.dimension().location();
        ResourceLocation toDimensionId = toDimension.dimension().location();

        boolean intraDimensional = fromDimensionId.equals(toDimensionId);

        //Intra dimensional enabled
        if(intraDimensional && !TeleporterBlockEntity.INTRA_DIMENSIONAL_ENABLED) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.intra_dimensional_disabled",
                                    fromDimensionId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        //Inter dimensional enabled
        if(!intraDimensional && !TeleporterBlockEntity.INTER_DIMENSIONAL_ENABLED) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.inter_dimensional_disabled",
                                    fromDimensionId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        //Dimension Blacklist
        if(TeleporterBlockEntity.DIMENSION_BLACKLIST.contains(fromDimensionId)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.blacklist.dimension",
                                    fromDimensionId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }
        if(TeleporterBlockEntity.DIMENSION_BLACKLIST.contains(toDimensionId)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.blacklist.dimension",
                                    toDimensionId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        //Intra Dimension Blacklist
        if(intraDimensional && TeleporterBlockEntity.INTRA_DIMENSIONAL_BLACKLIST.contains(fromDimensionId)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.blacklist.intra_dimensional",
                                    fromDimensionId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        //Inter Dimension From Blacklist
        if(!intraDimensional && TeleporterBlockEntity.INTER_DIMENSIONAL_FROM_BLACKLIST.contains(fromDimensionId)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.blacklist.inter_dimensional_from",
                                    fromDimensionId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        //Inter Dimension To Blacklist
        if(!intraDimensional && TeleporterBlockEntity.INTER_DIMENSIONAL_TO_BLACKLIST.contains(toDimensionId)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.blacklist.inter_dimensional_to",
                                    toDimensionId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        ResourceLocation fromDimensionTypeId = level.dimensionTypeId().location();
        ResourceLocation toDimensionTypeId = toDimension.dimensionTypeId().location();

        //Dimension Type Blacklist
        if(TeleporterBlockEntity.DIMENSION_TYPE_BLACKLIST.contains(fromDimensionTypeId)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.blacklist.dimension_type",
                                    fromDimensionTypeId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }
        if(TeleporterBlockEntity.DIMENSION_TYPE_BLACKLIST.contains(toDimensionTypeId)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.blacklist.dimension_type",
                                    toDimensionTypeId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        //Intra Dimension Type Blacklist
        if(intraDimensional && TeleporterBlockEntity.INTRA_DIMENSIONAL_TYPE_BLACKLIST.contains(fromDimensionTypeId)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.blacklist.intra_dimensional_type",
                                    fromDimensionTypeId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        //Inter Dimension From Type Blacklist
        if(!intraDimensional && TeleporterBlockEntity.INTER_DIMENSIONAL_FROM_TYPE_BLACKLIST.contains(fromDimensionTypeId)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.blacklist.inter_dimensional_from_type",
                                    fromDimensionTypeId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        //Inter Dimension To Type Blacklist
        if(!intraDimensional && TeleporterBlockEntity.INTER_DIMENSIONAL_TO_TYPE_BLACKLIST.contains(toDimensionTypeId)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.blacklist.inter_dimensional_to_type",
                                    toDimensionTypeId.toString()).
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        ///Do not check if chunk is loaded, chunk will be loaded by player after teleportation
        BlockEntity toBlockEntity = toDimension.getBlockEntity(toPos);
        if(!(toBlockEntity instanceof TeleporterBlockEntity)) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.teleporter_matrix_no_teleporter").
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        clearEnergyCallback.run();

        Vec3 toPosCenter = toPos.getCenter();

        player.teleportTo((ServerLevel)toDimension, toPosCenter.x(), toPos.getY() + 1, toPosCenter.z(),
                new HashSet<>(), 0, 0);

        player.connection.send(new ClientboundSoundPacket(
                Holder.direct(SoundEvents.ENDERMAN_TELEPORT), SoundSource.BLOCKS,
                toPosCenter.x(), toPos.getY(), toPosCenter.z(), 1.f, 1.f,
                toDimension.getRandom().nextLong()));
    }

    public int getSlotCount() {
        return itemHandler.getSlots();
    }

    public ItemStack getStack(int slot) {
        return itemHandler.getStackInSlot(slot);
    }
}