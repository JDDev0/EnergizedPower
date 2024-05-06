package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.TeleporterBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.screen.TeleporterMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class TeleporterBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate {
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

    public static final int CAPACITY = ModConfigs.COMMON_TELEPORTER_CAPACITY.getValue();

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChangedAndUpdateReadyState();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if(slot == 0) {
                return stack.is(ModItems.TELEPORTER_MATRIX.get());
            }

            return super.isItemValid(slot, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> true);

    private final ReceiveOnlyEnergyStorage energyStorage;


    public TeleporterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.TELEPORTER_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, CAPACITY,
                ModConfigs.COMMON_TELEPORTER_TRANSFER_RATE.getValue()) {
            @Override
            protected void onChange() {
                setChangedAndUpdateReadyState();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToPlayersWithinXBlocks(
                            new EnergySyncS2CPacket(getEnergy(), getCapacity(), getBlockPos()),
                            getBlockPos(), (ServerLevel)level, 32
                    );
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.teleporter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(),
                getBlockPos()), (ServerPlayer)player);

        return new TeleporterMenu(id, inventory, this);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
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
    protected void saveAdditional(CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        nbt.put("inventory", itemHandler.serializeNBT(registries));
        nbt.put("energy", energyStorage.saveNBT());

        super.saveAdditional(nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        itemHandler.deserializeNBT(registries, nbt.getCompound("inventory"));
        energyStorage.loadNBT(nbt.get("energy"));
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
    }

    public void setChangedAndUpdateReadyState() {
        boolean oldPowered = level.getBlockState(worldPosition).hasProperty(TeleporterBlock.POWERED) &&
                level.getBlockState(worldPosition).getValue(TeleporterBlock.POWERED);

        ItemStack teleporterMatrixItemStack = itemHandler.getStackInSlot(0);

        boolean powered = energyStorage.getEnergy() == energyStorage.getCapacity() &&
                teleporterMatrixItemStack.is(ModItems.TELEPORTER_MATRIX.get()) &&
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

        if(!(player.get() instanceof ServerPlayer serverPlayer))
            return;

        teleportPlayer(serverPlayer);
    }

    public void teleportPlayer(ServerPlayer player) {
        IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos(),
                getBlockState(), this, null);
        if(energyStorage == null)
            return;

        if(energyStorage.getEnergyStored() < TeleporterBlockEntity.CAPACITY) {
            player.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter.use.not_enough_energy").
                            withStyle(ChatFormatting.RED)
            ));

            return;
        }

        ItemStack teleporterMatrixItemStack = getStack(0);
        if(!teleporterMatrixItemStack.is(ModItems.TELEPORTER_MATRIX.get())) {
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

        if(worldPosition.equals(toPos) && level.dimension().equals(toDimension.dimension())) {
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

        ResourceLocation fromDimensionTypeId = level.dimensionTypeRegistration().unwrapKey().
                map(ResourceKey::location).orElse(new ResourceLocation("empty"));
        ResourceLocation toDimensionTypeId = toDimension.dimensionTypeRegistration().unwrapKey().
                map(ResourceKey::location).orElse(new ResourceLocation("empty"));

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

        clearEnergy();

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

    public void clearEnergy() {
        energyStorage.setEnergy(0);
    }

    public int getEnergy() {
        return energyStorage.getEnergy();
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }
}