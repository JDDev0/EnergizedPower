package me.jddev0.ep.event;

import me.jddev0.ep.item.EnergizedPowerBookItem;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.networking.packet.OpenEnergizedPowerBookS2CPacket;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class PlayerInteractHandler implements UseBlockCallback {
    @Override
    public InteractionResult interact(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        InteractionResult result = handlePlayerLecternInteraction(player, level, hand, hitResult);
        if(result != InteractionResult.PASS)
            return result;

       return handlePlayerInWorldCraftingInteract(player, level, hand, hitResult);
    }

    private static InteractionResult handlePlayerLecternInteraction(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos blockPos = hitResult.getBlockPos();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);

        if(!(blockEntity instanceof LecternBlockEntity lecternBlockEntity))
            return InteractionResult.PASS;

        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.getValue(LecternBlock.HAS_BOOK))
            return InteractionResult.PASS;

        ItemStack bookItemStack = lecternBlockEntity.getBook();
        if(!bookItemStack.is(EPItems.ENERGIZED_POWER_BOOK))
            return InteractionResult.PASS;

        Item bookItem = bookItemStack.getItem();
        if(!(bookItem instanceof EnergizedPowerBookItem))
            return InteractionResult.PASS;

        if(!level.isClientSide())
            ServerPlayNetworking.send((ServerPlayer)player, new OpenEnergizedPowerBookS2CPacket(blockPos));

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult handlePlayerInWorldCraftingInteract(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(!itemStack.is(ConventionalItemTags.SHEAR_TOOLS))
            return InteractionResult.PASS;

        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.is(BlockTags.WOOL))
            return InteractionResult.PASS;

        if(!level.isClientSide()) {
            if(!player.isCreative())
                itemStack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND?EquipmentSlot.MAINHAND:EquipmentSlot.OFFHAND);

            level.destroyBlock(blockPos, false, player);

            ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5,
                    new ItemStack(EPItems.CABLE_INSULATOR, 18), 0, 0, 0);
            itemEntity.setPickUpDelay(20);
            level.addFreshEntity(itemEntity);

            level.playSound(null, blockPos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.f, 1.f);
        }

        return InteractionResult.SUCCESS;
    }
}
