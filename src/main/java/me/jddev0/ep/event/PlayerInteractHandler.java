package me.jddev0.ep.event;

import me.jddev0.ep.item.EnergizedPowerBookItem;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerInteractHandler implements UseBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World level, Hand hand, BlockHitResult hitResult) {
        ActionResult result = handlePlayerLecternInteraction(player, level, hand, hitResult);
        if(result != ActionResult.PASS)
            return result;

       return handlePlayerInWorldCraftingInteract(player, level, hand, hitResult);
    }

    private static ActionResult handlePlayerLecternInteraction(PlayerEntity player, World level, Hand hand, BlockHitResult hitResult) {
        BlockPos blockPos = hitResult.getBlockPos();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);

        if(!(blockEntity instanceof LecternBlockEntity lecternBlockEntity))
            return ActionResult.PASS;

        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.get(LecternBlock.HAS_BOOK))
            return ActionResult.PASS;

        ItemStack bookItemStack = lecternBlockEntity.getBook();
        if(!bookItemStack.isOf(ModItems.ENERGIZED_POWER_BOOK))
            return ActionResult.PASS;

        Item bookItem = bookItemStack.getItem();
        if(!(bookItem instanceof EnergizedPowerBookItem))
            return ActionResult.PASS;

        if(!level.isClient())
            ServerPlayNetworking.send((ServerPlayerEntity)player, ModMessages.OPEN_ENERGIZED_POWER_BOOK_ID, PacketByteBufs.create().writeBlockPos(blockPos));

        return ActionResult.SUCCESS;
    }

    private static ActionResult handlePlayerInWorldCraftingInteract(PlayerEntity player, World level, Hand hand, BlockHitResult hitResult) {
        ItemStack itemStack = player.getStackInHand(hand);
        if(!itemStack.isIn(CommonItemTags.SHEARS))
            return ActionResult.PASS;

        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.isIn(BlockTags.WOOL))
            return ActionResult.PASS;

        if(!level.isClient()) {
            if(!player.isCreative())
                itemStack.damage(1, player, p -> p.sendToolBreakStatus(hand));

            level.breakBlock(blockPos, false, player);

            ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5,
                    new ItemStack(ModItems.CABLE_INSULATOR, 18), 0, 0, 0);
            itemEntity.setPickupDelay(20);
            level.spawnEntity(itemEntity);

            level.playSound(null, blockPos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.f, 1.f);
        }

        return ActionResult.SUCCESS;
    }
}
