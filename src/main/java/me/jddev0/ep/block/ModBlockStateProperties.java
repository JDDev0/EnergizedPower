package me.jddev0.ep.block;

import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.NotNull;

public final class ModBlockStateProperties {
    private ModBlockStateProperties() {}

    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_UP = EnumProperty.of("up", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_DOWN = EnumProperty.of("down", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_NORTH = EnumProperty.of("north", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_EAST = EnumProperty.of("east", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_SOUTH = EnumProperty.of("south", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_WEST = EnumProperty.of("west", PipeConnection.class);

    public enum PipeConnection implements StringIdentifiable {
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
        public String asString() {
            return name().toLowerCase();
        }

        public String getTranslationKey() {
            return "block_state.energizedpower." + asString();
        }
    }
}
