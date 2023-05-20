package me.jddev0.ep.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;

public final class ModTrackedDataHandlers {
    public static final TrackedDataHandler<Long> LONG = TrackedDataHandler.of(PacketByteBuf::writeVarLong, PacketByteBuf::readVarLong);

    private ModTrackedDataHandlers() {}

    public static void register() {
        TrackedDataHandlerRegistry.register(LONG);
    }
}
