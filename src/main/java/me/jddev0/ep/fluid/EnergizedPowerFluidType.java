package me.jddev0.ep.fluid;

import me.jddev0.ep.mixin.entity.LivingEntityFluidMovementAccessor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class EnergizedPowerFluidType extends FluidType {
    private final Identifier stillTexture;
    private final Identifier flowingTexture;
    private final Identifier overlayTexture;
    private final int tintColor;
    private final Vector3f fogColor;

    public EnergizedPowerFluidType(Properties props, Identifier stillTexture, Identifier flowingTexture, Identifier overlayTexture, int tintColor, Vector3f fogColor) {
        super(props);

        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColor = tintColor;
        this.fogColor = fogColor;
    }

    public Identifier getStillTexture() {
        return stillTexture;
    }

    public Identifier getFlowingTexture() {
        return flowingTexture;
    }

    public Identifier getOverlayTexture() {
        return overlayTexture;
    }

    public int getTintColor() {
        return tintColor;
    }

    public Vector3f getFogColor() {
        return fogColor;
    }

    @Override
    public @Nullable SoundEvent getSound(SoundAction action) {
        if(action == SoundActions.BUCKET_FILL)
            return SoundEvents.BUCKET_FILL;
        else if(action == SoundActions.BUCKET_EMPTY)
            return SoundEvents.BUCKET_EMPTY;

        return null;
    }

    @Override
    public boolean move(LivingEntity entity, Vec3 movementVector, double gravity) {
        boolean isFalling = entity.getDeltaMovement().y <= 0.0;
        double oldY = entity.getY();

        double movementMultiplier = .9365 - getViscosity() * .000072916;

        entity.moveRelative(.02f, movementVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        boolean isInShallowFluid = entity.getFluidHeight(this) <= entity.getFluidJumpThreshold();
        if(isInShallowFluid) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(movementMultiplier, .8, movementMultiplier));
            Vec3 movement = entity.getFluidFallingAdjustedMovement(gravity, isFalling, entity.getDeltaMovement());
            entity.setDeltaMovement(movement);
        }else {
            entity.setDeltaMovement(entity.getDeltaMovement().scale(movementMultiplier));
        }

        if(gravity != 0.0)
            entity.setDeltaMovement(entity.getDeltaMovement().add(0., -gravity / 4., 0.));

        ((LivingEntityFluidMovementAccessor)entity).energizedpower$jumpOutOfFluid(oldY);

        return true;
    }
}
