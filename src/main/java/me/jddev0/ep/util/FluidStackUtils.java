package me.jddev0.ep.util;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class FluidStackUtils {
    private FluidStackUtils() {}

    public static @NotNull FluidStack fromNullableFluidStackTemplate(@Nullable FluidStackTemplate fluidStack) {
        if(fluidStack == null)
            return FluidStack.EMPTY;

        return fluidStack.create();
    }

    public static boolean isSameFluidSameComponents(FluidStackTemplate a, FluidStackTemplate b) {
        if(a == null && b == null)
            return true;

        if(a == null || b == null || !a.is(b.fluid()))
            return false;

        return Objects.equals(a.components(), b.components());
    }
}
