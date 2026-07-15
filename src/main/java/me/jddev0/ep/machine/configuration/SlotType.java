package me.jddev0.ep.machine.configuration;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum SlotType implements StringRepresentable {
    ITEM, FLUID;

    public static final Codec<SlotType> CODEC = ExtraCodecs.orCompressed(
            Codec.stringResolver(SlotType::name, SlotType::valueOf),
            ExtraCodecs.idResolverCodec(SlotType::ordinal, i -> i >= 0 && i < SlotType.values().length?SlotType.values()[i]:null, -1)
    );

    /**
     * @return Returns the enum value at index if index is valid otherwise ITEM will be returned
     */
    public static @NotNull SlotType fromIndex(int index) {
        SlotType[] values = values();

        if(index < 0 || index >= values.length)
            return ITEM;

        return values[index];
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.US);
    }
}
