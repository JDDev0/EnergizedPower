package me.jddev0.ep.mixin.entity;

import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExperienceOrb.class)
public interface ExperienceOrbCountGetterSetter {
    @Accessor("count")
    int getCount();

    @Accessor("count")
    @Mutable
    void setCount(int count);
}
