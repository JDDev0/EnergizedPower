package me.jddev0.ep.item;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.FluidPipeBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidPipeBlockEntity;
import me.jddev0.ep.block.entity.ItemConveyorBeltBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem(Properties props) {
        super(props);
    }

    public static Direction getCurrentFace(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getTag();
        Direction currentFace = (nbt != null && nbt.contains("currentFace"))?Direction.byName(nbt.getString("currentFace")):
                Direction.DOWN;
        if(currentFace == null)
            currentFace = Direction.DOWN;

        return currentFace;
    }

    public static void cycleCurrentFace(ItemStack itemStack, ServerPlayer player) {
        int diff = player.isShiftKeyDown()?-1:1;
        Direction currentFace = getCurrentFace(itemStack);
        currentFace = Direction.values()[(currentFace.ordinal() + diff + Direction.values().length) %
                Direction.values().length];
        itemStack.getOrCreateTag().putString("currentFace", currentFace.getSerializedName());

        player.connection.send(new ClientboundSetActionBarTextPacket(
                Component.translatable("tooltip.energizedpower.wrench.select_face",
                        Component.translatable("tooltip.energizedpower.direction." + currentFace.getSerializedName()).
                                withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
                ).withStyle(ChatFormatting.GRAY)
        ));
    }

    public InteractionResult useOnFluidPipe(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState state = level.getBlockState(blockPos);

        ItemStack itemStack = useOnContext.getItemInHand();
        Direction currentFace = getCurrentFace(itemStack);

        BlockPos testPos = blockPos.relative(currentFace);

        Player player = useOnContext.getPlayer();

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(testBlockEntity == null || testBlockEntity instanceof FluidPipeBlockEntity) {
            //Connections to non-fluid blocks nor connections to another pipe can not be modified

            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.wrench.face_change_not_possible",
                                Component.translatable("tooltip.energizedpower.direction." + currentFace.getSerializedName()).
                                        withStyle(ChatFormatting.WHITE)
                        ).withStyle(ChatFormatting.RED)
                ));
            }

            return InteractionResult.SUCCESS;
        }

        LazyOptional<IFluidHandler> fluidStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, currentFace.getOpposite());
        if(!fluidStorageLazyOptional.isPresent()) {
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.wrench.face_change_not_possible",
                                Component.translatable("tooltip.energizedpower.direction." + currentFace.getSerializedName()).
                                        withStyle(ChatFormatting.WHITE)
                        ).withStyle(ChatFormatting.RED)
                ));
            }

            return InteractionResult.SUCCESS;
        }

        IFluidHandler fluidStorage = fluidStorageLazyOptional.orElse(null);
        if(fluidStorage.getTanks() == 0) {
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.wrench.face_change_not_possible",
                                Component.translatable("tooltip.energizedpower.direction." + currentFace.getSerializedName()).
                                        withStyle(ChatFormatting.WHITE)
                        ).withStyle(ChatFormatting.RED)
                ));
            }

            return InteractionResult.SUCCESS;
        }

        EnumProperty<ModBlockStateProperties.PipeConnection> pipeConnectionProperty =
                FluidPipeBlock.getPipeConnectionPropertyFromDirection(currentFace);

        int diff = player != null && player.isShiftKeyDown()?-1:1;

        ModBlockStateProperties.PipeConnection pipeConnection = state.getValue(pipeConnectionProperty);
        pipeConnection = ModBlockStateProperties.PipeConnection.values()[(pipeConnection.ordinal() + diff +
                ModBlockStateProperties.PipeConnection.values().length) %
                ModBlockStateProperties.PipeConnection.values().length];

        level.setBlock(blockPos, state.setValue(pipeConnectionProperty, pipeConnection), 3);

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.wrench.face_change",
                            Component.translatable("tooltip.energizedpower.direction." + currentFace.getSerializedName()).
                                    withStyle(ChatFormatting.WHITE),
                            Component.translatable(pipeConnection.getTranslationKey()).
                                    withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
                    ).withStyle(ChatFormatting.GREEN)
            ));
        }

        return InteractionResult.SUCCESS;
    }

    public InteractionResult useOnConveyorBelt(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState state = level.getBlockState(blockPos);

        Player player = useOnContext.getPlayer();

        ModBlockStateProperties.ConveyorBeltDirection facing = state.getValue(ItemConveyorBeltBlock.FACING);
        Boolean shape;

        if(player != null && player.isShiftKeyDown()) {
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

        level.setBlock(blockPos, state.setValue(ItemConveyorBeltBlock.FACING, ModBlockStateProperties.ConveyorBeltDirection.of(facing.getDirection(), shape)), 3);

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.wrench.change",
                            Component.translatable("tooltip.energizedpower.conveyor_belt_direction.slope." + (shape == null?"flat":(shape?"ascending":"descending"))).
                                    withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
                    ).withStyle(ChatFormatting.GREEN)
            ));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide)
            return InteractionResult.SUCCESS;

        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState state = level.getBlockState(blockPos);
        if(state.is(ModBlocks.FLUID_PIPE.get()) && (level.getBlockEntity(blockPos) instanceof FluidPipeBlockEntity))
            return useOnFluidPipe(useOnContext);

        if(state.is(ModBlocks.ITEM_CONVEYOR_BELT.get()) && (level.getBlockEntity(blockPos) instanceof ItemConveyorBeltBlockEntity))
            return useOnConveyorBelt(useOnContext);

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(level.isClientSide)
            return InteractionResultHolder.success(itemStack);

       cycleCurrentFace(itemStack, (ServerPlayer)player);

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        //Allow current face swap in survival in a reasonable amount of time
        return 1000.f;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos blockPos, Player player) {
        if(level.isClientSide || !(player instanceof ServerPlayer))
            return false;

        ItemStack itemStack = player.getMainHandItem();

        CompoundTag nbt = itemStack.getTag();
        if(nbt != null && nbt.contains("attackingCycleCooldown"))
            return false;

        cycleCurrentFace(itemStack, (ServerPlayer)player);
        itemStack.getOrCreateTag().putInt("attackingCycleCooldown", 5);

        return false;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        Direction currentFace = getCurrentFace(itemStack);
        components.add(Component.translatable("tooltip.energizedpower.wrench.select_face",
                Component.translatable("tooltip.energizedpower.direction." + currentFace.getSerializedName()).
                        withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
        ).withStyle(ChatFormatting.GRAY));

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.wrench.txt.shift").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemStack, level, entity, slot, selected);

        if(level.isClientSide)
            return;

        if(!(entity instanceof Player))
            return;

        CompoundTag nbt = itemStack.getTag();
        if(nbt != null && nbt.contains("attackingCycleCooldown")) {
            int attackingCycleCooldown = nbt.getInt("attackingCycleCooldown") - 1;
            if(attackingCycleCooldown <= 0)
                nbt.remove("attackingCycleCooldown");
            else
                nbt.putInt("attackingCycleCooldown", attackingCycleCooldown);
        }
    }
}
