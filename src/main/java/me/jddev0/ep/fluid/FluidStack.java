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
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
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

    public FluidStack(Fluid fluid, long dropletsAmount) {
        this(fluid, ComponentChanges.EMPTY, dropletsAmount);
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
}
