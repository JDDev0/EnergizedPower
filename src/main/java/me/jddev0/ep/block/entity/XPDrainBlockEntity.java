package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.mixin.entity.ExperienceOrbCountGetterSetter;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import me.jddev0.ep.util.XPUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

public class XPDrainBlockEntity extends BlockEntity {
    private static final boolean PLAY_PLAYER_XP_PICKUP_SOUND = ModConfigs.COMMON_XP_DRAIN_PLAY_PLAYER_XP_PICKUP_SOUND.getValue();
    private static final int PLAYER_XP_DRAIN_AMOUNT = ModConfigs.COMMON_XP_DRAIN_PLAYER_XP_DRAIN_AMOUNT.getValue();
    private static final int TICKS_TO_DRAIN_FROM_PLAYER = ModConfigs.COMMON_XP_DRAIN_TICKS_TO_DRAIN_FROM_PLAYER.getValue();
    private static final int MAX_XP_ORB_ATTRACTION_DISTANCE = ModConfigs.COMMON_XP_DRAIN_MAX_XP_ORB_ATTRACTION_DISTANCE.getValue();

    public XPDrainBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EPBlockEntities.XP_DRAIN_ENTITY, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, XPDrainBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        Vec3 blockCenter = Vec3.atCenterOf(blockPos);

        if(level.getGameTime() % TICKS_TO_DRAIN_FROM_PLAYER == 0) {
            List<Player> players = level.getEntities(EntityTypeTest.forClass(Player.class), AABB.of(BoundingBox.fromCorners(
                    new Vec3i(blockPos.getX() - 2, blockPos.getY() - 2,
                            blockPos.getZ() - 2),
                    new Vec3i(blockPos.getX() + 2, blockPos.getY() + 2,
                            blockPos.getZ() + 2))), EntitySelector.NO_SPECTATORS.
                    and(entity -> entity.distanceToSqr(Vec3.atCenterOf(blockPos)) <= 1.5*1.5));

            for(Player player:players) {
                if(player.isShiftKeyDown())
                    continue;

                Consumer<Integer> drainPlayerXP = xp -> {
                    player.giveExperiencePoints(xp);

                    if(PLAY_PLAYER_XP_PICKUP_SOUND) {
                        level.playSound(null, blockPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.1f,
                                (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * .33f + .9f);
                    }
                };

                int xpToDrain = (int)Math.min(PLAYER_XP_DRAIN_AMOUNT, XPUtils.getTotalXPFromPlayer(player));
                blockEntity.pushLiquidXP(xpToDrain, () -> drainPlayerXP.accept(-xpToDrain), maxXP -> {
                    if(maxXP > 0) {
                        //Try again with maximal supported amount
                        blockEntity.pushLiquidXP(maxXP, () -> drainPlayerXP.accept(-maxXP), maxXPi -> {});
                    }
                });
            }
        }

        List<ExperienceOrb> xpOrbs = level.getEntities(EntityTypeTest.forClass(ExperienceOrb.class), AABB.of(BoundingBox.fromCorners(
                new Vec3i(blockPos.getX() - MAX_XP_ORB_ATTRACTION_DISTANCE, blockPos.getY() - MAX_XP_ORB_ATTRACTION_DISTANCE,
                        blockPos.getZ() - MAX_XP_ORB_ATTRACTION_DISTANCE),
                new Vec3i(blockPos.getX() + MAX_XP_ORB_ATTRACTION_DISTANCE, blockPos.getY() + MAX_XP_ORB_ATTRACTION_DISTANCE,
                        blockPos.getZ() + MAX_XP_ORB_ATTRACTION_DISTANCE))), EntitySelector.NO_SPECTATORS.
                and(entity -> entity.distanceToSqr(Vec3.atCenterOf(blockPos)) <= MAX_XP_ORB_ATTRACTION_DISTANCE*MAX_XP_ORB_ATTRACTION_DISTANCE));

        //Attract XP orbs
        for(ExperienceOrb xpOrb:xpOrbs) {
            Vec3 delta = blockCenter.subtract(xpOrb.position());
            double distance = delta.lengthSqr();
            double power = 1.0 - Math.sqrt(distance) / 8.0;

            xpOrb.addDeltaMovement(delta.normalize().scale(power * power * 0.1));
            xpOrb.needsSync = true;
        }

        blockCenter = blockCenter.subtract(0, 0.4, 0);

        //Convert XP orbs
        for(ExperienceOrb xpOrb:xpOrbs) {
            Vec3 delta = blockCenter.subtract(xpOrb.position());
            double distance = delta.lengthSqr();
            if(distance < 0.35*0.35) {
                int count = ((ExperienceOrbCountGetterSetter)xpOrb).getCount();

                blockEntity.pushLiquidXP(xpOrb.getValue(), () -> {
                    if(count == 1) {
                        xpOrb.discard();
                    }else {
                        ((ExperienceOrbCountGetterSetter)xpOrb).setCount(count - 1);
                        xpOrb.needsSync = true;
                    }
                }, maxXP -> {});
            }
        }
    }

    private void pushLiquidXP(int xpAmount, Runnable onFinalCommit, Consumer<Integer> onCancel) {
        long fluidAmountDroplets = xpAmount * XPUtils.XP_TO_LIQUID_RATIO;
        if(fluidAmountDroplets <= 0)
            return;

        BlockPos outputBlockPos = worldPosition.relative(Direction.DOWN);
        BlockEntity outputBlockEntity = level.getBlockEntity(outputBlockPos);

        Storage<FluidVariant> fluidHandler = FluidStorage.SIDED.find(level, outputBlockPos, level.getBlockState(outputBlockPos), outputBlockEntity, Direction.DOWN.getOpposite());
        if(fluidHandler == null)
            return;

        long insertedAmount;
        try(Transaction transaction = Transaction.openOuter()) {
            insertedAmount = fluidHandler.insert(FluidVariant.of(EPFluids.LIQUID_XP), fluidAmountDroplets, transaction);
            if(insertedAmount == fluidAmountDroplets) {
                transaction.commit();
            }
        }

        if(insertedAmount == fluidAmountDroplets) {
            onFinalCommit.run();
        }else {
            onCancel.accept((int)(insertedAmount / XPUtils.XP_TO_LIQUID_RATIO));
        }
    }
}