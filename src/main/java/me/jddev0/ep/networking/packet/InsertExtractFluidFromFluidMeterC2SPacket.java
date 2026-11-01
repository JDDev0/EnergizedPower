package me.jddev0.ep.networking.packet;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.util.CapabilityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
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

    public static void handle(InsertExtractFluidFromFluidMeterC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof FluidStoragePacketUpdate)) //Check is only required to limit this packet to EP fluid machines
                return;

            ItemStack fluidItemStack = player.containerMenu.getCarried();
            ResourceHandler<FluidResource> fluidStorageItem = fluidItemStack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forPlayerCursor(player, player.containerMenu));
            if(fluidStorageItem == null || fluidStorageItem.size() == 0) {
                return;
            }

            ResourceHandler<FluidResource> fluidStorageSelf = level.getCapability(Capabilities.Fluid.BLOCK, data.pos, level.getBlockState(data.pos), blockEntity, null);
            if(fluidStorageSelf == null || fluidStorageSelf.size() < data.tank) {
                LOGGER.warn("Invalid block entity: Fluid Storage expected with at least {} tanks", data.tank);

                return;
            }

            if(data.insertExtract) {
                //Insert fluid into machine from item

                FluidStack fluidStackSelf = fluidStorageSelf.getResource(data.tank).toStack(1000);
                for(int i = 0;i < fluidStorageItem.size();i++) {
                    FluidResource fluidResourceItem = fluidStorageItem.getResource(i);
                    if(fluidResourceItem.isEmpty())
                        continue;

                    if(!fluidStackSelf.isEmpty() && !fluidResourceItem.matches(fluidStackSelf))
                        continue;

                    int extracted;
                    try(Transaction transaction = Transaction.openRoot()) {
                        extracted = fluidStorageItem.extract(fluidResourceItem, Integer.MAX_VALUE, transaction);
                    }

                    if(extracted <= 0)
                        continue;

                    try(Transaction transaction = Transaction.openRoot()) {
                        int inserted = fluidStorageSelf.insert(data.tank, fluidResourceItem, extracted, transaction);
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

                FluidResource fluidResourceSelf = fluidStorageSelf.getResource(data.tank);
                if(fluidResourceSelf.isEmpty())
                    return;

                int extracted;
                try(Transaction transaction = Transaction.openRoot()) {
                    extracted = fluidStorageSelf.extract(data.tank, fluidResourceSelf, Integer.MAX_VALUE, transaction);
                }

                if(extracted <= 0)
                    return;

                try(Transaction transaction = Transaction.openRoot()) {
                    int inserted = fluidStorageItem.insert(fluidResourceSelf, extracted, transaction);
                    if(inserted > 0) {
                        extracted = fluidStorageSelf.extract(data.tank, fluidResourceSelf, inserted, transaction);

                        if(inserted == extracted)
                            transaction.commit();
                    }
                }
            }
        });
    }
}
