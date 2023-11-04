package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.item.TeleporterMatrixItem;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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

            //TODO add blacklist checks

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

            Vec3d toPosCenter = new Vec3d(toPos.getX() + .5, toPos.getY() - .5, toPos.getZ() + .5);

            player.teleport((ServerWorld)toDimension, toPosCenter.getX(), toPos.getY() + 1, toPosCenter.getZ(),
                    0, 0);
        });
    }
}
