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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.function.Supplier;

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

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().getLevel();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
                return;

            ServerPlayer player = context.getSender();
            if(player == null)
                return;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(!(blockEntity instanceof TeleporterBlockEntity teleporterBlockEntity))
                return;

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = teleporterBlockEntity.getCapability(ForgeCapabilities.ENERGY, null);
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

            //TODO add blacklist checks

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

            Vec3 toPosCenter = new Vec3(toPos.getX() + .5, toPos.getY() - .5, toPos.getZ() + .5);

            context.getSender().teleportTo((ServerLevel)toDimension, toPosCenter.x(), toPos.getY() + 1, toPosCenter.z(),
                    0, 0);
        });

        return true;
    }
}
