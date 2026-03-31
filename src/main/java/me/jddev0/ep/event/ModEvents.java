package me.jddev0.ep.event;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.item.EnergizedPowerBookItem;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.OpenEnergizedPowerBookS2CPacket;
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
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;

@EventBusSubscriber(modid = EPAPI.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        handlePlayerLecternInteraction(event);
        if(event.isCanceled())
            return;

        handlePlayerInWorldCraftingInteract(event);
    }

    private static void handlePlayerLecternInteraction(PlayerInteractEvent.RightClickBlock event) {
        BlockPos blockPos = event.getPos();
        BlockEntity blockEntity = event.getLevel().getBlockEntity(blockPos);

        if(!(blockEntity instanceof LecternBlockEntity))
            return;

        LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;

        BlockState blockState = event.getLevel().getBlockState(blockPos);
        if(!blockState.getValue(LecternBlock.HAS_BOOK))
            return;

        ItemStack bookItemStack = lecternBlockEntity.getBook();
        if(!bookItemStack.is(EPItems.ENERGIZED_POWER_BOOK.get()))
            return;

        Item bookItem = bookItemStack.getItem();
        if(!(bookItem instanceof EnergizedPowerBookItem))
            return;

        Player player = event.getEntity();

        if(!event.getLevel().isClientSide())
            ModMessages.sendToPlayer(new OpenEnergizedPowerBookS2CPacket(blockPos), (ServerPlayer)player);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static void handlePlayerInWorldCraftingInteract(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        if(!itemStack.is(Tags.Items.TOOLS_SHEAR))
            return;

        Level level = event.getLevel();

        BlockPos blockPos = event.getPos();
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.is(BlockTags.WOOL))
            return;

        Player player = event.getEntity();

        if(!event.getLevel().isClientSide()) {
            if(!player.isCreative())
                itemStack.hurtAndBreak(1, player,
                        event.getHand() == InteractionHand.MAIN_HAND?EquipmentSlot.MAINHAND:EquipmentSlot.OFFHAND);

            level.destroyBlock(blockPos, false, player);

            ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5,
                    new ItemStack(EPItems.CABLE_INSULATOR.get(), 18), 0, 0, 0);
            itemEntity.setPickUpDelay(20);
            level.addFreshEntity(itemEntity);

            level.playSound(null, blockPos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.f, 1.f);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
