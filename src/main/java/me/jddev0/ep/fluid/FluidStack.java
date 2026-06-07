package me.jddev0.ep.fluid;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.codec.PacketCodecFix;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.slf4j.Logger;

public class FluidStack {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<FluidStack> CODEC_MILLIBUCKETS = RecordCodecBuilder.create(instance -> {
        return instance.group(ResourceLocation.CODEC.fieldOf("id").forGetter(fluidStack -> {
            return BuiltInRegistries.FLUID.getKey(fluidStack.fluidVariant.getFluid());
        }),CodecFix.NON_NEGATIVE_LONG.fieldOf("amount").forGetter(fluidStack -> {
            return FluidUtils.convertDropletsToMilliBuckets(fluidStack.dropletsAmount);
        }), CodecFix.NON_NEGATIVE_LONG.optionalFieldOf("leftoverDropletsAmount", 0L).forGetter(fluidStack -> {
            long milliBucketsAmount = FluidUtils.convertDropletsToMilliBuckets(fluidStack.dropletsAmount);
            return fluidStack.dropletsAmount - FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount);
        }), DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(fluidStack -> {
            return fluidStack.fluidVariant.getComponents();
        })).apply(instance, (id, milliBucketsAmount, leftoverDropletsAmount, fluidComponents) -> {
            Fluid fluid = BuiltInRegistries.FLUID.get(id);

            long dropletsAmount = FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount) + leftoverDropletsAmount;

            return new FluidStack(fluid, fluidComponents, dropletsAmount);
        });
    });
    
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> STREAM_CODEC = StreamCodec.composite(
            FluidVariant.PACKET_CODEC, FluidStack::getFluidVariant,
            PacketCodecFix.LONG, FluidStack::getDropletsAmount,
            FluidStack::new
    );

    private FluidVariant fluidVariant;
    private long dropletsAmount;

    public static FluidStack fromNbt(CompoundTag nbtCompound, HolderLookup.Provider registries) {
        if(nbtCompound.isEmpty())
            return new FluidStack(Fluids.EMPTY, DataComponentPatch.EMPTY, 0);

        Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(nbtCompound.getString("id")));

        //Save milli buckets amount in "Amount" for compatibility with Forge
        //Save leftover droplets amount in "LeftoverDropletsAmount" for preventing rounding errors
        long milliBucketsAmount = nbtCompound.getLong("amount");
        long dropletsLeftOverAmount = nbtCompound.contains("leftoverDropletsAmount")?
                nbtCompound.getLong("leftoverDropletsAmount"):0;
        long dropletsAmount = FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount) + dropletsLeftOverAmount;

        DataComponentPatch fluidComponents = nbtCompound.contains("components")?
                DataComponentPatch.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE),
                        nbtCompound.get("components")).resultOrPartial((error) -> {
                            LOGGER.error("Tried to load invalid components: '{}'", error);
                        }).orElse(DataComponentPatch.EMPTY):DataComponentPatch.EMPTY;

        return new FluidStack(fluid, fluidComponents, dropletsAmount);
    }

    public FluidStack(Fluid fluid, long dropletsAmount) {
        this(fluid, DataComponentPatch.EMPTY, dropletsAmount);
    }

    public FluidStack(Fluid fluid, DataComponentPatch fluidComponents, long dropletsAmount) {
        this(FluidVariant.of(fluid, fluidComponents), dropletsAmount);
    }

    public FluidStack(FluidVariant fluidVariant, long dropletsAmount) {
        this.fluidVariant = fluidVariant;
        this.dropletsAmount = dropletsAmount;
    }

    public FluidVariant getFluidVariant() {
        return fluidVariant;
    }

    public void setFluidVariant(FluidVariant fluidVariant) {
        this.fluidVariant = fluidVariant;
    }

    public long getDropletsAmount() {
        return dropletsAmount;
    }

    public void setDropletsAmount(long dropletsAmount) {
        this.dropletsAmount = dropletsAmount;
    }

    public long getMilliBucketsAmount() {
        return FluidUtils.convertDropletsToMilliBuckets(dropletsAmount);
    }

    public boolean isEmpty() {
        return fluidVariant.isBlank();
    }

    public Fluid getFluid() {
        return fluidVariant.getFluid();
    }

    public String getTranslationKey() {
        return fluidVariant.getFluid().defaultFluidState().createLegacyBlock().getBlock().getDescriptionId();
    }

    public CompoundTag toNBT(CompoundTag nbtCompound, HolderLookup.Provider registries) {
        if(fluidVariant.isBlank())
                return nbtCompound;

        nbtCompound.putString("id", BuiltInRegistries.FLUID.getKey(fluidVariant.getFluid()).toString());

        //Save milli buckets amount in "Amount" for compatibility with Forge
        //Save leftover droplets amount in "LeftoverDropletsAmount" for preventing rounding errors
        long milliBucketsAmount = FluidUtils.convertDropletsToMilliBuckets(dropletsAmount);
        long dropletsLeftOverAmount = dropletsAmount - FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount);
        nbtCompound.putLong("amount", milliBucketsAmount);
        if(dropletsLeftOverAmount > 0)
            nbtCompound.putLong("leftoverDropletsAmount", dropletsLeftOverAmount);

        if(fluidVariant.getComponents() != null) {
            nbtCompound.put("components", DataComponentPatch.CODEC.encode(fluidVariant.getComponents(),
                    registries.createSerializationContext(NbtOps.INSTANCE), new CompoundTag()).getOrThrow());
        }

        return nbtCompound;
    }
}
