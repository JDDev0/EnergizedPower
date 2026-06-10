package me.jddev0.ep.soil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.registry.EPRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;

public record SoilType(Component displayName) {
    public static final Codec<SoilType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(ComponentSerialization.CODEC.fieldOf("display_name").forGetter(soilType -> {
            return soilType.displayName;
        })).apply(instance, SoilType::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, SoilType> DIRECT_STREAM_CODEC = StreamCodec.ofMember(SoilType::write, SoilType::new);

    public static final Codec<Holder<SoilType>> CODEC = RegistryFixedCodec.create(EPRegistries.SOIL_TYPE);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SoilType>> STREAM_CODEC = ByteBufCodecs.holder(
            EPRegistries.SOIL_TYPE, DIRECT_STREAM_CODEC
    );

    private SoilType(RegistryFriendlyByteBuf buffer) {
        this(ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buffer));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buffer, displayName);
    }
}
