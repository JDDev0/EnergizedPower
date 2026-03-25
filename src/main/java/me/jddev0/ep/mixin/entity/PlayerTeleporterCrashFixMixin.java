package me.jddev0.ep.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public abstract class PlayerTeleporterCrashFixMixin {
    @ModifyVariable(method = "checkInsideBlocks(Ljava/util/List;Lnet/minecraft/world/entity/InsideBlockEffectApplier$StepBasedCollector;)V", at = @At("HEAD"))
    private List<?> copyQueuedCollisionsChecks(List<?> queuedCollisionChecks) {
        //Fixes ConcurrentModificationException thrown in "Entity.checkBlockCollisions()" if Teleporter or Inventory Teleporter is used
        return new ArrayList<>(queuedCollisionChecks);
    }
}
