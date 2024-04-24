package me.jddev0.ep.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

public final class PacketCodecFix {
    private PacketCodecFix() {}

    public static final PacketCodec<ByteBuf, Long> LONG = new PacketCodec<>() {
        public Long decode(ByteBuf byteBuf) {
            return byteBuf.readLong();
        }

        public void encode(ByteBuf byteBuf, Long value) {
            byteBuf.writeLong(value);
        }
    };
}
