package me.jddev0.ep.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class FluidIngredient {
     public static final Codec<FluidIngredient> CODEC = CodecFix.listOrSingleValueOrSingleTagKeyCodec(BuiltInRegistries.FLUID.byNameCodec(), BuiltInRegistries.FLUID.key()).
             xmap(value -> new FluidIngredient(value.mapBoth(
                     fluids -> fluids.map(fluidList -> fluidList, Collections::singletonList),
                     fluidTag -> fluidTag
             )), ingredient -> ingredient.fluid.mapBoth(
                     fluids -> fluids.size() == 1?Either.right(fluids.getFirst()):Either.left(fluids), fluid -> fluid)
             );

    private static final StreamCodec<ByteBuf, TagKey<Fluid>> FLUID_TAG_KEY_STREAM_CODEC = TagKey.streamCodec(BuiltInRegistries.FLUID.key());

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidIngredient> STREAM_CODEC = StreamCodec.of((buffer, fluidIngredient) -> {
        buffer.writeBoolean(fluidIngredient.fluid.left().isPresent());
        fluidIngredient.fluid.map(
                fluids -> {
                    buffer.writeInt(fluids.size());
                    for(Fluid fluid:fluids) {
                        Identifier fluidId = BuiltInRegistries.FLUID.getKey(fluid);
                        if(fluidId == null || fluidId.equals(Identifier.parse("empty")))
                            throw new IllegalArgumentException("Unregistered fluid '" + fluid + "'");

                        buffer.writeIdentifier(fluidId);
                    }

                    return null;
                },
                fluid -> {
                    FLUID_TAG_KEY_STREAM_CODEC.encode(buffer, fluid);

                    return null;
                }
        );
    }, buffer -> {
        if(buffer.readBoolean()) {
            int fluidCount = buffer.readInt();
            List<Fluid> fluids = new ArrayList<>(fluidCount);

            for(int i = 0;i < fluidCount;i++)
                fluids.add(BuiltInRegistries.FLUID.getValue(buffer.readIdentifier()));

            return new FluidIngredient(Either.left(fluids));
        }else {
            return new FluidIngredient(Either.right(FLUID_TAG_KEY_STREAM_CODEC.decode(buffer)));
        }
    });

    public static FluidIngredient of(Fluid fluid) {
        return of(Collections.singletonList(fluid));
    }

    public static FluidIngredient of(Fluid[] fluid) {
        return of(Arrays.asList(fluid));
    }

    public static FluidIngredient of(List<Fluid> fluid) {
        return of(Either.left(fluid));
    }

    public static FluidIngredient of(TagKey<Fluid> fluid) {
        return of(Either.right(fluid));
    }

    public static FluidIngredient of(Either<List<Fluid>, TagKey<Fluid>> fluid) {
        return new FluidIngredient(fluid);
    }

    private final Either<List<Fluid>, TagKey<Fluid>> fluid;

    private FluidIngredient(Either<List<Fluid>, TagKey<Fluid>> fluid) {
        this.fluid = fluid;
    }

    public Either<List<Fluid>, TagKey<Fluid>> getFluid() {
        return fluid;
    }

    public boolean test(FluidStack fluid) {
        return this.fluid.map(
                fluids -> fluids.stream().anyMatch(f -> fluid.is(f)),
                fluidTag -> fluid.is(fluidTag)
        );
    }

    public boolean test(FluidResource fluid) {
        return this.fluid.map(
                fluids -> fluids.stream().anyMatch(f -> fluid.is(f)),
                fluidTag -> fluid.is(fluidTag)
        );
    }

    public boolean test(FluidState fluid) {
        return this.fluid.map(
                fluids -> fluids.stream().anyMatch(f -> fluid.is(f)),
                fluidTag -> fluid.is(fluidTag)
        );
    }
}
