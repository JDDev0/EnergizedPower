package me.jddev0.ep.machine.configuration;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ComparatorMode implements StringRepresentable {
    ITEM, FLUID, ENERGY;

    public static final Codec<ComparatorMode> CODEC = ExtraCodecs.orCompressed(
            Codec.stringResolver(ComparatorMode::name, ComparatorMode::valueOf),
            ExtraCodecs.idResolverCodec(ComparatorMode::ordinal, i -> i >= 0 && i < ComparatorMode.values().length?ComparatorMode.values()[i]:null, -1)
    );

    /**
     * @return Returns the enum value at index if index is valid otherwise ITEM will be returned
     */
    public static @NotNull ComparatorMode fromIndex(int index) {
        ComparatorMode[] values = values();

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
