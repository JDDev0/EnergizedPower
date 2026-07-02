package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.mixin.entity.ExperienceOrbCountGetterSetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.function.Consumer;

public class XPDrainBlockEntity extends BlockEntity {
    private static final int XP_TO_LIQUID_RATIO = ModConfigs.COMMON_XP_TO_LIQUID_RATIO.getValue();
    private static final int TICKS_PER_PLAYER_LEVEL = ModConfigs.COMMON_XP_DRAIN_TICKS_PER_PLAYER_LEVEL.getValue();
    private static final int MAX_XP_ORB_ATTRACTION_DISTANCE = ModConfigs.COMMON_XP_DRAIN_MAX_XP_ORB_ATTRACTION_DISTANCE.getValue();

    public XPDrainBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EPBlockEntities.XP_DRAIN_ENTITY.get(), blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, XPDrainBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        Vec3 blockCenter = Vec3.atCenterOf(blockPos);

        if(level.getGameTime() % TICKS_PER_PLAYER_LEVEL == 0) {
            List<Player> players = level.getEntities(EntityTypeTest.forClass(Player.class), AABB.of(BoundingBox.fromCorners(
                    new Vec3i(blockPos.getX() - 2, blockPos.getY() - 2,
                            blockPos.getZ() - 2),
                    new Vec3i(blockPos.getX() + 2, blockPos.getY() + 2,
                            blockPos.getZ() + 2))), EntitySelector.NO_SPECTATORS.
                    and(entity -> entity.distanceToSqr(Vec3.atCenterOf(blockPos)) <= 1.5*1.5));

            for(Player player:players) {
                if(player.isShiftKeyDown())
                    continue;

                int playerLevel = player.experienceLevel;
                float experienceProgress = player.experienceProgress;
                if(playerLevel > 0 || experienceProgress > 0) {
                    int xpNeededForNextLevel = getXpNeededForNextLevel(playerLevel);

                    int xpPointsAboveXPLevel = (int)(experienceProgress * xpNeededForNextLevel);
                    int xpToDrain;
                    if(xpPointsAboveXPLevel > 0) {
                        xpToDrain = xpPointsAboveXPLevel;
                    }else {
                        xpToDrain = getXpNeededForNextLevel(playerLevel - 1);
                    }

                    blockEntity.pushLiquidXP(xpToDrain, () -> player.giveExperiencePoints(-xpToDrain), maxXP -> {
                        if(maxXP > 0) {
                            //Try again with maximal supported amount
                            blockEntity.pushLiquidXP(maxXP, () -> player.giveExperiencePoints(-maxXP), maxXPi -> {});
                        }
                    });
                }
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

    private static int getXpNeededForNextLevel(int currentLevel) {
        if(currentLevel >= 30) {
            return 112 + (currentLevel - 30) * 9;
        }else {
            return currentLevel >= 15 ? 37 + (currentLevel - 15) * 5 : 7 + currentLevel * 2;
        }
    }

    private void pushLiquidXP(int xpAmount, Runnable onFinalCommit, Consumer<Integer> onCancel) {
        BlockPos outputBlockPos = worldPosition.relative(Direction.DOWN);
        BlockEntity outputBlockEntity = level.getBlockEntity(outputBlockPos);

        ResourceHandler<FluidResource> fluidHandler = level.getCapability(Capabilities.Fluid.BLOCK, outputBlockPos, level.getBlockState(outputBlockPos),
                outputBlockEntity, Direction.DOWN.getOpposite());
        if(fluidHandler == null)
            return;

        int fluidAmount = xpAmount * XP_TO_LIQUID_RATIO;
        if(fluidAmount <= 0)
            return;

        int insertedAmount;
        try(Transaction transaction = Transaction.open(null)) {
            insertedAmount = fluidHandler.insert(FluidResource.of(EPFluids.LIQUID_XP), fluidAmount, transaction);
            if(insertedAmount == fluidAmount) {
                transaction.commit();
            }
        }

        if(insertedAmount == fluidAmount) {
            onFinalCommit.run();
        }else {
            onCancel.accept(insertedAmount / XP_TO_LIQUID_RATIO);
        }
    }
}