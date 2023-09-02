package me.jddev0.ep.item;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.FluidPipeBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidPipeBlockEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import me.jddev0.ep.block.entity.ItemConveyorBeltBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem(FabricItemSettings props) {
        super(props);
    }

    public static Direction getCurrentFace(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        Direction currentFace = (nbt != null && nbt.contains("currentFace"))?Direction.byName(nbt.getString("currentFace")):
                Direction.DOWN;
        if(currentFace == null)
            currentFace = Direction.DOWN;

        return currentFace;
    }

    public static void cycleCurrentFace(ItemStack itemStack, ServerPlayerEntity player) {
        int diff = player.isSneaking() ? -1 : 1;
        Direction currentFace = getCurrentFace(itemStack);
        currentFace = Direction.values()[(currentFace.ordinal() + diff + Direction.values().length) %
                Direction.values().length];
        itemStack.getOrCreateNbt().putString("currentFace", currentFace.asString());

        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                Text.translatable("tooltip.energizedpower.wrench.select_face",
                        Text.translatable("tooltip.energizedpower.direction." + currentFace.asString()).
                                formatted(Formatting.WHITE, Formatting.BOLD)
                ).formatted(Formatting.GRAY)
        ));
    }

    public ActionResult useOnFluidPipe(ItemUsageContext useOnContext) {
        World level = useOnContext.getWorld();
        BlockPos blockPos = useOnContext.getBlockPos();
        BlockState state = level.getBlockState(blockPos);

        ItemStack itemStack = useOnContext.getStack();
        Direction currentFace = getCurrentFace(itemStack);

        BlockPos testPos = blockPos.offset(currentFace);

        PlayerEntity player = useOnContext.getPlayer();

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(testBlockEntity == null || testBlockEntity instanceof FluidPipeBlockEntity) {
            //Connections to non-fluid blocks nor connections to another pipe can not be modified

            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.wrench.face_change_not_possible",
                                Text.translatable("tooltip.energizedpower.direction." + currentFace.asString()).
                                        formatted(Formatting.WHITE)
                        ).formatted(Formatting.RED)
                ));
            }

            return ActionResult.SUCCESS;
        }

        Storage<FluidVariant> fluidStorage = FluidStorage.SIDED.find(level, testPos, currentFace.getOpposite());
        if(fluidStorage == null) {
            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.wrench.face_change_not_possible",
                                Text.translatable("tooltip.energizedpower.direction." + currentFace.asString()).
                                        formatted(Formatting.WHITE)
                        ).formatted(Formatting.RED)
                ));
            }

            return ActionResult.SUCCESS;
        }


        //If first has no next, no tanks are present
        if(!fluidStorage.iterator().hasNext()) {
            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.wrench.face_change_not_possible",
                                Text.translatable("tooltip.energizedpower.direction." + currentFace.asString()).
                                        formatted(Formatting.WHITE)
                        ).formatted(Formatting.RED)
                ));
            }

            return ActionResult.SUCCESS;
        }

        EnumProperty<ModBlockStateProperties.PipeConnection> pipeConnectionProperty =
                FluidPipeBlock.getPipeConnectionPropertyFromDirection(currentFace);

        int diff = player != null && player.isSneaking()?-1:1;

        ModBlockStateProperties.PipeConnection pipeConnection = state.get(pipeConnectionProperty);
        pipeConnection = ModBlockStateProperties.PipeConnection.values()[(pipeConnection.ordinal() + diff +
                ModBlockStateProperties.PipeConnection.values().length) %
                ModBlockStateProperties.PipeConnection.values().length];

        level.setBlockState(blockPos, state.with(pipeConnectionProperty, pipeConnection), 3);

        if(player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.wrench.face_change",
                            Text.translatable("tooltip.energizedpower.direction." + currentFace.asString()).
                                    formatted(Formatting.WHITE),
                            Text.translatable(pipeConnection.getTranslationKey()).
                                    formatted(Formatting.WHITE, Formatting.BOLD)
                    ).formatted(Formatting.GREEN)
            ));
        }

        return ActionResult.SUCCESS;
    }

    public ActionResult useOnConveyorBelt(ItemUsageContext useOnContext) {
        World level = useOnContext.getWorld();
        BlockPos blockPos = useOnContext.getBlockPos();
        BlockState state = level.getBlockState(blockPos);

        PlayerEntity player = useOnContext.getPlayer();

        ModBlockStateProperties.ConveyorBeltDirection facing = state.get(ItemConveyorBeltBlock.FACING);
        Boolean shape;

        if(player != null && player.isSneaking()) {
            if(facing.isAscending())
                shape = null;
            else if(facing.isDescending())
                shape = true;
            else
                shape = false;
        }else {
            if(facing.isAscending())
                shape = false;
            else if(facing.isDescending())
                shape = null;
            else
                shape = true;
        }

        level.setBlockState(blockPos, state.with(ItemConveyorBeltBlock.FACING, ModBlockStateProperties.ConveyorBeltDirection.of(facing.getDirection(), shape)), 3);

        if(player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.wrench.change",
                            Text.translatable("tooltip.energizedpower.conveyor_belt_direction.slope." + (shape == null?"flat":(shape?"ascending":"descending"))).
                                    formatted(Formatting.WHITE, Formatting.BOLD)
                    ).formatted(Formatting.GREEN)
            ));
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext useOnContext) {
        World level = useOnContext.getWorld();
        if(level.isClient())
            return ActionResult.SUCCESS;

        BlockPos blockPos = useOnContext.getBlockPos();
        BlockState state = level.getBlockState(blockPos);
        if(state.isOf(ModBlocks.FLUID_PIPE) && (level.getBlockEntity(blockPos) instanceof FluidPipeBlockEntity))
            return useOnFluidPipe(useOnContext);

        if(state.isOf(ModBlocks.ITEM_CONVEYOR_BELT) && (level.getBlockEntity(blockPos) instanceof ItemConveyorBeltBlockEntity))
            return useOnConveyorBelt(useOnContext);

        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getStackInHand(interactionHand);

        if(level.isClient())
            return TypedActionResult.success(itemStack);

       cycleCurrentFace(itemStack, (ServerPlayerEntity)player);

        return TypedActionResult.success(itemStack);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack itemStack, BlockState blockState) {
        //Allow current face swap in survival in a reasonable amount of time
        return 1000.f;
    }

    @Override
    public boolean canMine(BlockState state, World level, BlockPos blockPos, PlayerEntity player) {
        if(level.isClient() || !(player instanceof ServerPlayerEntity))
            return false;

        ItemStack itemStack = player.getMainHandStack();

        NbtCompound nbt = itemStack.getNbt();
        if(nbt != null && nbt.contains("attackingCycleCooldown"))
            return false;

        cycleCurrentFace(itemStack, (ServerPlayerEntity)player);
        itemStack.getOrCreateNbt().putInt("attackingCycleCooldown", 5);

        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Direction currentFace = getCurrentFace(stack);
        tooltip.add(Text.translatable("tooltip.energizedpower.wrench.select_face",
                Text.translatable("tooltip.energizedpower.direction." + currentFace.asString()).
                        formatted(Formatting.WHITE, Formatting.BOLD)
        ).formatted(Formatting.GRAY));

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.wrench.txt.shift").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemStack, level, entity, slot, selected);

        if(level.isClient())
            return;

        if(!(entity instanceof PlayerEntity))
            return;

        NbtCompound nbt = itemStack.getNbt();
        if(nbt != null && nbt.contains("attackingCycleCooldown")) {
            int attackingCycleCooldown = nbt.getInt("attackingCycleCooldown") - 1;
            if(attackingCycleCooldown <= 0)
                nbt.remove("attackingCycleCooldown");
            else
                nbt.putInt("attackingCycleCooldown", attackingCycleCooldown);
        }
    }
}
