package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.HashSet;

public class UseTeleporterC2SPacket {
    private final BlockPos pos;

    public UseTeleporterC2SPacket(BlockPos pos) {
        this.pos = pos;
    }

    public UseTeleporterC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            ServerPlayer player = context.getSender();
            if(player == null)
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof TeleporterBlockEntity teleporterBlockEntity))
                return;

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = teleporterBlockEntity.getCapability(Capabilities.ENERGY, null);
            if(!energyStorageLazyOptional.isPresent())
                return;

            IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
            if(energyStorage.getEnergyStored() < TeleporterBlockEntity.CAPACITY) {
                player.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.teleporter.use.not_enough_energy").
                                withStyle(ChatFormatting.RED)
                ));

                return;
            }

            ItemStack teleporterMatrixItemStack = teleporterBlockEntity.getStack(0);
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

            if(pos.equals(toPos) && level.dimension().equals(toDimension.dimension())) {
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

            teleporterBlockEntity.clearEnergy();

            Vec3 toPosCenter = toPos.getCenter();

            context.getSender().teleportTo((ServerLevel)toDimension, toPosCenter.x(), toPos.getY() + 1, toPosCenter.z(),
                    new HashSet<>(), 0, 0);
        });

        return true;
    }
}
