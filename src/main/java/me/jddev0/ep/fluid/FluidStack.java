package me.jddev0.ep.fluid;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.codec.CodecFix;
import me.jddev0.ep.codec.PacketCodecFix;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.component.ComponentChanges;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class FluidStack {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<FluidStack> CODEC_MILLIBUCKETS = RecordCodecBuilder.create(instance -> {
        return instance.group(Identifier.CODEC.fieldOf("id").forGetter(fluidStack -> {
            return Registries.FLUID.getId(fluidStack.fluidVariant.getFluid());
        }),CodecFix.NON_NEGATIVE_LONG.fieldOf("amount").forGetter(fluidStack -> {
            return FluidUtils.convertDropletsToMilliBuckets(fluidStack.dropletsAmount);
        }), CodecFix.NON_NEGATIVE_LONG.optionalFieldOf("leftoverDropletsAmount", 0L).forGetter(fluidStack -> {
            long milliBucketsAmount = FluidUtils.convertDropletsToMilliBuckets(fluidStack.dropletsAmount);
            return fluidStack.dropletsAmount - FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount);
        }), ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(fluidStack -> {
            return fluidStack.fluidVariant.getComponents();
        })).apply(instance, (id, milliBucketsAmount, leftoverDropletsAmount, fluidComponents) -> {
            Fluid fluid = Registries.FLUID.get(id);

            long dropletsAmount = FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount) + leftoverDropletsAmount;

            return new FluidStack(fluid, fluidComponents, dropletsAmount);
        });
    });
    
    public static final PacketCodec<RegistryByteBuf, FluidStack> PACKET_CODEC = PacketCodec.tuple(
            FluidVariant.PACKET_CODEC, FluidStack::getFluidVariant,
            PacketCodecFix.LONG, FluidStack::getDropletsAmount,
            FluidStack::new
    );

    private FluidVariant fluidVariant;
    private long dropletsAmount;

    public static FluidStack fromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup registries) {
        if(nbtCompound.isEmpty())
            return new FluidStack(Fluids.EMPTY, ComponentChanges.EMPTY, 0);

        Fluid fluid = Registries.FLUID.get(new Identifier(nbtCompound.getString("id")));

        //Save milli buckets amount in "Amount" for compatibility with Forge
        //Save leftover droplets amount in "LeftoverDropletsAmount" for preventing rounding errors
        long milliBucketsAmount = nbtCompound.getLong("amount");
        long dropletsLeftOverAmount = nbtCompound.contains("leftoverDropletsAmount")?
                nbtCompound.getLong("leftoverDropletsAmount"):0;
        long dropletsAmount = FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount) + dropletsLeftOverAmount;

        ComponentChanges fluidComponents = nbtCompound.contains("components")?
                ComponentChanges.CODEC.parse(registries.getOps(NbtOps.INSTANCE),
                        nbtCompound.get("components")).resultOrPartial((error) -> {
                            LOGGER.error("Tried to load invalid components: '{}'", error);
                        }).orElse(ComponentChanges.EMPTY):ComponentChanges.EMPTY;

        return new FluidStack(fluid, fluidComponents, dropletsAmount);
    }

    public FluidStack(Fluid fluid, long dropletsAmount) {
        this(fluid, null, dropletsAmount);
    }

    public FluidStack(Fluid fluid, ComponentChanges fluidComponents, long dropletsAmount) {
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
        return fluidVariant.getFluid().getDefaultState().getBlockState().getBlock().getTranslationKey();
    }

    public NbtCompound toNBT(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup registries) {
        if(fluidVariant.isBlank())
                return nbtCompound;

        nbtCompound.putString("id", Registries.FLUID.getId(fluidVariant.getFluid()).toString());

        //Save milli buckets amount in "Amount" for compatibility with Forge
        //Save leftover droplets amount in "LeftoverDropletsAmount" for preventing rounding errors
        long milliBucketsAmount = FluidUtils.convertDropletsToMilliBuckets(dropletsAmount);
        long dropletsLeftOverAmount = dropletsAmount - FluidUtils.convertMilliBucketsToDroplets(milliBucketsAmount);
        nbtCompound.putLong("amount", milliBucketsAmount);
        if(dropletsLeftOverAmount > 0)
            nbtCompound.putLong("leftoverDropletsAmount", dropletsLeftOverAmount);

        if(fluidVariant.getComponents() != null) {
            nbtCompound.put("components", ComponentChanges.CODEC.encode(fluidVariant.getComponents(),
                    registries.getOps(NbtOps.INSTANCE), new NbtCompound()).getOrThrow());
        }

        return nbtCompound;
    }
}
