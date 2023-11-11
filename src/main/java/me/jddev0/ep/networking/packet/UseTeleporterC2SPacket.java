package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

import java.util.HashSet;

public class UseTeleporterC2SPacket {
    private UseTeleporterC2SPacket() {}

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                           PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();

        server.execute(() -> {
            World level = player.getWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof TeleporterBlockEntity teleporterBlockEntity))
                return;

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

            ItemStack teleporterMatrixItemStack = teleporterBlockEntity.getStack(0);
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

            BlockPos toPos = TeleporterMatrixItem.getBlockPos(level, teleporterMatrixItemStack);
            if(toPos == null) {
                player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.teleporter.use.teleporter_matrix_invalid_position").
                                formatted(Formatting.RED)
                ));

                return;
            }

            World toDimension = TeleporterMatrixItem.getDimension(level, teleporterMatrixItemStack);
            if(!(toDimension instanceof ServerWorld)) {
                player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.teleporter.use.teleporter_matrix_invalid_dimension").
                                formatted(Formatting.RED)
                ));

                return;
            }

            if(pos.equals(toPos) && level.getRegistryKey().equals(toDimension.getRegistryKey())) {
                player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.teleporter.use.teleporter_self_position").
                                formatted(Formatting.RED)
                ));

                return;
            }

            Identifier fromDimensionId = level.getRegistryKey().getValue();
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

            Identifier fromDimensionTypeId = level.getDimensionKey().getValue();
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

            teleporterBlockEntity.clearEnergy();

            Vec3d toPosCenter = toPos.toCenterPos();

            player.teleport((ServerWorld)toDimension, toPosCenter.getX(), toPos.getY() + 1, toPosCenter.getZ(),
                    new HashSet<>(), 0, 0);

            player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                    RegistryEntry.of(SoundEvents.ENTITY_ENDERMAN_TELEPORT), SoundCategory.BLOCKS,
                    toPosCenter.getX(), toPos.getY(), toPosCenter.getZ(), 1.f, 1.f,
                    toDimension.getRandom().nextLong()));
        });
    }
}
