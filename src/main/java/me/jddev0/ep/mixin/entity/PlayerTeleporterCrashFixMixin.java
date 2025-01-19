package me.jddev0.ep.mixin.entity;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class PlayerTeleporterCrashFixMixin {
    @ModifyVariable(method = "checkBlockCollision", at = @At("HEAD"))
    private List<?> copyQueuedCollisionChecks(List<?> queuedCollisionChecks) {
        //Fixes ConcurrentModificationException thrown in "Entity.checkBlockCollision()" if Teleporter or Inventory Teleporter is used
        return new ArrayList<>(queuedCollisionChecks);
    }
}
