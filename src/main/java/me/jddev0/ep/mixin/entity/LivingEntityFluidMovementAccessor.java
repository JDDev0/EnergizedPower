package me.jddev0.ep.mixin.entity;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityFluidMovementAccessor {
    @Invoker("jumpOutOfFluid")
    void energizedpower$jumpOutOfFluid(double oldY);
}
