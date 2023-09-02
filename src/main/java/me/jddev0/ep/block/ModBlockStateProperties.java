package me.jddev0.ep.block;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ModBlockStateProperties {
    private ModBlockStateProperties() {}

    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_UP = EnumProperty.create("up", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_DOWN = EnumProperty.create("down", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_NORTH = EnumProperty.create("north", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_EAST = EnumProperty.create("east", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_SOUTH = EnumProperty.create("south", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_WEST = EnumProperty.create("west", PipeConnection.class);
    public static final EnumProperty<ConveyorBeltDirection> CONVEYOR_BELT_FACING = EnumProperty.create("facing", ConveyorBeltDirection.class);

    public enum PipeConnection implements StringRepresentable {
        NOT_CONNECTED,
        CONNECTED,
        EXTRACT;

        public boolean isConnected() {
            return this != NOT_CONNECTED;
        }

        public boolean isInsert() {
            return this == CONNECTED;
        }

        public boolean isExtract() {
            return this == EXTRACT;
        }

        @Override
        @NotNull
        public String getSerializedName() {
            return name().toLowerCase();
        }

        public String getTranslationKey() {
            return "block_state.energizedpower.pipe_connection." + getSerializedName();
        }
    }

    public enum ConveyorBeltDirection implements StringRepresentable {
        NORTH_SOUTH(Direction.SOUTH),
        SOUTH_NORTH(Direction.NORTH),
        WEST_EAST(Direction.EAST),
        EAST_WEST(Direction.WEST),

        ASCENDING_NORTH_SOUTH(Direction.SOUTH),
        ASCENDING_SOUTH_NORTH(Direction.NORTH),
        ASCENDING_WEST_EAST(Direction.EAST),
        ASCENDING_EAST_WEST(Direction.WEST),

        DESCENDING_NORTH_SOUTH(Direction.SOUTH),
        DESCENDING_SOUTH_NORTH(Direction.NORTH),
        DESCENDING_WEST_EAST(Direction.EAST),
        DESCENDING_EAST_WEST(Direction.WEST);

        /**
         * @param slope <ul>
         *      <li>null: flat</li>
         *      <li>false: descending</li>
         *      <li>true: ascending</li>
         * </ul>
         */
        public static ConveyorBeltDirection of(@NotNull Direction direction, @Nullable Boolean slope) {
            return switch(direction) {
                case SOUTH -> {
                    if(slope == null)
                        yield NORTH_SOUTH;
                    else if(slope)
                        yield ASCENDING_NORTH_SOUTH;
                    else
                        yield DESCENDING_NORTH_SOUTH;
                }
                case NORTH -> {
                    if(slope == null)
                        yield SOUTH_NORTH;
                    else if(slope)
                        yield ASCENDING_SOUTH_NORTH;
                    else
                        yield DESCENDING_SOUTH_NORTH;
                }
                case EAST -> {
                    if(slope == null)
                        yield WEST_EAST;
                    else if(slope)
                        yield ASCENDING_WEST_EAST;
                    else
                        yield DESCENDING_WEST_EAST;
                }
                case WEST -> {
                    if(slope == null)
                        yield EAST_WEST;
                    else if(slope)
                        yield ASCENDING_EAST_WEST;
                    else
                        yield DESCENDING_EAST_WEST;
                }
                default -> throw new IllegalArgumentException("Direction must be one of NORTH, SOUTH, WEST, or EAST");
            };
        }

        private final Direction direction;

        ConveyorBeltDirection(Direction direction) {
            this.direction = direction;
        }

        public Direction getDirection() {
            return direction;
        }

        public boolean isAscending() {
            return this == ASCENDING_NORTH_SOUTH || this == ASCENDING_SOUTH_NORTH || this == ASCENDING_WEST_EAST ||
                    this == ASCENDING_EAST_WEST;
        }

        public boolean isDescending() {
            return this == DESCENDING_NORTH_SOUTH || this == DESCENDING_SOUTH_NORTH || this == DESCENDING_WEST_EAST ||
                    this == DESCENDING_EAST_WEST;
        }

        /**
         * @return <ul>
         *      <li>null: flat</li>
         *      <li>false: descending</li>
         *      <li>true: ascending</li>
         * </ul>
         */
        public @Nullable Boolean getSlope() {
            if(isAscending())
                return true;

            if(isDescending())
                return false;

            return null;
        }

        @Override
        @NotNull
        public String getSerializedName() {
            return name().toLowerCase();
        }

        public String getTranslationKey() {
            return "block_state.energizedpower.conveyor_belt_direction." + getSerializedName();
        }
    }
}
