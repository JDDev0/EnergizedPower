package me.jddev0.ep.datagen.model;

import net.minecraft.client.data.models.model.TextureSlot;

public final class ModTextureSlot {
    public static final TextureSlot INTERIOR = TextureSlot.create("interior");

    public static final TextureSlot BELT = TextureSlot.create("belt");

    public static final TextureSlot FLUID_PIPE_CORE = TextureSlot.create("fluid_pipe_core");
    public static final TextureSlot FLUID_PIPE_SIDE = TextureSlot.create("fluid_pipe_side");
    public static final TextureSlot FLUID_PIPE_SIDE_INNER = TextureSlot.create("fluid_pipe_side_inner");
    public static final TextureSlot FLUID_PIPE_SIDE_OUTER = TextureSlot.create("fluid_pipe_side_outer");

    public static final TextureSlot CABLE = TextureSlot.create("cable");

    private ModTextureSlot() {}
}
