package me.jddev0.ep.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;

public final class ModTrackedDataHandlers {
    public static final TrackedDataHandler<Long> LONG = new TrackedDataHandler<>() {
        public void write(PacketByteBuf buf, Long longValue) {
            buf.writeVarLong(longValue);
        }

        public Long read(PacketByteBuf buf) {
            return buf.readVarLong();
        }

        public Long copy(Long longValue) {
            return longValue;
        }
    };

    private ModTrackedDataHandlers() {}

    public static void register() {
        TrackedDataHandlerRegistry.register(LONG);
    }
}
