package me.jddev0.ep.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record DimensionalPositionComponent(int x, int y, int z, ResourceLocation dimensionId) {
    public static final Codec<DimensionalPositionComponent> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("x").forGetter((component) -> {
            return component.x;
        }), Codec.INT.fieldOf("y").forGetter((component) -> {
            return component.y;
        }), Codec.INT.fieldOf("z").forGetter((component) -> {
            return component.z;
        }), ResourceLocation.CODEC.fieldOf("dimensionId").forGetter((component) -> {
            return component.dimensionId;
        })).apply(instance, DimensionalPositionComponent::new);
    });

    public static final StreamCodec<FriendlyByteBuf, DimensionalPositionComponent> STREAM_CODEC = StreamCodec.ofMember(
            DimensionalPositionComponent::write, DimensionalPositionComponent::new);

    public DimensionalPositionComponent(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readResourceLocation());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeResourceLocation(dimensionId);
    }
}
