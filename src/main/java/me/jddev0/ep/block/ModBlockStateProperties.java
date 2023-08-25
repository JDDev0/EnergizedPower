package me.jddev0.ep.block;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public final class ModBlockStateProperties {
    private ModBlockStateProperties() {}

    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_UP = EnumProperty.create("up", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_DOWN = EnumProperty.create("down", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_NORTH = EnumProperty.create("north", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_EAST = EnumProperty.create("east", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_SOUTH = EnumProperty.create("south", PipeConnection.class);
    public static final EnumProperty<PipeConnection> PIPE_CONNECTION_WEST = EnumProperty.create("west", PipeConnection.class);

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
}
