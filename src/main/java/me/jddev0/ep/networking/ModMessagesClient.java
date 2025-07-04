package me.jddev0.ep.networking;

import me.jddev0.ep.networking.packet.*;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

public final class ModMessagesClient {
    private ModMessagesClient() {}

    public static void register(final RegisterClientPayloadHandlersEvent event) {
        //Server -> Client
        event.register(EnergySyncS2CPacket.ID, EnergySyncS2CPacket::handle);
        event.register(FluidSyncS2CPacket.ID, FluidSyncS2CPacket::handle);
        event.register(ItemStackSyncS2CPacket.ID, ItemStackSyncS2CPacket::handle);
        event.register(OpenEnergizedPowerBookS2CPacket.ID, OpenEnergizedPowerBookS2CPacket::handle);
        event.register(SyncPressMoldMakerRecipeListS2CPacket.ID, SyncPressMoldMakerRecipeListS2CPacket::handle);
        event.register(SyncCurrentRecipeS2CPacket.ID, SyncCurrentRecipeS2CPacket::handle);
        event.register(SyncIngredientsS2CPacket.ID, SyncIngredientsS2CPacket::handle);
    }
}
