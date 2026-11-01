package me.jddev0.ep.networking.packet;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public record InsertExtractFluidFromFluidMeterC2SPacket(BlockPos pos, int tank, boolean insertExtract) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<InsertExtractFluidFromFluidMeterC2SPacket> ID =
            new Type<>(EPAPI.id("insert_extract_fluid_from_fluid_meter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InsertExtractFluidFromFluidMeterC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(InsertExtractFluidFromFluidMeterC2SPacket::write, InsertExtractFluidFromFluidMeterC2SPacket::new);

    public InsertExtractFluidFromFluidMeterC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readBoolean());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(tank);
        buffer.writeBoolean(insertExtract);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(InsertExtractFluidFromFluidMeterC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof FluidStoragePacketUpdate)) //Check is only required to limit this packet to EP fluid machines
                return;

            ItemStack fluidItemStack = context.player().containerMenu.getCarried();
            Storage<FluidVariant> fluidStorageItem = FluidStorage.ITEM.find(fluidItemStack, ContainerItemContext.ofPlayerCursor(context.player(), context.player().containerMenu));
            if(fluidStorageItem == null) {
                return;
            }

            Storage<FluidVariant> fluidStorageSelfRaw = FluidStorage.SIDED.find(level, data.pos, level.getBlockState(data.pos), blockEntity, null);
            if(!(fluidStorageSelfRaw instanceof EnergizedPowerFluidStorage fluidStorageSelf) || fluidStorageSelf.size() < data.tank) {
                LOGGER.warn("Invalid block entity: Fluid Storage expected with at least {} tanks", data.tank);

                return;
            }

            if(data.insertExtract) {
                //Insert fluid into machine from item

                FluidVariant fluidStackSelf = fluidStorageSelf.getResource(data.tank);
                for(StorageView<FluidVariant> fluidView:fluidStorageItem) {
                    FluidVariant fluidResourceItem = fluidView.getResource();
                    if(fluidResourceItem.isBlank())
                        continue;

                    if(!fluidStackSelf.isBlank() && !fluidResourceItem.equals(fluidStackSelf))
                        continue;

                    long extracted;
                    try(Transaction transaction = Transaction.openOuter()) {
                        extracted = fluidStorageItem.extract(fluidResourceItem, Long.MAX_VALUE, transaction);
                    }

                    if(extracted <= 0)
                        continue;

                    try(Transaction transaction = Transaction.openOuter()) {
                        long inserted = fluidStorageSelf.getSlot(data.tank).insert(fluidResourceItem, extracted, transaction);
                        if(inserted > 0) {
                            extracted = fluidStorageItem.extract(fluidResourceItem, inserted, transaction);

                            if(inserted == extracted) {
                                transaction.commit();

                                //Do not try to insert more fluids
                                return;
                            }
                        }
                    }
                }
            }else {
                //Extract fluid from machine into item

                FluidVariant fluidResourceSelf = fluidStorageSelf.getResource(data.tank);
                if(fluidResourceSelf.isBlank())
                    return;

                long extracted;
                try(Transaction transaction = Transaction.openOuter()) {
                    extracted = fluidStorageSelf.getSlot(data.tank).extract(fluidResourceSelf, Long.MAX_VALUE, transaction);
                }

                if(extracted <= 0)
                    return;

                try(Transaction transaction = Transaction.openOuter()) {
                    long inserted = fluidStorageItem.insert(fluidResourceSelf, extracted, transaction);
                    if(inserted > 0) {
                        extracted = fluidStorageSelf.getSlot(data.tank).extract(fluidResourceSelf, inserted, transaction);

                        if(inserted == extracted)
                            transaction.commit();
                    }
                }
            }
        });
    }
}
