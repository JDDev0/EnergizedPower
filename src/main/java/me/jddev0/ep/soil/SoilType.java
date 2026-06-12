package me.jddev0.ep.soil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

public record SoilType(Component displayName) {
    public static final Codec<SoilType> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(ComponentSerialization.CODEC.fieldOf("display_name").forGetter(soilType -> {
            return soilType.displayName;
        })).apply(instance, SoilType::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, SoilType> STREAM_CODEC = StreamCodec.ofMember(SoilType::write, SoilType::new);

    private SoilType(RegistryFriendlyByteBuf buffer) {
        this(ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buffer));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buffer, displayName);
    }
}
