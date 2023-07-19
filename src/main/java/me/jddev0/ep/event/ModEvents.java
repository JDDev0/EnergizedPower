package me.jddev0.ep.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.EnergizedPowerBookItem;
import me.jddev0.ep.item.EnergyAnalyzerItem;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.OpenEnergizedPowerBookS2CPacket;
import me.jddev0.ep.villager.ModVillager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.IntTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = EnergizedPowerMod.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        if(event.getType() == ModVillager.ELECTRICIAN_PROFESSION.get()) {
            //Level 1
            addOffer(trades, 1,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(Items.BOOK),
                    new ItemStack(ModItems.ENERGIZED_POWER_BOOK.get()),
                    3, 3, .02f);
            addOffer(trades, 1,
                    new ItemStack(Items.COPPER_INGOT, 2),
                    new ItemStack(Items.EMERALD, 1),
                    25, 1, .02f);
            addOffer(trades, 1,
                    new ItemStack(ModItems.SILICON.get(), 3),
                    new ItemStack(Items.EMERALD, 2),
                    15, 2, .02f);

            //Level 2
            addOffer(trades, 2,
                    new ItemStack(ModItems.BASIC_SOLAR_CELL.get(), 2),
                    new ItemStack(Items.EMERALD, 3),
                    5, 5, .02f);
            {

                ItemStack energyAnalyzer = new ItemStack(ModItems.BATTERY_2.get());
                energyAnalyzer.getOrCreateTag().put("energy", IntTag.valueOf(128));

                addOffer(trades, 2,
                        new ItemStack(Items.EMERALD, 6),
                        new ItemStack(Items.COPPER_INGOT, 4),
                        energyAnalyzer,
                        3, 8, .02f);
            }

            //Level 3
            addOffer(trades, 3,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get(), 1),
                    new ItemStack(ModBlocks.AUTO_CRAFTER_ITEM.get(), 1),
                    5, 10, .02f);
            {

                ItemStack energyAnalyzer = new ItemStack(ModItems.ENERGY_ANALYZER.get());
                energyAnalyzer.getOrCreateTag().put("energy", IntTag.valueOf(16 * EnergyAnalyzerItem.ENERGY_CONSUMPTION_PER_USE));

                addOffer(trades, 3,
                        new ItemStack(Items.EMERALD, 16),
                        new ItemStack(Items.COPPER_INGOT, 12),
                        energyAnalyzer,
                        1, 12, .02f);
            }

            //Level 4
            addOffer(trades, 4,
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(ModItems.BASIC_SOLAR_CELL.get(), 2),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_1.get(), 1),
                    3, 19, .02f);
            addOffer(trades, 4,
                    new ItemStack(Items.EMERALD, 14),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get(), 1),
                    new ItemStack(ModBlocks.COAL_ENGINE_ITEM.get(), 1),
                    2, 24, .02f);

            //Level 5
            addOffer(trades, 5,
                    new ItemStack(Items.EMERALD, 59),
                    new ItemStack(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM.get(), 1),
                    new ItemStack(ModBlocks.LIGHTNING_GENERATOR_ITEM.get(), 1),
                    1, 30, .02f);
            addOffer(trades, 5,
                    new ItemStack(Items.EMERALD, 24),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_1.get(), 2),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_2.get(), 1),
                    3, 41, .02f);
        }
    }

    private static void addOffer(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int minLevel,
                                 ItemStack cost, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        trades.get(minLevel).add((trader, rand) -> new MerchantOffer(cost, result, maxUses, xp, priceMultiplier));
    }

    private static void addOffer(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int minLevel,
                                 ItemStack costA, ItemStack costB, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        trades.get(minLevel).add((trader, rand) -> new MerchantOffer(costA, costB, result, maxUses, xp, priceMultiplier));
    }

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
        if(!bookItemStack.is(ModItems.ENERGIZED_POWER_BOOK.get()))
            return;

        Item bookItem = bookItemStack.getItem();
        if(!(bookItem instanceof EnergizedPowerBookItem))
            return;

        Player player = event.getEntity();

        if(!event.getLevel().isClientSide)
            ModMessages.sendToPlayer(new OpenEnergizedPowerBookS2CPacket(blockPos), (ServerPlayer)player);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static void handlePlayerInWorldCraftingInteract(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        if(!itemStack.is(Tags.Items.SHEARS))
            return;

        Level level = event.getLevel();

        BlockPos blockPos = event.getPos();
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.is(BlockTags.WOOL))
            return;

        Player player = event.getEntity();

        if(!event.getLevel().isClientSide) {
            if(!player.isCreative())
                itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));

            level.destroyBlock(blockPos, false, player);

            ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5,
                    new ItemStack(ModItems.CABLE_INSULATOR.get(), 18), 0, 0, 0);
            itemEntity.setPickUpDelay(20);
            level.addFreshEntity(itemEntity);

            level.playSound(null, blockPos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.f, 1.f);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
