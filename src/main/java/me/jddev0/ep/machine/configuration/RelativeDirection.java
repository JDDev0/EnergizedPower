package me.jddev0.ep.machine.configuration;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum RelativeDirection implements StringRepresentable {
    BOTTOM, TOP, FRONT, BACK, RIGHT, LEFT;

    public @NotNull Direction toRawDirection() {
        //Direction and relative direction have the same ordinal positions
        return Direction.values()[this.ordinal()];
    }

    public static @NotNull RelativeDirection fromRawDirection(@NotNull Direction direction) {
        //Direction and relative direction have the same ordinal positions
        return RelativeDirection.values()[direction.ordinal()];
    }

    public static @NotNull RelativeDirection resolve(@NotNull Direction facing, @NotNull Direction side) {
        //TODO support facing up and facing down

        if(facing == side)
            return RelativeDirection.FRONT;
        else if(facing.getOpposite() == side)
            return RelativeDirection.BACK;
        else if(facing.getClockWise() == side)
            return RelativeDirection.LEFT;
        else if(facing.getCounterClockWise() == side)
            return RelativeDirection.RIGHT;
        else if(side == Direction.UP)
            return RelativeDirection.TOP;
        else
            return RelativeDirection.BOTTOM;
    }

    public static @NotNull RelativeDirection @NotNull [] sidesOnlyValues() {
        return new RelativeDirection[] {
                RelativeDirection.FRONT, RelativeDirection.BACK, RelativeDirection.RIGHT, RelativeDirection.LEFT
        };
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.US);
    }
}