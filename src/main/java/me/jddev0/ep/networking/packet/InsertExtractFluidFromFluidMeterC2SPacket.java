package me.jddev0.ep.networking.packet;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
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
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.network.handling.IPayloadContext;
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

            ItemStack fluidItemStack = player.containerMenu.getCarried().copy();
            IFluidHandlerItem fluidStorageItem = fluidItemStack.getCapability(Capabilities.FluidHandler.ITEM);
            if(fluidStorageItem == null || fluidStorageItem.getTanks() == 0) {
                return;
            }

            IFluidHandler fluidStorageSelfRaw = level.getCapability(Capabilities.FluidHandler.BLOCK, data.pos, level.getBlockState(data.pos), blockEntity, null);
            /*
             * In NeoForge 1.21.1, fluid storages cannot be limited to filter certain tanks.
             * This method must modify the internal data of the handler directly in order to function correctly (=> Only EnergizedPowerFluidStorage is supported).
             */
            if(!(fluidStorageSelfRaw instanceof EnergizedPowerFluidStorage fluidStorageSelf) || fluidStorageSelf.size() < data.tank) {
                LOGGER.warn("Invalid block entity: Fluid Storage expected with at least {} tanks", data.tank);

                return;
            }

            if(data.insertExtract) {
                //Insert fluid into machine from item

                FluidStack fluidStackSelf = fluidStorageSelf.fluidStacks.get(data.tank);
                for(int i = 0;i < fluidStorageItem.getTanks();i++) {
                    FluidStack fluidResourceItem = fluidStorageItem.getFluidInTank(i);
                    if(fluidResourceItem.isEmpty())
                        continue;

                    if(!fluidStackSelf.isEmpty() && !FluidStack.isSameFluidSameComponents(fluidResourceItem, fluidStackSelf))
                        continue;

                    //Fluid must be validated here, because internal tank state is directly modified
                    if(!fluidStorageSelf.isFluidValid(data.tank, fluidResourceItem))
                        continue;

                    int maxInsert = fluidStorageSelf.getTankCapacity(data.tank) - fluidStackSelf.getAmount();
                    FluidStack extracted = fluidStorageItem.drain(fluidResourceItem.copyWithAmount(maxInsert), IFluidHandler.FluidAction.EXECUTE);
                    if(extracted.isEmpty())
                        continue;

                    int inserted = extracted.getAmount();
                    fluidStorageSelf.fluidStacks.set(data.tank, fluidResourceItem.copyWithAmount(fluidStackSelf.getAmount() + inserted));
                    fluidStorageSelf._triggerFinalCommitFromInsertExtractFluidFromFluidMeterC2SPacket();

                    player.containerMenu.setCarried(fluidStorageItem.getContainer());
                    player.containerMenu.broadcastChanges();

                    //Do not try to insert more fluids
                    return;
                }
            }else {
                //Extract fluid from machine into item

                FluidStack fluidResourceSelf = fluidStorageSelf.fluidStacks.get(data.tank);
                if(fluidResourceSelf.isEmpty())
                    return;

                int inserted = fluidStorageItem.fill(fluidResourceSelf.copy(), IFluidHandler.FluidAction.EXECUTE);
                if(inserted > 0) {
                    fluidStorageSelf.fluidStacks.set(data.tank, fluidResourceSelf.copyWithAmount(fluidResourceSelf.getAmount() - inserted));
                    fluidStorageSelf._triggerFinalCommitFromInsertExtractFluidFromFluidMeterC2SPacket();

                    player.containerMenu.setCarried(fluidStorageItem.getContainer());
                    player.containerMenu.broadcastChanges();
                }
            }
        });
    }
}
